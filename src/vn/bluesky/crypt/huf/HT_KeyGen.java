package vn.bluesky.crypt.huf;

import vn.bluesky.crypt.Hash;
import vn.bluesky.crypt.rsa.RSA_KeyGen;
import vn.bluesky.crypt.xor.XOR;
import vn.bluesky.ui.DialogBox;
import vn.bluesky.utils.data.ArrayUtil;
import vn.bluesky.utils.data.FileC;
import vn.bluesky.utils.structure.Heap;

import javax.xml.soap.Node;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by DuThien on 22/03/2017.
 */
public class HT_KeyGen {
    public interface HT_KeyComplete{
        void  onComplete(BNode res);
    }
    public static int[] histogramOfFile(String path) {
        FileC f = FileC.openToRead(path);
        if (f == null) return null;
        int[] res = new int[256];
        for (int i = 0; i < 256; ++i)
            res[i] = 0;
        byte[] buff = new byte[1048576];
        while (f.remain() > 0) {
            int r = f.read(buff);
            for (int i = 0; i < r; ++i)
                res[buff[i] + 128]++;
        }
        f.close();
        return res;
    }

    public static void fromFile(String path, HT_KeyGen.HT_KeyComplete onResultCallback, DialogBox.OnCancelCallback onCancelCallback)
    {
        FileC f = FileC.openToRead(path);
        if (f== null) return;
        byte [] md = new byte[16];
        f.read(md);
        byte []b = new byte[f.remain()];
        f.read(b);
        DialogBox.passwordDialog("Decrypt your key file", result -> {
            XOR kx = new XOR(result);
            kx.xor(b);
            //System.out.println(ArrayUtil.bytes2Hex(b));
            byte[] mds = Hash.MD5(b);
            if (ArrayUtil.equalArray(md, mds))
                onResultCallback.onComplete(new BNode(b));
            else
                DialogBox.showAlert("Error", "Wrong password!");
        }, onCancelCallback);
        f.close();
    }

    public static void saveToFile(BNode node, String path, String password)
    {
        XOR xor = new XOR(password);
        FileC  f = FileC.openToWrite(path);
        byte []b = node.toArray();
        byte[] md = Hash.MD5(b);
        f.write(md);
        //System.out.println(ArrayUtil.bytes2Hex(b));
        xor.xor(b);
        f.write(b);
        f.close();
    }

    public static BNode buildTree(int[] his) {
        Random random = new Random(System.currentTimeMillis());
        Heap<BNode> heap = new Heap<>(BNode.class, 1000);
        for (int i = 0; i < his.length; ++i) {
            heap.push(new BNode((byte) (i - 128), his[i]));
        }
        while (heap.size() > 1) {
            BNode left;
            BNode right;
            if (random.nextBoolean()) {
                left = heap.top();
                heap.removeTop();
                right = heap.top();
                heap.removeTop();

            } else {
                right = heap.top();
                heap.removeTop();
                left = heap.top();
                heap.removeTop();
            }
            BNode node = new BNode(left, right);
            heap.push(node);
        }
        return heap.top();
    }

    public static class BNode implements Heap.HeapNode {
        public int id;
        public BNode left = null;
        public BNode right = null;
        byte value;
        long weight;

        public BNode(BNode left, BNode right) {
            this.left = left;
            this.right = right;
            this.weight = left.weight+right.weight;
        }

        public BNode(byte value, long weight) {
            this.value = value;
            this.weight = weight;
        }


        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compare(Heap.HeapNode x) {
            if (weight > ((BNode) x).weight) return -1;
            if (weight == ((BNode) x).weight) return 0;
            return 1;
        }


        @Override
        public String toString() {
            if (isLeaf())
                return "["+value+"]";

            return "{"+ left.toString()+right.toString()+"}";

        }

        static Random rand = new Random();
        private static final byte LEAF = 0;
        private static final byte N_LEAF = 1;

        private static byte randomOfs(byte id)
        {
            return (byte) (Math.abs(rand.nextInt())%100*2+ id);
        }

        private void toArray(byte[]r, int []n)
        {
            if (left.isLeaf()) {
                r[n[0]++] = randomOfs(LEAF);
                r[n[0]++] = left.value;
            }
            else
            {
                r[n[0]++] = randomOfs(N_LEAF);
                left.toArray(r, n);
            }

            if (right.isLeaf()) {
                r[n[0]++] = randomOfs(LEAF);
                r[n[0]++] = right.value;
            }
            else
            {
                r[n[0]++] = randomOfs(N_LEAF);
                right.toArray(r, n);
            }
        }

        public BNode(byte []data)
        {
            int [] n = {0};
            if (data[n[0]++]%2==LEAF)
            {
                left = new BNode(data[n[0]++], 0);
            }
            else
            {
                left = new BNode(data, n);
            }

            if (data[n[0]++]%2==LEAF)
            {
                right = new BNode(data[n[0]++], 0);
            }
            else
            {
                right = new BNode(data, n);
            }

        }

        private BNode(byte[]data, int []n)
        {
            if (data[n[0]++]%2==LEAF)
            {
                left = new BNode(data[n[0]++], 0);
            }
            else
            {
                left = new BNode(data, n);
            }

            if (data[n[0]++]%2==LEAF)
            {
                right = new BNode(data[n[0]++], 0);
            }
            else
            {
                right = new BNode(data, n);
            }
        }

        public byte [] toArray()
        {
            byte [] res = new byte[10000];
            int [] n = {0};
            toArray(res, n);
            byte []rd = new byte[n[0]];
            System.arraycopy(res, 0, rd, 0, n[0]);
            return rd;
        }

        private void buildDictionary(boolean [][]res,Stack<Boolean> stack )
        {
            if (isLeaf())
            {
                Boolean []b = new Boolean[stack.size()];
                res[value+128] = new boolean[stack.size()];
                stack.toArray(b);
                for (int i = 0; i< b.length; ++i)
                    res[value+128][i] = b[i].booleanValue();
                return;
            }
            else
            {
                stack.push(false);
                left.buildDictionary(res, stack);
                stack.pop();
                stack.push(true);
                right.buildDictionary(res, stack);
                stack.pop();

            }
        }
        public boolean [][] getDictionary()
        {
            boolean [][]res = new boolean[256][];
            Stack<Boolean> stack = new Stack<>();
            buildDictionary(res, stack);
            return res;
        }
    }


}
