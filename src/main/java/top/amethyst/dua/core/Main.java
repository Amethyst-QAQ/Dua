package top.amethyst.dua.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.amethyst.dua.core.utils.Log4jInitializer;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        Integer a = 1;
        Integer b = 1;
        Integer c = 2;
        Integer d = 2;
        Hash h1 = new Hash(a);
        Hash h2 = new Hash(b);
        Hash h3 = new Hash(c);
        Hash h4 = new Hash(d);

        LOGGER.debug("Created Hash {} of {}", h1, a);
        LOGGER.debug("Created Hash {} of {}", h2, b);
        LOGGER.debug("Created Hash {} of {}", h3, c);
        LOGGER.debug("Created Hash {} of {}", h4, d);
    }
}
