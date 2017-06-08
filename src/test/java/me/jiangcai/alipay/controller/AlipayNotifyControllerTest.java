package me.jiangcai.alipay.controller;

import me.jiangcai.alipay.AlipayTestConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.springframework.test.web.servlet.MvcResult;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Created by luohaibo on 2017/6/8.
 */
@WebAppConfiguration
@ContextConfiguration(classes = AlipayTestConfig.class)
public class AlipayNotifyControllerTest extends SpringWebTest {


    @Test
    public void alipayPayNotifyController() throws Exception {

        mockMvc.perform(
            post("/alipayCallBack")
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .content(readContent())
        )
                .andExpect(
                content().string("success")
        );
    }


    private byte[] readContent() throws IOException, URISyntaxException {
        URI fileUri = getClass().getResource("/success_alipay_notify_content").toURI();
        return Files.readAllLines(Paths.get(fileUri), Charset.forName("UTF-8"))
                .stream()
                .collect(Collectors.joining())
        .getBytes("UTF-8");
    }

}