package top.amethyst.dua.core;

import top.amethyst.dua.core.utils.GsonImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash
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

    public Hash(Object obj)
    {
        String json = GsonImpl.GSON.toJson(obj);
        value = SHA256(json);
    }

    public Hash(Object... objects)
    {
        StringBuilder json = new StringBuilder();
        for(Object i : objects)
            json.append(GsonImpl.GSON.toJson(i));

        value = SHA256(json.toString());
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Hash)
            return value.equals(((Hash) obj).value);
        return false;
    }
}
