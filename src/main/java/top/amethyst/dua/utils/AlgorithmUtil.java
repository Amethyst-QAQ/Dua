package top.amethyst.dua.utils;

import java.util.Comparator;
import java.util.List;

/**
 * 一些自定义的算法工具
 */
public class AlgorithmUtil
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
    public static <T> int binarySearch(List<T> list, int lo, int hi, T value, Comparator<? super T> comparator)
    {

        if (comparator == null)
        {
            throw new IllegalArgumentException("comparable can not be null!");
        }

        if (checkList(list))
        {
            return 0;
        }

        checkBinarySearchBounds(lo, hi, list.size());

        while (lo <= hi)
        {
            final int mid = (lo + hi) >>> 1;
            final T midVal = list.get(mid);

            if (comparator.compare(midVal, value) < 0)
            {
                lo = mid + 1;
            }
            else if (comparator.compare(midVal, value) > 0)
            {
                hi = mid - 1;
            }
            else
            {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present

    }

    /**
     * 二分查找
     * @param list       有序列表
     * @param value      查找的元素
     * @param comparator 比较器
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    public static <T> int binarySearch(List<T> list, T value, Comparator<? super T> comparator)
    {
        if (checkList(list))
        {
            return 0;
        }
        return binarySearch(list, 0, list.size() - 1, value, comparator);
    }

    /**
     * 二分查找
     * @param list  有序列表，元素必须实现了Comparable接口
     * @param lo    查找开始位置
     * @param hi    查找的结束位置
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    public static <T extends Comparable<T>> int binarySearch(List<T> list, int lo, int hi, T value)
    {

        if (checkList(list))
        {
            return 0;
        }
        checkBinarySearchBounds(lo, hi, list.size());

        while (lo <= hi)
        {
            final int mid = (lo + hi) >>> 1;
            final T midVal = list.get(mid);

            if (midVal.compareTo(value) < 0)
            {
                lo = mid + 1;
            }
            else if (midVal.compareTo(value) > 0)
            {
                hi = mid - 1;
            }
            else
            {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present

    }

    /**
     * 二分查找
     * @param list  有序列表 元素必须实现了Comparable接口
     * @param value 查找的元素
     * @return 元素 如果找到 返回元素value的索引，否则返回 < 0
     */
    public static <T extends Comparable<T>> int binarySearch(List<T> list, T value)
    {
        if (checkList(list))
        {
            return 0;
        }
        return binarySearch(list, 0, list.size() - 1, value);
    }

    private static boolean checkList(List<?> list)
    {
        return list == null || list.isEmpty();
    }

    private static void checkBinarySearchBounds(int startIndex, int endIndex, int length)
    {
        if (startIndex > endIndex)
        {
            throw new IllegalArgumentException();
        }
        if (startIndex < 0 || endIndex > length)
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
