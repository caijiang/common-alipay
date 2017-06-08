package me.jiangcai.alipay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertThat;

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AlipayTestConfig.class)
public class AlipayServiceTest {

    private final Random random = new Random();

    @Autowired
    private AlipayService alipayService;

    @Test
    public void createTrade() throws Exception {
        ChargeTrade chargeTrade = alipayService.createTrade(null, UUID.randomUUID().toString()
                , new BigDecimal((double) random.nextInt(100) + random.nextDouble()), "我系订单啊");
    }

    @Test
    public void pcPagePayTest() throws Exception{

       ResponseEntity<?> responseEntity =  alipayService.createDesktopPageTrade(null,UUID.randomUUID().toString(),new BigDecimal((double) random.nextInt(100) + random.nextDouble()),"test");
       // assertThat(responseEntity.getHeaders().get);

        System.out.println(responseEntity.getHeaders().getLocation());
        Desktop.getDesktop().browse(responseEntity.getHeaders().getLocation());

    }

}