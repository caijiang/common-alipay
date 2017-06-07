package me.jiangcai.alipay.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author CJ
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeCreate {
    /**
     * 商户订单号
     */
    @JsonProperty("out_trade_no")
    private String tradeId;
    /**
     * 支付宝交易号
     */
    @JsonProperty("trade_no")
    private String alipayTradeId;
}
