package me.jiangcai.alipay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.alipay.AlipayService;
import me.jiangcai.alipay.AppInfo;
import me.jiangcai.alipay.ChargeTrade;
import me.jiangcai.alipay.RSA;
import me.jiangcai.alipay.exception.AlipayException;
import me.jiangcai.alipay.http.ResponseHandler;
import me.jiangcai.alipay.response.TradeCreate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    private static final Log log = LogFactory.getLog(AlipayServiceImpl.class);
    @Autowired
    private Environment environment;

    @Override
    public ChargeTrade createTrade(AppInfo appInfo, String tradeId, BigDecimal amount, String subject)
            throws IOException, AlipayException, InvalidKeyException {
        appInfo = buildAppInfo(appInfo);


        //保留2位小数
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

        try (CloseableHttpClient client = createHttpClient()) {
            HttpGet get = new HttpGet(gatewayUri());
            HttpPost post = new HttpPost(gatewayUri());
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("out_trade_no", tradeId);
            requestData.put("total_amount", amount);
            requestData.put("subject", subject);
            requestData.put("product_code", "FAST_INSTANT_TRADE_PAY");
            requestData.put("body", subject);
            requestData.put("passback_params", "merchantBizType%3d3C%26merchantBizNo%3d2016010101111");
            requestData.put("timeout_express", "90m");

            if (!StringUtils.isEmpty(appInfo.getSellerId())) {
                requestData.put("seller_id", appInfo.getSellerId());
            }

            HttpEntity entity =  createEntityFor(appInfo, "alipay.trade.create", requestData);
            post.setEntity(
                    entity
            );

            TradeCreate tradeCreate = client.execute(post, new ResponseHandler<>(TradeCreate.class, appInfo));
            System.out.println(tradeCreate);
        }

        return null;
    }

    @Override
    public ResponseEntity<?> createDesktopPageTrade(AppInfo appInfo, String tradeId, BigDecimal amount, String subject) throws IOException, AlipayException, InvalidKeyException {

        appInfo = buildAppInfo(appInfo);
        //保留2位小数
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost post = new HttpPost(gatewayUri());
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("out_trade_no", tradeId);
            requestData.put("total_amount", amount);
            requestData.put("subject", subject);
            requestData.put("product_code", "FAST_INSTANT_TRADE_PAY");
//            requestData.put("body", subject);
//            requestData.put("passback_params", "merchantBizType%3d3C%26merchantBizNo%3d2016010101111");
//            requestData.put("timeout_express", "90m");

            if (!StringUtils.isEmpty(appInfo.getSellerId())) {
                requestData.put("seller_id", appInfo.getSellerId());
            }

            HttpEntity entity =  createEntityFor(appInfo, "alipay.trade.page.pay", requestData);
            post.setEntity(
                    entity
            );
            try {
                return ResponseEntity
                        .status(HttpStatus.SC_MOVED_TEMPORARILY)
                        .location(new URI(client.execute(post).getFirstHeader("location").getValue()))
   //                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                        .build()
                ;
            } catch (URISyntaxException e) {
                throw new AlipayException(e);
            }
        }
    }



    public static final ObjectMapper objectMapper = new ObjectMapper();
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private HttpEntity createEntityFor(AppInfo appInfo, String requestMethod, Map<String, Object> requestData) throws InvalidKeyException {
        List<NameValuePair> parameters = new ArrayList<>();

        if (!requestData.containsKey("extend_params")) {
            requestData.put("extend_params", Collections.emptyMap());
        }

        parameters.add(new BasicNameValuePair("app_id", appInfo.getId()));
        parameters.add(new BasicNameValuePair("method", requestMethod));
        parameters.add(new BasicNameValuePair("charset", "utf-8"));
        parameters.add(new BasicNameValuePair("format", "JSON"));
        parameters.add(new BasicNameValuePair("sign_type", "RSA2"));
        parameters.add(new BasicNameValuePair("timestamp", LocalDateTime.now().format(formatter)));
        parameters.add(new BasicNameValuePair("version", "1.0"));
        if (!StringUtils.isEmpty(appInfo.getNotifyUrl()))
            parameters.add(new BasicNameValuePair("notify_url", appInfo.getNotifyUrl()));
        try {
            parameters.add(new BasicNameValuePair("biz_content", objectMapper.writeValueAsString(requestData)));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

        sign(parameters, appInfo);

        return EntityBuilder.create()
                .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset(Charset.forName("UTF-8")))
                .setParameters(parameters)
                .build();
    }

    private void sign(List<NameValuePair> parameters, AppInfo appInfo) throws InvalidKeyException {
        String beforeSign = parameters.stream()
                .sorted(Comparator.comparing(NameValuePair::getName))
                .map(pair -> pair.getName() + "=" + pair.getValue())
                .collect(Collectors.joining("&"));
        log.debug("[ALIPAY] before sign:" + beforeSign);

        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(appInfo.getPrivateKey());
            signature.update(beforeSign.getBytes(Charset.forName("UTF-8")));
            parameters.add(new BasicNameValuePair("sign", Base64.getEncoder().encodeToString(signature.sign())));
        } catch (NoSuchAlgorithmException | SignatureException e) {
            throw new IllegalStateException(e);
        }
    }



    private String gatewayUri() {
        if (environment.containsProperty("me.jiangcai.alipay.sandbox"))
            return "https://openapi.alipaydev.com/gateway.do";
        return "https://openapi.alipay.com/gateway.do";
    }

    private CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create()
                // TODO 更多安全选项
                .build();
    }

    private AppInfo buildAppInfo(AppInfo appInfo) {
        if (appInfo == null) {
            return defaultAppInfo();
        }
        AppInfo app = new AppInfo();
        app.setId(StringUtils.isEmpty(appInfo.getId()) ?
                environment.getRequiredProperty("me.jiangcai.alipay.default.appId") : appInfo.getId());
        app.setSellerId(StringUtils.isEmpty(appInfo.getSellerId()) ?
                environment.getProperty("me.jiangcai.alipay.default.sellId") : appInfo.getSellerId());
        app.setNotifyUrl(StringUtils.isEmpty(appInfo.getNotifyUrl()) ?
                environment.getProperty("me.jiangcai.alipay.default.notifyUrl") : appInfo.getNotifyUrl());

        try {
            app.setPrivateKey(appInfo.getPrivateKey() == null ?
                    RSA.readPrivateKeyFromString(environment.getRequiredProperty("me.jiangcai.alipay.default.privateKey"))
                    : appInfo.getPrivateKey());
            app.setAlipayPublicKey(appInfo.getAlipayPublicKey() == null ?
                    RSA.readPublicKeyFromString(environment.getRequiredProperty("me.jiangcai.alipay.default.alipayPublicKey"))
                    : appInfo.getAlipayPublicKey());
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }

        return app;
    }

    private AppInfo defaultAppInfo;

    @Override
    public AppInfo defaultAppInfo() {
        if (defaultAppInfo != null)
            return defaultAppInfo;
        defaultAppInfo = new AppInfo();
        defaultAppInfo.setId(environment.getRequiredProperty("me.jiangcai.alipay.default.appId"));
        defaultAppInfo.setSellerId(environment.getProperty("me.jiangcai.alipay.default.sellId"));
        defaultAppInfo.setNotifyUrl(environment.getProperty("me.jiangcai.alipay.default.notifyUrl"));

        try {
            defaultAppInfo.setPrivateKey(RSA.readPrivateKeyFromString(environment.getRequiredProperty("me.jiangcai.alipay.default.privateKey")));
            defaultAppInfo.setAlipayPublicKey(RSA.readPublicKeyFromString(environment.getRequiredProperty("me.jiangcai.alipay.default.alipayPublicKey")));
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }

        return defaultAppInfo;
    }
}
