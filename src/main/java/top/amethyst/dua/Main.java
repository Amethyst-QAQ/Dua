package top.amethyst.dua;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.amethyst.dua.core.Script;
import top.amethyst.dua.network.utils.JsonUtil;
import top.amethyst.dua.network.utils.Log4jInitializer;
import xyz.chlamydomonos.brainfuc.BFChEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static StringBuilder create()
    {
        return new StringBuilder();
    }

    public static void main(String[] args)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/dua/test/testScript.json"))));
        StringBuilder builder = new StringBuilder();
        String line;
        try
        {
            line = reader.readLine();
            while (line != null)
            {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        LOGGER.debug(int.class.getName());
        JsonObject jsonObject = JsonUtil.GSON.fromJson(builder.toString(), JsonObject.class);
        Script script = new Script(jsonObject);
        BFChEngine bfChEngine = new BFChEngine();
        bfChEngine.runScript(script, script.enqueueInputs(), true, true, true);
        for (Object i : bfChEngine.getOutputs())
            LOGGER.debug(i);
    }
}
