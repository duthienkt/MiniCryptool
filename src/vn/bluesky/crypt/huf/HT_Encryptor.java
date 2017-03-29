package vn.bluesky.crypt.huf;

import vn.bluesky.crypt.CryptResult;
import vn.bluesky.crypt.Hash;
import vn.bluesky.utils.data.BitC;
import vn.bluesky.utils.data.FileC;

/**
 * Created by DuThien on 22/03/2017.
 */
public class HT_Encryptor {
    public final HT_KeyGen.BNode tree;
    boolean[][] dic;

    public HT_Encryptor(HT_KeyGen.BNode tree) {
        this.tree = tree;
        dic = tree.getDictionary();
    }

    public CryptResult encryptFile(String path, String outPath) {
        long startTime = System.currentTimeMillis();
        byte[] md = Hash.MD5File(path);
        FileC in = FileC.openToRead(path);
        BitC out = BitC.openToWrite(outPath);
        if (out == null) return new CryptResult(0, -1, false);
        out.forceWriteByte(md);

        int size = in.remain();
        out.writeInt(size);
        byte[] buff = new byte[65536];
        boolean[] dat;
        for (int i = 0; i < size; ) {
            int read = in.read(buff);
            for (int k = 0; k < read; ++k) {
                dat = dic[buff[k] + 128];
                for (int j = 0; j < dat.length; ++j)
                    out.writeBit(dat[j]);
            }
            i += read;
        }
        in.close();
        out.close();
        return new CryptResult(System.currentTimeMillis() - startTime, size, true);
    }
}
