package me.jiangcai.alipay;

import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author CJ
 */
@Data
public class AppInfo {
    /**
     * 必须的app_id
     */
    private String id;
    /**
     * 通知地址；可选
     */
    private String notifyUrl;

    private String sellerId;

    private PrivateKey privateKey;
    private PublicKey alipayPublicKey;
}
