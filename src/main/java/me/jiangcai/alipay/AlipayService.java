package me.jiangcai.alipay;

import me.jiangcai.alipay.exception.AlipayException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;

/**
 * 支付宝付款服务
 *
 * @author CJ
 */
public interface AlipayService {

    /**
     * 最简单版本的收款订单建立
     *
     * @param appInfo 可选的appInfo
     * @param tradeId 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
     * @param amount 订单总金额，单位为元，精确到小数点后两位
     * @param subject 标题
     * @return 成功创建的支付宝付款订单
     * @throws IOException 在执行时网络层发生故障
     * @throws AlipayException 支付宝系统返回的错误
     * @throws InvalidKeyException 如果给予或者默认的密钥不支持特定的签名算法
     */
    ChargeTrade createTrade(AppInfo appInfo, String tradeId, BigDecimal amount, String subject) throws IOException
            , AlipayException, InvalidKeyException;


    /**
     * 创建pc端网页支付
     * @param appInfo 可选的appInfo
     * @param tradeId 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
     * @param amount  订单总金额，单位为元，精确到小数点后两位
     * @param subject  标题
     * @return
     * @throws IOException
     * @throws AlipayException
     * @throws InvalidKeyException
     */
    ResponseEntity<?> createPcPagePay(AppInfo appInfo, String tradeId, BigDecimal amount, String subject) throws IOException
            , AlipayException, InvalidKeyException;



    /**
     *
     * @return 默认的appInfo
     */
    AppInfo defaultAppInfo();
}
