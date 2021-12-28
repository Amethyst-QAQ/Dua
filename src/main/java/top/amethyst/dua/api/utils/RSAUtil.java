package top.amethyst.dua.api.utils;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IJsonSerializable;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * 一些用于RSA加解密的相关工具
 */
public interface RSAUtil
{
    /**
     * 生成一对公私钥
     * @return 以Java对象形式的公私钥
     */
    KeyPair genKeyPair();

    /**
     * 把密钥转换为字符串
     */
    String keyToString(Key key) throws InvalidKeyException;

    /**
     * 把字符串转换为公钥
     */
    @Nullable RSAPublicKey getPublicKey(String str);

    /**
     * 把字符串转换为私钥
     */
    @Nullable RSAPrivateKey getPrivateKey(String str);

    /**
     * 生成一对公私钥
     * @return 以字符串对形式的公私钥，其中key为公钥，value为私钥
     */
    Pair<String, String> genKeyPairString();

    /**
     * RSA加密
     */
    String encrypt(@NotNull String str, RSAPublicKey key) throws InvalidKeyException;

    /**
     * RSA加密
     */
    String encrypt(@NotNull IJsonSerializable obj, RSAPublicKey key) throws InvalidKeyException;

    /**
     * RSA解密
     */
    @NotNull String decrypt(String str, RSAPrivateKey key) throws InvalidKeyException;

    /**
     * RSA解密并反序列化为对象
     * @param targetClass 目标类必须具有反序列化构造函数
     * @see IJsonSerializable
     */
    @NotNull IJsonSerializable decrypt(String str, RSAPrivateKey key, Class<? extends IJsonSerializable> targetClass) throws InvalidKeyException;

    /**
     * 数字签名
     */
    String sign(@NotNull String str, RSAPrivateKey key) throws InvalidKeyException;

    /**
     * 数字签名
     */
    String sign(@NotNull IJsonSerializable obj, RSAPrivateKey key) throws InvalidKeyException;

    /**
     * 验证数字签名
     */
    boolean verify(@NotNull String str, String sign, RSAPublicKey key) throws InvalidKeyException;

    /**
     * 验证数字签名
     */
    boolean verify(@NotNull IJsonSerializable obj, String sign, RSAPublicKey key) throws InvalidKeyException;
}
