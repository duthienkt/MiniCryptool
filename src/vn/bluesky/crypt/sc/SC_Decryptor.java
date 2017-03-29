package vn.bluesky.crypt.sc;

import javafx.application.Platform;
import vn.bluesky.crypt.CryptResult;
import vn.bluesky.crypt.Hash;
import vn.bluesky.utils.data.ArrayUtil;
import vn.bluesky.utils.data.FileC;

import java.io.File;

import static vn.bluesky.crypt.Hash.MD5File;

public class SC_Decryptor {
    protected byte[] transformation;
    protected byte [] rev;
    public SC_Decryptor(byte[] transformation) {
        this.transformation = transformation;
        this.rev = new byte[transformation.length];
        for (int i = 0; i< 256; ++i)
        {
            rev[transformation[i]+128]= (byte) (i-128);
        }
    }
    public void decrypt(byte[] input, byte[] output) {
        int length = input.length;
        for (int i=0; i < length; i++) {
            int index = input[i];
            index = index + 128;
            output[i] = this.rev[index];
        }
    }

    public CryptResult decryptFile(String path, String outPath)
    {
        long startT = System.currentTimeMillis();
        FileC inf = FileC.openToRead(path);
        FileC outf = FileC.openToWrite(outPath);
        byte[] bi = new byte[65536];
        byte[] bo = new byte[65536];
        byte[] md = new byte[16];
        inf.read(md);
        long size = inf.remain();
        while (inf.remain() > 0) {
            int read = inf.read(bi);
            decrypt(bi, bo);
            outf.write(bo, 0, read);
        }
        inf.close();
        outf.close();
        System.out.println(ArrayUtil.bytes2Hex(md)+"\n"+ArrayUtil.bytes2Hex(Hash.MD5File(outPath)) );

        if (ArrayUtil.equalArray(md, MD5File(outPath))) {
            final long timex = System.currentTimeMillis() - startT;

            return new CryptResult(timex, size, true);
        } else {
            final long timex = System.currentTimeMillis() - startT;
            new File(outPath).delete();
            return new CryptResult(timex, -1, false);
        }
    }
}
