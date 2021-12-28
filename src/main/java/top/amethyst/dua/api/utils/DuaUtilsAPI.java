package top.amethyst.dua.api.utils;

/**
 * Dua其他工具API，提供了一些可能会用到的算法
 */
public interface DuaUtilsAPI
{
    /**
     * 获取算法工具
     * @see AlgorithmUtil
     */
    AlgorithmUtil getAlgorithmUtil();

    /**
     * 获取Json工具
     * @see JsonUtil
     */
    JsonUtil getJsonUtil();

    /**
     * 获取数学工具
     * @see MathUtil
     */
    MathUtil getMathUtil();

    /**
     * 获取RSA加解密工具
     * @see RSAUtil
     */
    RSAUtil getRSAUtil();
}
