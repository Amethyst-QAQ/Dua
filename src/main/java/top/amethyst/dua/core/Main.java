package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.amethyst.dua.utils.Log4jInitializer;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("className", "com.google.gson.JsonObject");
        ScriptInputWrapper.deserialize(jsonObject);
    }
}
