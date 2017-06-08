package me.jiangcai.alipay.controller;

import me.jiangcai.alipay.AlipayService;
import me.jiangcai.alipay.AppInfo;
import me.jiangcai.alipay.RSA;
import me.jiangcai.alipay.TradeStatus;
import me.jiangcai.alipay.event.TradeChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luohaibo on 2017/6/8.
 */

@Controller
public class AlipayPayNotifyController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private AlipayService alipayService;

    @RequestMapping(value = "/alipayCallBack")
    @ResponseBody
    public String alipayPayNotifyController(HttpServletRequest request,String sign) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {

        //1、获取回调参数
        Map<String,String[]> requestMap = request.getParameterMap();

        //第一步： 在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数
        requestMap.remove("sign");
        requestMap.remove("signType");

        //第二步： 将剩下参数进行url_decode, 然后进行字典排序，组成字符串，得到待签名字符串
        String beforeSign = requestMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(pair -> pair.getKey()+"="+pair.getValue()[0])
                .collect(Collectors.joining("&"));

        //第三步： 将签名参数（sign）使用base64解码为字节码串。
        byte[] signByte = Base64.getDecoder().decode(sign);

        AppInfo appInfo = alipayService.defaultAppInfo();
        Signature signature1 = Signature.getInstance("SHA256withRSA");
        signature1.initVerify(appInfo.getAlipayPublicKey());
        signature1.update(beforeSign.getBytes("UTF-8"));

        assert signature1.verify(signByte);
        String tradeStatus = requestMap.get("trade_status")[0];
        if(tradeStatus.compareTo("TRADE_SUCCESS") == 0){
            applicationEventPublisher.publishEvent(new TradeChangeEvent(requestMap.get("out_trade_no")[0],requestMap.get("trade_no")[0],TradeStatus.success));
        }
        return "success";

    }
}
