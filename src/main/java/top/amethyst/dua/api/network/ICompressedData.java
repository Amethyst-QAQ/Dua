package top.amethyst.dua.api.network;

import top.amethyst.dua.api.core.IJsonSerializable;

/**
 * 压缩的数据，用于在网络上传播以及保存到文件
 * @param <T> 要压缩的类，必须实现{@link IJsonSerializable}接口。
 * @see IJsonSerializable
 */
public interface ICompressedData <T extends IJsonSerializable>
{
    /**
     * 获取当前维护的数据
     */
    T getData();

    /**
     * 设置当前维护的数据
     */
    void setData(T data);

    /**
     * 把当前维护的数据压缩并返回
     * @return 压缩后的数据
     */
    byte[] compress();

    /**
     * 把给定压缩数据解压到当前维护的数据
     */
    void decompress(byte[] compressedData);

    /**
     * 把当前维护的数据保存到文件
     * @param fileName 文件名，若不存在会自动创建
     */
    void saveToFile(String fileName);

    /**
     * 读取文件到当前维护的数据
     */
    void loadFromFile(String fileName);
}
