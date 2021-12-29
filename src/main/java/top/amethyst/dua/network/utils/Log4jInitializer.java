package top.amethyst.dua.network.utils;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.BufferedInputStream;
import java.util.Objects;

/**
 * 用于初始化Log4j的工具类
 */
public class Log4jInitializer
{
    /**
     * 初始化Log4j的配置文件
     */
    public static void init()
    {
        try
        {
            BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(Log4jInitializer.class.getResourceAsStream("/dua/log4j.xml")));
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
