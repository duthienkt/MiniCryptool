package vn.bluesky.crypt.sc;

import javafx.application.Platform;
import vn.bluesky.crypt.CryptResult;
import vn.bluesky.utils.data.ArrayUtil;
import vn.bluesky.utils.data.FileC;

import static vn.bluesky.crypt.Hash.MD5File;

/**
 * Created by DuThien on 22/03/2017.
 */
public class SC_Encryptor {
    protected byte[] transformation;

    public SC_Encryptor(byte[] transformation) {
        this.transformation = transformation;

    }
    public void encrypt(byte[] input, byte[] output) {
        int length = input.length;
        for (int i=0; i < length; i++) {
            int index = input[i];
            index = index + 128;
            output[i] = this.transformation[index];
        }
    }

    public CryptResult encryptFile(String path, String outPath)
    {
        long startT = System.currentTimeMillis();
        FileC inf = FileC.openToRead(path);
        FileC outf = FileC.openToWrite(outPath);

        outf.write(MD5File(path));
        byte[] bi = new byte[65536];
        byte[] bo = new byte[65536];
        long size = inf.remain();
        while (inf.remain() > 0) {

            int read = inf.read(bi);
            encrypt(bi, bo);
            outf.write(bo, 0, read);
        }
        inf.close();
        outf.close();
        final long timex = System.currentTimeMillis() - startT;
        return new CryptResult(timex, size, true);
    }
}