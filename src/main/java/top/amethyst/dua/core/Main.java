package top.amethyst.dua.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.amethyst.dua.core.utils.Log4jInitializer;
import top.amethyst.dua.core.utils.MathUtil;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        LOGGER.debug(MathUtil.pow(3, 9));
    }
}
