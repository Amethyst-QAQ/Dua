package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IScript;

import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class ScriptInputWrapper <T> implements IScript.IInputWrapper<T>
{
    public static class IntegerInputWrapper extends ScriptInputWrapper<Integer>
    {
        public IntegerInputWrapper(Integer value)
        {
            super(value);
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.addProperty("className", Integer.class.getName());
            json.addProperty("value", get());
            return json;
        }
    }

    public static class MethodInputWrapper extends ScriptInputWrapper<Method>
    {
        public MethodInputWrapper(Method value)
        {
            super(value);
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.addProperty("className", Method.class.getName());

                JsonObject temp = new JsonObject();
                temp.addProperty("className", get().getDeclaringClass().getName());
                temp.addProperty("methodName", get().getName());

                    JsonArray paramClassNames = new JsonArray();
                    for(Class<?> i : get().getParameterTypes())
                        paramClassNames.add(i.getName());

                temp.add("paramClassNames", paramClassNames);

            json.add("value", temp);
            return json;
        }
    }

    private final T value;

    public ScriptInputWrapper(T value)
    {
        this.value = value;
    }

    public static ScriptInputWrapper<?> deserialize(JsonObject json)
    {
        try
        {
            Class<?> classOfT = Class.forName(json.get("className").getAsString());
            if(Integer.class.isAssignableFrom(classOfT))
            {
                return new IntegerInputWrapper(json.get("value").getAsInt());
            }
            else if (Method.class.isAssignableFrom(classOfT))
            {
                JsonObject temp = json.getAsJsonObject("value");
                Class<?> methodClass = Class.forName(temp.get("className").getAsString());
                ArrayList<Class<?>> paramClasses = new ArrayList<>();
                JsonArray paramList = temp.getAsJsonArray("paramClassNames");
                for(JsonElement i : paramList)
                {
                    String str = i.getAsString();
                    switch (str)
                    {
                        case "int":
                            paramClasses.add(Integer.TYPE);
                            break;
                        case "short":
                            paramClasses.add(Short.TYPE);
                            break;
                        case "long":
                            paramClasses.add(Long.TYPE);
                            break;
                        case "float":
                            paramClasses.add(Float.TYPE);
                            break;
                        case "double":
                            paramClasses.add(Double.TYPE);
                            break;
                        case "byte":
                            paramClasses.add(Byte.TYPE);
                            break;
                        case "char":
                            paramClasses.add(Character.TYPE);
                            break;
                        case "boolean":
                            paramClasses.add(Boolean.TYPE);
                            break;
                        case "void":
                            paramClasses.add(Void.TYPE);
                            break;
                        default:
                            paramClasses.add(Class.forName(i.getAsString()));
                            break;
                    }
                }
                return new MethodInputWrapper(methodClass.getMethod(temp.get("methodName").getAsString(), paramClasses.toArray(new Class[0])));
            }
        }
        catch (ClassNotFoundException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T get()
    {
        return value;
    }
}
