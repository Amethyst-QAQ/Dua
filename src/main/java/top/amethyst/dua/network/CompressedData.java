package top.amethyst.dua.network;

import com.google.gson.JsonObject;
import top.amethyst.dua.api.core.IJsonSerializable;
import top.amethyst.dua.api.network.ICompressedData;
import top.amethyst.dua.utils.JsonUtil;

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

    private static byte[] toByteArray(String filename) throws IOException{

        File f = new File(filename);
        if(!f.exists()){
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());
        BufferedInputStream in = null;
        try{
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1 << 26;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while(-1 != (len = in.read(buffer,0,buf_size))){
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        }catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally{
            try{
                in.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
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
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
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
                    e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
                }
            }
        }

        byte[] out = outputStream.toByteArray();
        if(out.length > (1 << 20))
            throw new RuntimeException("Data bigger than 1MB, will cause error decompressing");

        try
        {
            outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
        }
        return out;
    }

    @Override
    public void decompress(byte[] compressedData)
    {
        if(compressedData.length > (1 << 20))
            throw new RuntimeException("Compressed data bigger than 1MB, cannot decompress");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzip = null;

        try
        {
            gzip = new GZIPInputStream(inputStream);
            byte[] buffer = new byte[1 << 20];
            int offset;
            while ((offset = gzip.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, offset);
            }

            String jsonString = outputStream.toString();

            data = JsonUtil.deserialize(JsonUtil.GSON.fromJson(jsonString, JsonObject.class), classOfT);
        }
        catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
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
                e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
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
                if(!file.getParentFile().exists())
                    if(!file.getParentFile().mkdirs())
                        throw new RuntimeException("What Happened??");

                if(!file.createNewFile())
                    throw new RuntimeException("What Happened??");
            }

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(compressedData, 0, compressedData.length);
            bufferedOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void loadFromFile(String fileName)
    {
        try
        {
            decompress(toByteArray(fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
        }
    }
}
