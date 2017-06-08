package me.jiangcai.alipay.event;

import lombok.Data;
import me.jiangcai.alipay.TradeStatus;

/**
 * Created by luohaibo on 2017/6/8.
 */
@Data
public class TradeChangeEvent {
    /**
     * 商户系统的唯一订单号
     */
    private final String tradeId;
    /**
     * 支付宝交易号
     */
    private final String alipayTradeId;
    private  final TradeStatus status;

}
