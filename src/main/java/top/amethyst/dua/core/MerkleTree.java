package top.amethyst.dua.core;

import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.core.api.IHash;
import top.amethyst.dua.core.api.IMerkleTree;
import top.amethyst.dua.core.utils.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 对{@link IMerkleTree}接口的实现
 */
public class MerkleTree <T> implements IMerkleTree<T>
{
    private static class Node
    {
    }
    private static class ParentNode extends Node
    {
        public Hash childrenHash;
        public Node lChild;
        public Node rChild;

        public ParentNode(@NotNull Node lChild)
        {
            Hash lHash = new Hash(lChild);
            Hash rHash = new Hash(null);
            childrenHash = new Hash(lHash.toString() + rHash.toString());
            this.lChild = lChild;
            this.rChild = null;
        }

        public ParentNode(@NotNull Node lChild, @NotNull Node rChild)
        {
            Hash lHash = new Hash(lChild);
            Hash rHash = new Hash(rChild);
            childrenHash = new Hash(lHash.toString() + rHash.toString());
            this.lChild = lChild;
            this.rChild = rChild;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParentNode that = (ParentNode) o;
            return childrenHash.equals(that.childrenHash);
        }

        @Override
        public int hashCode()
        {
            return childrenHash.hashCode();
        }
    }

    private static class LeaveNode <T> extends Node
    {
        public Hash dataHash;
        public T data;

        public LeaveNode(@NotNull T data)
        {
            dataHash = new Hash(data);
            this.data = data;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LeaveNode leaveNode = (LeaveNode) o;
            return dataHash.equals(leaveNode.dataHash);
        }

        @Override
        public int hashCode()
        {
            return dataHash.hashCode();
        }
    }

    private static class MerkleProofNodes
    {
        private final ArrayList<Pair<Boolean, Node>> data;

        public MerkleProofNodes(@NotNull ArrayList<Pair<Boolean, Node>> data)
        {
            this.data = data;
        }

        @NotNull
        public ArrayList<Pair<Boolean, Node>> getData()
        {
            return data;
        }
    }

    /**
     * 对{@link top.amethyst.dua.core.api.IMerkleTree.IMerkleProof}接口的实现
     */
    public class MerkleProof implements IMerkleTree.IMerkleProof<T>
    {
        private final T datum;
        private final ArrayList<Pair<Boolean, Hash>> data;

        /**
         * 构造函数
         * 该构造函数无法使用，要创建默克尔证据，使用{@link MerkleTree#getMerkleProof(T)}
         */
        public MerkleProof(@NotNull T datum, @NotNull MerkleProofNodes from)
        {
            this.datum = datum;
            data = new ArrayList<>();
            ArrayList<Pair<Boolean, Node>> fromData = from.getData();
            for(Pair<Boolean, Node> i : fromData)
            {
                data.add(new Pair<>(i.getKey(), new Hash(i.getValue())));
            }
        }

        /**
         * 获得默克尔证据对应的数据
         */
        @NotNull
        public T getDatum()
        {
            return datum;
        }

        /**
         * 验证默克尔证据的有效性
         * @param rootHash 对应的默克尔树根节点哈希值
         * @return 如果默克尔证据有效，返回true
         */
        public boolean valid(@NotNull IHash rootHash)
        {
            Hash hash = new Hash(datum);
            for(Pair<Boolean, Hash> i : data)
            {
                if(i.getKey())
                    hash = new Hash(i.getValue().toString() + hash);
                else
                    hash = new Hash(hash + i.getValue().toString());
            }

            return hash.equals(rootHash);
        }
    }

    private final Node root;
    private final HashMap<LeaveNode<T>, Integer> leaves;
    private int height;

    private int lastIndex;
    private ArrayList<Pair<Boolean, Node>> lastSearch;

    private @NotNull ArrayList<Node> generateNodes(@NotNull ArrayList<Node> from)
    {
        height++;
        ArrayList<Node> out = new ArrayList<>();
        for(int i = 0; i < from.size(); i += 2)
            out.add(new ParentNode(from.get(i), from.get(i + 1)));

        if(from.size() % 2 == 1)
            out.add(new ParentNode(from.get(from.size() - 1)));

        return out;
    }

    private boolean searchFrom(@NotNull Node node, int currentNodeIndex, int currentHeight)
    {
        if(node instanceof LeaveNode)
        {
            return leaves.get((LeaveNode<T>) node) == lastIndex;
        }

        ParentNode node1 = (ParentNode) node;

        int layerWidth = MathUtil.pow(2, currentHeight);
        boolean found;

        if(lastIndex >= currentNodeIndex + layerWidth)
            found = searchFrom(node1.rChild, currentNodeIndex + layerWidth, currentHeight - 1);
        else
            found = searchFrom(node1.lChild, currentNodeIndex, currentHeight - 1);

        if(found)
        {
            if(lastIndex >= currentNodeIndex + layerWidth)
                lastSearch.add(new Pair<>(true, node1.lChild));
            else
                lastSearch.add(new Pair<>(false, node1.rChild));

            return true;
        }

        return false;
    }

    private void searchFor(int index)
    {
        if(index == lastIndex)
            return;
        lastSearch.clear();
        lastIndex = index;
        searchFrom(root, 0, height - 1);
    }

    /**
     * 构造函数
     * @param data 要构建为默克尔树的数据
     */
    public MerkleTree(@NotNull List<T> data)
    {
        if(data.size() == 0)
            throw new IllegalArgumentException("Cannot build Merkle tree with 0 datum");

        ArrayList<Node> nodes = new ArrayList<>();

        leaves = new HashMap<>();

        for (int i = 0; i < data.size(); i++)
        {
            LeaveNode<T> newNode = new LeaveNode<>(data.get(i));
            nodes.add(newNode);
            leaves.put(newNode, i);
        }

        height = 0;

        while (nodes.size() > 1)
            nodes = generateNodes(nodes);

        root = nodes.get(0);
        lastSearch = new ArrayList<>();
        lastIndex = 0;
    }

    /**
     * 获得根节点的哈希值
     */
    @NotNull
    public IHash getRootHash()
    {
        return new Hash(root);
    }

    /**
     * 判断指定数据是否在默克尔树中
     * @param value 要判断的数据
     * @return 如果数据在默克尔树中，返回true，否则返回false
     */
    public boolean contains(@NotNull T value)
    {
        return leaves.containsKey(new LeaveNode<>(value));
    }

    /**
     * 获取默克尔证据
     * <br>
     * 注意，调用本方法前需要先调用{@link #contains(T)}确定要验证的数据在默克尔树中
     * @param datum 要验证的数据
     * @return 用于验证数据的默克尔证据
     *
     */
    @NotNull
    public IMerkleTree.IMerkleProof<T> getMerkleProof(@NotNull T datum)
    {
        if(!contains(datum))
            throw new IllegalArgumentException("Merkle tree does not contain this datum");

        searchFor(leaves.get(new LeaveNode<>(datum)));

        return new MerkleProof(datum, new MerkleProofNodes(lastSearch));
    }
}
