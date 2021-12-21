package top.amethyst.dua.core.api;

/**
 * 哈希值
 * <br>
 * 哈希值提供转化为字符串，判断相等，比较以及转化为32位哈希值的方法
 */
public interface IHash extends Comparable<IHash>, IJsonSerializable
{
    /**
     * 转化为字符串
     */
    String toString();

    /**
     * 判断本哈希值与给定哈希值是否相等
     */
    boolean equals(Object o);

    /**
     * 转化为32位哈希值
     */
    int hashCode();
}
