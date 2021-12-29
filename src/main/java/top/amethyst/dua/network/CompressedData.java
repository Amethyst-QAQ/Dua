package top.amethyst.dua.network;

import com.google.gson.JsonObject;
import top.amethyst.dua.api.core.IJsonSerializable;
import top.amethyst.dua.api.network.ICompressedData;
import top.amethyst.dua.network.utils.JsonUtil;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class CompressedData <T extends IJsonSerializable> implements ICompressedData<T>
{
    private T data;
    private final Class<? extends T> classOfT;

    @SuppressWarnings("unchecked")
    private Class<? extends T> getTClass()
    {
        Class<T> out;
        Type temp = getClass().getGenericSuperclass();
        if(!(temp instanceof ParameterizedType))
            throw new RuntimeException("What Happened?");
        else
        {
            Type[] types = ((ParameterizedType) temp).getActualTypeArguments();
            if(types == null)
                throw new RuntimeException("What Happened??");
            else if(types.length == 0)
                throw new RuntimeException("What Happened???");
            else
                out = (Class<T>) types[0];
        }
        return out;
    }

    public CompressedData()
    {
        classOfT = getTClass();
        data = null;
    }

    public CompressedData(T data)
    {
        classOfT = getTClass();
        this.data = data;
    }

    public CompressedData(byte[] compressedData)
    {
        classOfT = getTClass();
        decompress(compressedData);
    }

    public CompressedData(String fileName)
    {
        classOfT = getTClass();
        loadFromFile(fileName);
    }

    @Override
    public T getData()
    {
        return data;
    }

    @Override
    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public byte[] compress()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try
        {
            gzip = new GZIPOutputStream(outputStream);
            gzip.write(data.serialize().toString().getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(gzip != null)
            {
                try
                {
                    gzip.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        byte[] out = outputStream.toByteArray();
        if(out.length > (1 << 6))
            throw new RuntimeException("Data bigger than 1MB, will cause error decompressing");

        try
        {
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public void decompress(byte[] compressedData)
    {
        if(compressedData.length > 1 >> 6)
            throw new RuntimeException("Compressed data bigger than 1MB, cannot decompress");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzip = null;

        try
        {
            gzip = new GZIPInputStream(inputStream);
            byte[] buffer = new byte[1 << 6];
            int offset;
            while ((offset = gzip.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, offset);
            }

            data = JsonUtil.deserialize(JsonUtil.GSON.fromJson(outputStream.toString(), JsonObject.class), classOfT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                outputStream.close();
                inputStream.close();
                if(gzip != null)
                    gzip.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveToFile(String fileName)
    {
        byte[] compressedData = compress();

        try
        {
            File file = new File(fileName);
            if(!file.exists())
            {
                if(!file.createNewFile())
                    throw new RuntimeException("What Happened??");
            }

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bytes = data.serialize().toString().getBytes(StandardCharsets.UTF_8);
            bufferedOutputStream.write(bytes, 0, bytes.length);
            bufferedOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromFile(String fileName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedInputStream inputStream = null;
        GZIPInputStream gzip = null;
        try
        {
            inputStream = new BufferedInputStream(new  FileInputStream(fileName));
            if(inputStream.available() > 1 >> 6)
                throw new RuntimeException("File bigger than 1MB, cannot load");
            gzip = new GZIPInputStream(inputStream);

            byte[] buffer = new byte[1 << 6];
            int offset;
            while ((offset = gzip.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, offset);
            }

            data = JsonUtil.deserialize(JsonUtil.GSON.fromJson(outputStream.toString(), JsonObject.class), classOfT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                outputStream.close();
                if(inputStream != null)
                    inputStream.close();
                if (gzip != null)
                    gzip.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
