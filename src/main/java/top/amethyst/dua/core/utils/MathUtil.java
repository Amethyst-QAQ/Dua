package top.amethyst.dua.core.utils;

public class MathUtil
{
    public static int pow(int a, int b)
    {
        if(b == 0)
            return 1;
        if(b == 1)
            return a;
        return pow(a * a, b >> 1) * pow(a, b & 1);
    }
}
