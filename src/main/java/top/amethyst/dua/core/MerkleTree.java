package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IJsonSerializable;
import top.amethyst.dua.api.core.IMerkleTree;
import top.amethyst.dua.utils.AlgorithmUtil;
import top.amethyst.dua.utils.JsonUtil;
import top.amethyst.dua.utils.MathUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 对{@link IMerkleTree}接口的实现
 * <br>
 * 此类是抽象类，但不含任何抽象方法。使用时可以直接构造匿名内部类。
 */
public abstract class MerkleTree <T extends IJsonSerializable> implements IMerkleTree<T>
{
    private abstract static class Node implements IJsonSerializable
    {
        @NotNull
        public abstract Hash getChildrenHash();

        @Nullable
        public abstract Node getLChild();

        @Nullable
        public abstract Node getRChild();
    }

    private static class ParentNode extends Node
    {
        private final Hash childrenHash;
        private final Node lChild;
        private final Node rChild;
        public ParentNode(Node lChild)
        {
            this.lChild = lChild;
            this.rChild = null;
            this.childrenHash = new Hash(lChild, null);
        }
        public ParentNode(Node lChild, Node rChild)
        {
            this.lChild = lChild;
            this.rChild = rChild;
            this.childrenHash = new Hash(lChild, rChild);
        }
        public ParentNode(JsonObject json)
        {
            lChild = null;
            rChild = null;
            childrenHash = new Hash(json.getAsJsonObject("childrenHash"));
        }
        @Override
        public @NotNull Hash getChildrenHash()
        {
            return childrenHash;
        }

        @Override
        public @Nullable Node getLChild()
        {
            return lChild;
        }

        @Override
        public @Nullable Node getRChild()
        {
            return rChild;
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.add("childrenHash", childrenHash.serialize());
            return json;
        }
    }

    private class LeaveNode extends Node implements Comparable<LeaveNode>
    {
        private final T datum;
        private final Hash datumHash;

        public LeaveNode(@NotNull T datum)
        {
            this.datum = datum;
            this.datumHash = new Hash(datum);
        }

        public LeaveNode(@NotNull JsonObject json)
        {
            this.datum = JsonUtil.deserialize(json.getAsJsonObject("datum"), classOfT);
            this.datumHash = new Hash(json.getAsJsonObject("datumHash"));
            if(!this.datumHash.equals(new Hash(this.datum)))
                throw new RuntimeException("Illegal leave node received!");
        }

        @Override
        public @NotNull Hash getChildrenHash()
        {
            return datumHash;
        }

        @Override
        public @Nullable Node getLChild()
        {
            return null;
        }

        @Override
        public @Nullable Node getRChild()
        {
            return null;
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.add("datum", datum.serialize());
            json.add("datumHash", datumHash.serialize());
            return json;
        }

        @Override
        public int compareTo(@NotNull MerkleTree<T>.LeaveNode o)
        {
            return datumHash.compareTo(o.datumHash);
        }

