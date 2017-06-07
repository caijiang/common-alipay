package me.jiangcai.alipay.http;

import com.fasterxml.jackson.databind.JsonNode;
import me.jiangcai.alipay.AppInfo;
import me.jiangcai.alipay.exception.AlipayException;
import me.jiangcai.alipay.response.GlobalResponse;
import me.jiangcai.alipay.service.AlipayServiceImpl;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
public class ResponseHandler<T> extends AbstractResponseHandler<T> {

    private final Class<T> type;
    private final AppInfo app;

    public ResponseHandler(Class<T> type, AppInfo appInfo) {
        this.type = type;
        this.app = appInfo;
    }

    @Override
    public T handleEntity(HttpEntity entity) throws IOException {
        String allJson = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        JsonNode json = AlipayServiceImpl.objectMapper.readTree(allJson);
        String responseJson = checkOutResponse(allJson);

        // 第一步验签
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(app.getAlipayPublicKey());
            signature.update(responseJson.getBytes(Charset.forName("UTF-8")));
            final String sign = json.get("sign").asText();
            if (!signature.verify(Base64.getDecoder().decode(sign)))
                throw new IOException("验证支付宝同步签名失败:" + sign);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IllegalStateException(e);
        }
        // 第二步抛出异常
        final Iterator<String> fieldNames = json.fieldNames();
        String responseName = null;
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            if (name.endsWith("_response")) {
                responseName = name;
                break;
            }
        }
        if (responseName == null)
            throw new IOException("没有找到响应块");
        final JsonNode responseJsonObject = json.get(responseName);
        GlobalResponse globalResponse = AlipayServiceImpl.objectMapper.readerFor(GlobalResponse.class)
                .readValue(responseJsonObject);
        if (globalResponse.getCode() != 10000) {
            throw new AlipayException(globalResponse);
        }
        // 第三步业务结果
        return AlipayServiceImpl.objectMapper.readerFor(type).readValue(responseJsonObject);
    }

    private String checkOutResponse(String json) {
        // 1 情况 response 先  _response":{   },"sign"
        // 2 情况 sign先 "sign":{  },"*_response"
        Pattern s1p1 = Pattern.compile("_response\":\\{");
        Pattern s1p2 = Pattern.compile("},\"sign\"");

        Matcher matcher1 = s1p1.matcher(json);
        Matcher matcher2 = s1p2.matcher(json);

        String rs = checkMatcher(json, matcher1, matcher2);
        if (rs != null)
            return rs;

//        Pattern s2p1 = Pattern.compile("\"sign\":\\{");
        Pattern s2p2 = Pattern.compile("}}");

        matcher1 = s1p1.matcher(json);
        matcher2 = s2p2.matcher(json);

        return checkMatcher(json, matcher1, matcher2);
    }

    private String checkMatcher(String json, Matcher matcher1, Matcher matcher2) {
        if (matcher1.find() && matcher2.find()) {
            int start = matcher1.end();
            int end = matcher2.start();
            // end 得最后一个
//            while (matcher2.find(end+1))
//                end = matcher2.start();
            if (end > start) {
                // 对的
                // json.substring(start-1,end+1)
                return json.substring(start - 1, end + 1);
            }
        }
        return null;
    }
}
