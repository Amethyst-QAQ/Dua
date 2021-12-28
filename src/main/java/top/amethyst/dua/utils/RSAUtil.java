package top.amethyst.dua.utils;

import com.google.gson.JsonObject;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IJsonSerializable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 一些用于RSA加解密的相关工具
 */
public class RSAUtil
{
    private static KeyPairGenerator keyPairGenerator;
    static
    {
        try
        {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024, new SecureRandom());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 生成一对公私钥
     * @return 以Java对象形式的公私钥
     */
    public static KeyPair genKeyPair()
    {
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 把密钥转换为字符串
     */
    public static String keyToString(Key key) throws InvalidKeyException
    {
        if(!(key instanceof RSAKey))
            throw new InvalidKeyException("Not an RSA key!");
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 把字符串转换为公钥
     */
    public static @Nullable RSAPublicKey getPublicKey(String str)
    {
        try
        {
            return  (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(str)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把字符串转换为私钥
     */
    public static @Nullable RSAPrivateKey getPrivateKey(String str)
    {
        try
        {
            return  (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(Base64.getDecoder().decode(str)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成一对公私钥
     * @return 以字符串对形式的公私钥，其中key为公钥，value为私钥
     */
    public static Pair<String, String> genKeyPairString()
    {
        KeyPair pair = genKeyPair();
        try
        {
            String publicKey = keyToString(pair.getPublic());
            String privateKey = keyToString(pair.getPrivate());
            return new Pair<>(publicKey, privateKey);
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return new Pair<>("", "");
    }

    /**
     * RSA加密
     */
    public static String encrypt(@NotNull String str, RSAPublicKey key) throws InvalidKeyException
    {
        String result = "";
        try
        {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            result = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * RSA加密
     */
    public static String encrypt(@NotNull IJsonSerializable obj, RSAPublicKey key) throws InvalidKeyException
    {
        return encrypt(obj.serialize().toString(), key);
    }

    /**
     * RSA解密
     */
    public static @NotNull String decrypt(String str, RSAPrivateKey key) throws InvalidKeyException
    {
        try
        {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * RSA解密并反序列化为对象
     * @param targetClass 目标类必须具有反序列化构造函数
     * @see IJsonSerializable
     */
    public static @NotNull IJsonSerializable decrypt(String str, RSAPrivateKey key, Class<? extends IJsonSerializable> targetClass) throws InvalidKeyException
    {
        return JsonUtil.deserialize(JsonUtil.GSON.fromJson(decrypt(str, key), JsonObject.class), targetClass);
    }

    /**
     * 数字签名
     */
    public static String sign(@NotNull String str, RSAPrivateKey key) throws InvalidKeyException
    {
        String result = "";
        try
        {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(key);
            signature.update(str.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(signature.sign());
        } catch (NoSuchAlgorithmException | SignatureException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 数字签名
     */
    public static String sign(@NotNull IJsonSerializable obj, RSAPrivateKey key) throws InvalidKeyException
    {
        return sign(obj.serialize().toString(), key);
    }

    /**
     * 验证数字签名
     */
    public static boolean verify(@NotNull String str, String sign, RSAPublicKey key) throws InvalidKeyException
    {
        try
        {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(key);
            signature.update(str.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | SignatureException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 验证数字签名
     */
    public static boolean verify(@NotNull IJsonSerializable obj, String sign, RSAPublicKey key) throws InvalidKeyException
    {
        return verify(obj.serialize().toString(), sign, key);
    }
}
