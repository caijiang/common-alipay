package me.jiangcai.alipay;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
public class RSA {


    /**
     * 从base64风格的字符串中获取密钥
     *
     * @param base64
     * @return
     */
    public static PrivateKey readPrivateKeyFromString(String base64) throws InvalidKeySpecException {
        try {
            // 不是PEM 要去头去尾
            final byte[] decode = Base64.getDecoder().decode(base64);

            return KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(decode));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从数据流中读取一个PEM格式，并且依次构造RSA私钥
     *
     * @param inputStream 数据流
     * @return 私钥
     * @throws InvalidKeySpecException 文件不合法
     */
    public static PrivateKey readPrivateKeyFromPEM(InputStream inputStream) throws InvalidKeySpecException {
        try {
            // 不是PEM 要去头去尾
            final byte[] decode = readKeyFromPEM(inputStream);

            return KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(decode));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readKeyFromPEM(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String base64 = reader.lines()
                .filter(str -> !str.startsWith("-----"))
                .filter(str -> !str.endsWith("-----"))
                .collect(Collectors.joining());
        return Base64.getDecoder().decode(base64);
    }

    public static PublicKey readPublicKeyFromPEM(InputStream inputStream) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(readKeyFromPEM(inputStream)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey readPublicKeyFromString(String base64) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
