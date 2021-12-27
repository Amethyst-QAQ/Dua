package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IJsonSerializable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;

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
     * @param objects 指定的对象，可以为null
     */
    public Hash(IJsonSerializable... objects)
    {
        StringBuilder builder = new StringBuilder();
        for(IJsonSerializable i : objects)
        {
            if (i == null)
                builder.append("{}");
            else
            {
                JsonObject json = i.serialize();
                ArrayList<String> fields = i.getHashExcludedFields();
                if(!(fields == null || fields.isEmpty()))
                {
                    HashSet<String> fieldsSet = new HashSet<>(fields);
                    JsonObject temp = new JsonObject();
                    for(String j : json.keySet())
                    {
                        if(!fieldsSet.contains(j))
                            temp.add(j, json.get(j));
                    }
                    json = temp;
                }
                builder.append(json);
            }
        }
        value = SHA256(builder.toString());
    }

    /**
     * 从Json反序列化
     */
    public Hash(JsonObject json)
    {
        value = json.get("value").getAsString();
    }

    /**
     * 创建空对象的哈希值
     */
    public Hash()
    {
        value = SHA256("{}");
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

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        return json;
    }
}
