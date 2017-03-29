package vn.bluesky.crypt.huf;

import vn.bluesky.crypt.CryptResult;
import vn.bluesky.crypt.Hash;
import vn.bluesky.utils.data.ArrayUtil;
import vn.bluesky.utils.data.BitC;
import vn.bluesky.utils.data.FileC;

import java.io.File;

/**
 * Created by DuThien on 22/03/2017.
 */
public class HT_Decryptor {
    public final HT_KeyGen.BNode tree;
    public HT_Decryptor(HT_KeyGen.BNode tree)
    {
        this.tree = tree;
    }

    public CryptResult decryptFile(String path, String outPath)
    {
        long timex = System.currentTimeMillis();
        BitC in = BitC.openToRead(path);
        if (in== null) return new CryptResult(0, -1, false);
        FileC out = FileC.openToWrite(outPath);
        if (in== null) return new CryptResult(0, -1, false);
        byte [] md = new byte[16];
        in.forceReadByte(md);
        int size = in.readInt();
        byte []b = new byte[65536];
        int k = 0;
        for (int i = 0; i< size;++i)
        {
            HT_KeyGen.BNode t = tree;
            while (!t.isLeaf())
            {
                if (in.readBit())
                    t = t.right;
                else
                    t = t.left;
            }
            b[k++]= t.value;
            if (k== b.length)
            {
                out.write(b);
                k = 0;
            }
        }
        if (k>0)
            out.write(b, 0, k);
        in.close();
        out.close();

        if (ArrayUtil.equalArray(md, Hash.MD5File(outPath))) {
            return new CryptResult(System.currentTimeMillis() - timex, size, true);
        } else {
            new File(outPath).delete();

            return new CryptResult(System.currentTimeMillis() - timex, size, false);
        }
    }
}
