package me.jiangcai.alipay.exception;

import me.jiangcai.alipay.response.GlobalResponse;

/**
 * 支付宝对接系统响应的异常
 *
 * @author CJ
 */
public class AlipayException extends RuntimeException {

    private final GlobalResponse response;

    public AlipayException(GlobalResponse globalResponse) {
        this.response = globalResponse;
    }

    @Override
    public String getMessage() {
        if (response != null)
            return response.toString();
        return super.getMessage();
    }
}
