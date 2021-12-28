package top.amethyst.dua.api.utils;

import java.util.Comparator;
import java.util.List;

/**
 * 一些自定义的算法工具
 */
public interface AlgorithmUtil
{
    /**
     * 二分查找
     * @param list       有序列表
     * @param lo         查找开始位置
     * @param hi         查找的结束位置
     * @param value      查找的元素
     * @param comparator 比较器
     * @return 如果找到 返回元素value的索引，否则返回 < 0
     */
    <T> int binarySearch(List<T> list, int lo, int hi, T value, Comparator<? super T> comparator);

    /**
     * 二分查找
     * @param list       有序列表
     * @param value      查找的元素
     * @param comparator 比较器
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    <T> int binarySearch(List<T> list, T value, Comparator<? super T> comparator);

    /**
     * 二分查找
     * @param list  有序列表，元素必须实现了Comparable接口
     * @param lo    查找开始位置
     * @param hi    查找的结束位置
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    <T extends Comparable<T>> int binarySearch(List<T> list, int lo, int hi, T value);

    /**
     * 二分查找
     * @param list  有序列表 元素必须实现了Comparable接口
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    <T extends Comparable<T>> int binarySearch(List<T> list, T value);
}
