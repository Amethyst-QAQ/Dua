package top.amethyst.dua.utils;

/**
 * 一些自定义的数学工具
 */
public class MathUtil
{
    /**
     * 快速幂
     * @param a 底数
     * @param b 指数
     * @return 幂
     */
    public static int pow(int a, int b)
    {
        if(b == 0)
            return 1;
        if(b == 1)
            return a;
        return pow(a * a, b >> 1) * pow(a, b & 1);
    }

    /**
     * 快速幂
     * @param a 底数
     * @param b 指数
     * @return 幂
     */
    public static long pow(long a, long b)
    {
        if(b == 0)
            return 1;
        if(b == 1)
            return a;
        return pow(a * a, b >> 1) * pow(a, b & 1);
    }
}
