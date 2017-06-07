package me.jiangcai.alipay.http;

import me.jiangcai.alipay.AlipayService;
import me.jiangcai.alipay.AlipayTestConfig;
import me.jiangcai.alipay.response.TradeCreate;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AlipayTestConfig.class)
public class ResponseHandlerTest {

    private ResponseHandler<TradeCreate> handler;
    @Autowired
    private AlipayService alipayService;

    @Before
    public void before() {
        handler = new ResponseHandler<>(TradeCreate.class, alipayService.defaultAppInfo());
    }

    @Test
    public void handleEntity() throws Exception {

        handler.handleEntity(EntityBuilder.create()
                .setContentEncoding("UTF-8")
                .setBinary("{\"sign\":\"CzAtlKLCDCEnKoNe0TmWSy+tBDi1+2113lBfH8io7EDDY9fmq5ysepnujKQZ5Z2sLHq7yV37LaGUwqdZsipFqiICsuAri8RnkQNYlyzvYLLLl9WjGLLfIdWd9v7mOyh03WqSizqj/6Ll5iLHQW+CXPC/wtpssFh+4B/QZaZWXyZodGE2L8/1NSIbXQKC7tJJKiFpPgN9TuunErXhY8bQqqxiFq4HSj+fOPSbQij7mXBG9cwd/yfPK/+C7dBi/iVSVRvM/YRoKWKqr3LIiCxpXuFDcKqd734eoESqpM3Kq9XCxRtoVUN3mLngB+JnRnsfAxI7MbTgFkdq8RrFoKneRQ==\",\"alipay_trade_create_response\":{\"code\":\"40004\",\"msg\":\"Business Failed\",\"sub_code\":\"ACQ.INVALID_PARAMETER\",\"sub_msg\":\"参数无效\"}}".getBytes(Charset.forName("UTF-8")))
                .build());

        TradeCreate tradeCreate = handler.handleEntity(EntityBuilder.create()
                .setContentEncoding("UTF-8")
                .setBinary("{\"alipay_trade_create_response\":{\"code\":\"40004\",\"msg\":\"Business Failed\",\"sub_code\":\"ACQ.INVALID_PARAMETER\",\"sub_msg\":\"参数无效\"},\"sign\":\"CzAtlKLCDCEnKoNe0TmWSy+tBDi1+2113lBfH8io7EDDY9fmq5ysepnujKQZ5Z2sLHq7yV37LaGUwqdZsipFqiICsuAri8RnkQNYlyzvYLLLl9WjGLLfIdWd9v7mOyh03WqSizqj/6Ll5iLHQW+CXPC/wtpssFh+4B/QZaZWXyZodGE2L8/1NSIbXQKC7tJJKiFpPgN9TuunErXhY8bQqqxiFq4HSj+fOPSbQij7mXBG9cwd/yfPK/+C7dBi/iVSVRvM/YRoKWKqr3LIiCxpXuFDcKqd734eoESqpM3Kq9XCxRtoVUN3mLngB+JnRnsfAxI7MbTgFkdq8RrFoKneRQ==\"}".getBytes(Charset.forName("UTF-8")))
                .build());
    }

}