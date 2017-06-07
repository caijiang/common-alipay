package me.jiangcai.alipay;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author CJ
 */
@Import(AlipaySpringConfig.class)
@Configuration
@PropertySource("classpath:/test_system.properties")
public class AlipayTestConfig {
}
