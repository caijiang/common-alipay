package me.jiangcai.alipay.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author CJ
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalResponse {
    private int code;
    @JsonProperty("msg")
    private String message;
    @JsonProperty("sub_code")
    private String businessCode;
    @JsonProperty("sub_msg")
    private String businessMessage;
}
