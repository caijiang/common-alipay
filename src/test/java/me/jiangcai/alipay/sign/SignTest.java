package me.jiangcai.alipay.sign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.alipay.RSA;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author CJ
 */
public class SignTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void fuck() throws JsonProcessingException {
        Map map = new HashMap();
        map.put("a","123");
        map.put("hi",1);
        Map map2 = new HashMap();
        map2.put("foo","bar");
        map.put("object",map2);
        System.out.println(objectMapper.writeValueAsString(map));
    }

    @Test
    public void go() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");


        PrivateKey privateKey = RSA.readPrivateKeyFromPEM(new ClassPathResource("/rsa_private_pkcs8.pem").getInputStream());
        signature.initSign(privateKey);

        signature.update("a=123".getBytes("UTF-8"));
        byte[] signed = signature.sign();
        System.out.println(Base64.getEncoder().encodeToString(signed));
//        System.out.println(bytesToHex(signed).toLowerCase(Locale.CHINA));

        // 自个验证。。。

        Signature signature1 = Signature.getInstance("SHA256withRSA");
        signature1.initVerify(RSA.readPublicKeyFromPEM(new ClassPathResource("/rsa_public.pem").getInputStream()));
        signature1.update("a=123".getBytes());
        assert signature1.verify(signed);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
