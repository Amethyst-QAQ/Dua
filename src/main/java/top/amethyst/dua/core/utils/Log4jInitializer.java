package top.amethyst.dua.core.utils;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import top.amethyst.dua.core.Main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log4jInitializer
{
    public static void init()
    {
        try
        {
            BufferedInputStream in = new BufferedInputStream(Log4jInitializer.class.getResourceAsStream("/dua/log4j.xml"));
            final ConfigurationSource source = new ConfigurationSource(in);
            Configurator.initialize(null, source);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
