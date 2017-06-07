package me.jiangcai.alipay;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 载入该Spring配置可以获得支付宝付款能力
 * 可以提供{@link AlipayService}
 * 因为{@link AlipayService}的appInfo参数是支持可选的
 * 所以我们需要系统系统以下默认的支付宝参数
 * <ul>
 * <li>可选的 me.jiangcai.alipay.sandbox 如果存在就表示处于支付宝沙盒测试</li>
 * <li>me.jiangcai.alipay.default.appId 默认的appId</li>
 * <li>me.jiangcai.alipay.default.notifyUrl 默认的通知地址</li>
 * <li>me.jiangcai.alipay.default.sellId 默认的卖家id</li>
 * <li>me.jiangcai.alipay.default.privateKey 默认的卖家私钥</li>
 * <li>me.jiangcai.alipay.default.alipayPublicKey 默认的支付宝平台公钥</li>
 * </ul>
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.alipay.service")
public class AlipaySpringConfig {
}
