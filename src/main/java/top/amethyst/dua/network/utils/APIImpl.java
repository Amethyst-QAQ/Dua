package top.amethyst.dua.network.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IJsonSerializable;
import top.amethyst.dua.api.utils.*;
import top.amethyst.dua.api.utils.AlgorithmUtil;
import top.amethyst.dua.api.utils.JsonUtil;
import top.amethyst.dua.api.utils.MathUtil;
import top.amethyst.dua.api.utils.RSAUtil;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Comparator;
import java.util.List;

public class APIImpl implements AlgorithmUtil, JsonUtil, MathUtil, RSAUtil, DuaUtilsAPI
{
    public static final APIImpl INSTANCE = new APIImpl();

    @Override
    public <T> int binarySearch(List<T> list, int lo, int hi, T value, Comparator<? super T> comparator)
    {
        return top.amethyst.dua.network.utils.AlgorithmUtil.binarySearch(list, lo, hi, value, comparator);
    }

    @Override
    public <T> int binarySearch(List<T> list, T value, Comparator<? super T> comparator)
    {
        return top.amethyst.dua.network.utils.AlgorithmUtil.binarySearch(list, value, comparator);
    }

    @Override
    public <T extends Comparable<T>> int binarySearch(List<T> list, int lo, int hi, T value)
    {
        return top.amethyst.dua.network.utils.AlgorithmUtil.binarySearch(list, lo, hi, value);
    }

    @Override
    public <T extends Comparable<T>> int binarySearch(List<T> list, T value)
    {
        return top.amethyst.dua.network.utils.AlgorithmUtil.binarySearch(list, value);
    }

    @Override
    public int pow(int a, int b)
    {
        return top.amethyst.dua.network.utils.MathUtil.pow(a, b);
    }

    @Override
    public long pow(long a, long b)
    {
        return top.amethyst.dua.network.utils.MathUtil.pow(a, b);
    }

    @Override
    public KeyPair genKeyPair()
    {
        return top.amethyst.dua.network.utils.RSAUtil.genKeyPair();
    }

    @Override
    public String keyToString(Key key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.keyToString(key);
    }

    @Override
    public @Nullable RSAPublicKey getPublicKey(String str)
    {
        return top.amethyst.dua.network.utils.RSAUtil.getPublicKey(str);
    }

    @Override
    public @Nullable RSAPrivateKey getPrivateKey(String str)
    {
        return top.amethyst.dua.network.utils.RSAUtil.getPrivateKey(str);
    }

    @Override
    public Pair<String, String> genKeyPairString()
    {
        return top.amethyst.dua.network.utils.RSAUtil.genKeyPairString();
    }

    @Override
    public String encrypt(@NotNull String str, RSAPublicKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.encrypt(str, key);
    }

    @Override
    public String encrypt(@NotNull IJsonSerializable obj, RSAPublicKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.encrypt(obj, key);
    }

    @Override
    public @NotNull String decrypt(String str, RSAPrivateKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.decrypt(str, key);
    }

    @Override
    public @NotNull IJsonSerializable decrypt(String str, RSAPrivateKey key, Class<? extends IJsonSerializable> targetClass) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.decrypt(str, key, targetClass);
    }

    @Override
    public String sign(@NotNull String str, RSAPrivateKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.sign(str, key);
    }

    @Override
    public String sign(@NotNull IJsonSerializable obj, RSAPrivateKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.sign(obj, key);
    }

    @Override
    public boolean verify(@NotNull String str, String sign, RSAPublicKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.verify(str, sign, key);
    }

    @Override
    public boolean verify(@NotNull IJsonSerializable obj, String sign, RSAPublicKey key) throws InvalidKeyException
    {
        return top.amethyst.dua.network.utils.RSAUtil.verify(obj, sign, key);
    }

    @Override
    public Gson getGson()
    {
        return top.amethyst.dua.network.utils.JsonUtil.GSON;
    }

    @Override
    public <T extends IJsonSerializable> @NotNull T deserialize(@NotNull JsonObject json, @NotNull Class<? extends T> classOfT)
    {
        return top.amethyst.dua.network.utils.JsonUtil.deserialize(json, classOfT);
    }

    @Override
    public AlgorithmUtil getAlgorithmUtil()
    {
        return this;
    }

    @Override
    public JsonUtil getJsonUtil()
    {
        return this;
    }

    @Override
    public MathUtil getMathUtil()
    {
        return this;
    }

    @Override
    public RSAUtil getRSAUtil()
    {
        return this;
    }
}
