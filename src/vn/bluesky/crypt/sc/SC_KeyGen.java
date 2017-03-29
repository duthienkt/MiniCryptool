package vn.bluesky.crypt.sc;

import vn.bluesky.crypt.xor.XOR;
import vn.bluesky.ui.DialogBox;
import vn.bluesky.utils.data.ArrayUtil;
import vn.bluesky.utils.data.FileC;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static vn.bluesky.crypt.Hash.MD5;

public class SC_KeyGen {

    public interface SC_KeyComplete{
       void  onComplete(SC_KeyGen res);
    }
    public static void  fromFile(String path, SC_KeyComplete onResultCallback, DialogBox.OnCancelCallback onCancelCallback)
    {
        FileC f = FileC.openToRead(path);
        if (f == null) return;
        byte [] key = new byte[256];
        byte [] checksum = new  byte[16];
        f.read(key);
        f.read(checksum);
        DialogBox.passwordDialog("Decrypt your key file", result -> {
            XOR kx = new XOR(result);
            kx.xor(key);
            if (ArrayUtil.equalArray(checksum, MD5(key)))
                onResultCallback.onComplete(new SC_KeyGen(key));
            else
                DialogBox.showAlert("Error", "Wrong password!");
        }, onCancelCallback);
        f.close();
    }


    public void saveToFile(String path, String password)
    {
        byte []md = MD5(transformation);
        FileC f = FileC.openToWrite(path);
        byte [] t = new byte[256];
        System.arraycopy(transformation, 0, t, 0, 256);
        XOR x = new XOR(password);
        x.xor(t);
        f.write(t);
        f.write(md);
        f.close();

    }



    protected byte[] transformation;
    public SC_KeyGen() {
        this.transformation = this.createRandomTransformation();
    }
    public SC_KeyGen(byte [] data) {
        this.transformation = data;
    }

    protected byte[] createRandomTransformation() {
        byte[] randomTransformation = this.createInitTransformation();
        Collections.shuffle(Arrays.asList(randomTransformation));
        return randomTransformation;
    }
    protected byte[] createInitTransformation() {
        byte[] initTransformation = new byte[256];
        boolean []check  = new boolean[256];
        for (int i = 0; i< 256; ++i)
            check[i] = true;
        Random random = new Random();
        for (int i = 0; i< 256; ++i)
        {
            int k = Math.abs(random.nextInt())%(256-i);
           // System.out.println(i+" "+ k);
            for (int j = 0; j< 256; ++j)
            {
                if (check[j])
                {
                    if ((--k)<0)
                    {
                        check[j] = false;
                        initTransformation[i] = (byte) (j-128);
                        break;
                    }
                }
            }


        }
        return initTransformation;
    }


    public byte[] getTransformation() {
        return this.transformation;
    }
}
