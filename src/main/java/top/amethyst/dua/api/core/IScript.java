package top.amethyst.dua.api.core;

import org.jetbrains.annotations.NotNull;
import xyz.chlamydomonos.brainfuc.IBFChScript;

import java.util.ArrayList;
import java.util.Queue;

/**
 * 交易脚本，用于验证交易合法性
 */
public interface IScript extends IJsonSerializable
{
    /**
     * 交易脚本输入
     * @param <T> 该输入的类型
     */
    interface IInputWrapper <T> extends IJsonSerializable
    {
        /**
         * 获取作为输入的数据
         */
        T get();
    }

    /**
     * 与{@link IBFChScript#getData()}相同
     * @see IBFChScript
     */
    ArrayList<Character> getData();

    /**
     * 与{@link IBFChScript#getBracketInfo()}相同
     * @see IBFChScript
     */
    ArrayList<Integer> getBracketInfo();

    /**
     * 把交易脚本的输入列表编为队列，提供给脚本引擎
     * @see xyz.chlamydomonos.brainfuc.BFChEngine
     */
    @NotNull
    Queue<Object> enqueueInputs();
}