        @SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            LeaveNode leaveNode = (LeaveNode) o;
            return Objects.equals(datum, leaveNode.datum) && Objects.equals(datumHash, leaveNode.datumHash);
        }
    }

    /**
     * 对{@link IMerkleTree.IMerkleProof}接口的实现
     */
    public class MerkleProof implements IMerkleTree.IMerkleProof<T>
    {
        private final T datum;
        private final ArrayList<Boolean> isLeft;
        private final ArrayList<Node> nodes;

        /**
         * 此构造函数无法使用
         * <br>
         * 要创建默克尔证据，使用{@link IMerkleTree#getMerkleProof(T)}
         */
        public MerkleProof(T datum, ArrayList<Boolean> isLeft, ArrayList<Node> nodes)
        {
            this.datum = datum;
            this.isLeft = isLeft;
            this.nodes = nodes;
        }

        /**
         * 从Json反序列化
         */
        public MerkleProof(JsonObject json)
        {
            datum = JsonUtil.deserialize(json.getAsJsonObject("datum"), classOfT);
            isLeft = new ArrayList<>();
            nodes = new ArrayList<>();
            JsonArray isLeftJson = json.getAsJsonArray("isLeft");
            JsonArray nodesJson = json.getAsJsonArray("nodes");
            for(JsonElement i : isLeftJson)
                isLeft.add(i.getAsBoolean());
            for(JsonElement i : nodesJson)
            {
                JsonObject temp = i.getAsJsonObject().getAsJsonObject("value");
                if(i.getAsJsonObject().get("isLeave").getAsBoolean())
                    nodes.add(new LeaveNode(temp));
                else
                    nodes.add(new ParentNode(temp));
            }
        }

        @Override
        public @NotNull T getDatum()
        {
            return datum;
        }

        @Override
        public boolean valid(@NotNull IHash rootHash)
        {
            Node start = new LeaveNode(datum);
            for(int i = 0; i < isLeft.size(); i++)
            {
                if(isLeft.get(i))
                    start = new ParentNode(nodes.get(i), start);
                else
                    start = new ParentNode(start, nodes.get(i));
            }
            return new Hash(start).equals(rootHash);
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            JsonArray nodesJson = new JsonArray();
            JsonArray isLeftJson = new JsonArray();
            for(Node i : nodes)
            {
                JsonObject temp = new JsonObject();
                temp.add("value", i.serialize());
                temp.addProperty("isLeave", !(i instanceof ParentNode));
                nodesJson.add(temp);
            }
            for(Boolean i : isLeft)
                isLeftJson.add(i);
            json.add("nodes", nodesJson);
            json.add("isLeft", isLeftJson);
            json.add("datum", datum.serialize());
            return json;
        }
    }

    private final Class<T> classOfT;
    private final ParentNode root;
    private final ArrayList<LeaveNode> leaves;
    private int height;

    @SuppressWarnings("unchecked")
    private Class<T> getTClass()
    {
        Class<T> out;
        Type temp = getClass().getGenericSuperclass();
        if(!(temp instanceof ParameterizedType))
            throw new RuntimeException("What Happened?");
        else
        {
            Type[] types = ((ParameterizedType) temp).getActualTypeArguments();
            if(types == null)
                throw new RuntimeException("What Happened??");
            else if(types.length == 0)
                throw new RuntimeException("What Happened???");
            else
                out = (Class<T>) types[0];
        }
        return out;
    }

    @NotNull
    private ArrayList<Node> createNodes(@NotNull ArrayList<? extends Node> nodes)
    {
        ArrayList<Node> out = new ArrayList<>();
        for(int i = 0; i < nodes.size() - 1; i += 2)
        {
            ParentNode temp = new ParentNode(nodes.get(i), nodes.get(i + 1));
            out.add(temp);
        }
        if(nodes.size() % 2 == 1)
            out.add(new ParentNode(nodes.get(nodes.size() - 1)));

        height++;
        return out;
    }

    private void searchFrom(int targetIndex, Node node, int currentHeight, int currentIndex, ArrayList<Node> result, ArrayList<Boolean> isLeft)
    {
        if(node instanceof ParentNode)
        {
            if (targetIndex < currentIndex + MathUtil.pow(2, currentHeight))
            {
                searchFrom(targetIndex, node.getLChild(), currentHeight - 1, currentIndex, result, isLeft);
                result.add(node.getRChild());
                isLeft.add(false);
            }
            else
            {
                searchFrom(targetIndex, node.getRChild(), currentHeight - 1, currentIndex + MathUtil.pow(2, currentHeight), result, isLeft);
                result.add(node.getLChild());
                isLeft.add(true);
            }
        }
    }

    /**
     * 从指定数据创建默克尔树
     */
    public MerkleTree(Collection<? extends T> data)
    {
        height = 0;
        classOfT = getTClass();
        leaves = new ArrayList<>();
        for(T i : data)
            leaves.add(new LeaveNode(i));

        leaves.sort(LeaveNode::compareTo);

        ArrayList<? extends Node> nodes = leaves;

        if(nodes.size() == 1)
        {
            root = new ParentNode(nodes.get(0));
        }

        else
        {
            while (nodes.size() > 1)
                nodes = createNodes(nodes);
            root = (ParentNode) nodes.get(0);
        }
    }

    /**
     * 从Json反序列化
     */
    public MerkleTree(JsonObject json)
    {
        height = 0;
        classOfT = getTClass();

        leaves = new ArrayList<>();
        JsonArray array = json.getAsJsonArray("leaves");
        for(JsonElement i : array)
        {
            leaves.add(new LeaveNode(i.getAsJsonObject()));
        }

        ArrayList<? extends Node> nodes = leaves;

        if(nodes.size() == 1)
            root = new ParentNode(nodes.get(0));
        else
        {
            while (nodes.size() > 1)
                nodes = createNodes(nodes);
            root = (ParentNode) nodes.get(0);
        }
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        for(LeaveNode i : leaves)
            array.add(i.serialize());

        json.add("leaves", array);

        return json;
    }

    @Override
    public @NotNull IHash getRootHash()
    {
        return new Hash(root);
    }

    @Override
    public boolean contains(@NotNull T value)
    {
        return AlgorithmUtil.binarySearch(leaves, new LeaveNode(value)) >= 0;
    }

    @Override
    public @NotNull IMerkleProof<T> getMerkleProof(@NotNull T datum)
    {
        int index = AlgorithmUtil.binarySearch(leaves, new LeaveNode(datum));
        if(index < 0)
            throw new IllegalArgumentException("Cannot get Merkle proof of a nonexistent datum!");

        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Boolean> isLeft = new ArrayList<>();

        searchFrom(index, root, height - 1, 0, nodes, isLeft);

        return new MerkleProof(datum, isLeft, nodes);
    }

    @Override
    public T get(int index)
    {
        return leaves.get(index).datum;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MerkleTree)) return false;
        MerkleTree<?> that = (MerkleTree<?>) o;
        return Objects.equals(classOfT, that.classOfT) && Objects.equals(leaves, that.leaves);
    }

    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private int index = 0;
            @Override
            public boolean hasNext()
            {
                return index < leaves.size();
            }

            @Override
            public T next()
            {
                if(!hasNext())
                    throw new NoSuchElementException("What happened??");
                T temp = leaves.get(index).datum;
                index++;
                return temp;
            }
        };
    }
}