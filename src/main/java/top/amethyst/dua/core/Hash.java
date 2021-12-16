package top.amethyst.dua.core;

import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.core.api.ICustomHashObject;
import top.amethyst.dua.core.api.IHash;
import top.amethyst.dua.core.utils.GsonImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 对{@link IHash}接口的实现
 */
public class Hash implements IHash
{
    private final String value;

    private static String byte2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for (byte aByte : bytes)
        {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1)
            {
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }

    private static String SHA256(String str)
    {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            System.out.println("getSHA256 is error" + e.getMessage());
        }
        return encodeStr;
    }

    /**
     * 创建指定对象的哈希值
     * @param obj 指定的对象
     */
    public Hash(Object obj)
    {
        if(obj instanceof ICustomHashObject)
            obj = ((ICustomHashObject) obj).getHashPart();
        String json = GsonImpl.GSON.toJson(obj);
        value = SHA256(json);
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public int compareTo(@NotNull IHash o)
    {
        return value.compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hash hash = (Hash) o;
        return value.equals(hash.value);
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
