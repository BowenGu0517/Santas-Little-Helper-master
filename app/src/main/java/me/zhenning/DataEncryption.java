package me.zhenning;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by user on 2016/4/12.
 */
public class DataEncryption {
    private String TAG= "DataEncryption";
    private int mBlocksize;
    private SecretKey mKey;

    public DataEncryption() {
        mBlocksize = 128;
        KeyGenerator kgen;
        mKey = null;
        try {

            byte[] encodedKey     = Base64.decode("r1GqRCkvIk3zKDOz5CoLy8PegwutVgQ3", Base64.DEFAULT);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

            mKey = originalKey;

            /*
            kgen = KeyGenerator.getInstance("AES");
            mKey = kgen.generateKey();
            String stringKey = Base64.encodeToString(mKey.getEncoded(), Base64.DEFAULT);

            Log.d(TAG,"Key:" + stringKey);
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long encryptFile(String encFilepath,String origFilepath) {
        long start, stop, seconds;
        seconds = -1;

        try{

            Log.d(TAG, "Crypto: Encrypting file:" + origFilepath);

            // open stream to read origFilepath. We are going to save encrypted contents to outfile
            InputStream fis = new FileInputStream(origFilepath);
            File outfile = new File(encFilepath);
            int read = 0;
            if (!outfile.exists())
                outfile.createNewFile();

            FileOutputStream encfos = new FileOutputStream(outfile);
            // Create Cipher using "AES" provider
            Cipher encipher = Cipher.getInstance("AES");
            encipher.init(Cipher.ENCRYPT_MODE, mKey);
            CipherOutputStream cos = new CipherOutputStream(encfos, encipher);

            // capture time it takes to encrypt file
            start = System.nanoTime();
            Log.d(TAG, String.valueOf(start));

            byte[] block = new byte[mBlocksize];

            while ((read = fis.read(block,0,mBlocksize)) != -1) {
                cos.write(block,0, read);
            }
            cos.close();
            stop = System.nanoTime();

            Log.d(TAG, String.valueOf(stop));
            seconds = (stop - start) / 1000000;// for milliseconds
            Log.d(TAG, String.valueOf(seconds));

            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return seconds;
    }

    public long decryptFile(String decFilepath, String encFilepath) {
        long start, stop, seconds;
        seconds = -1;

        try{
            Log.d(TAG, "Crypto: Decrypting file:" + encFilepath);

            // open stream to read encFilepath. We are going to save decrypted contents to outfile
            InputStream fis = new FileInputStream(encFilepath);
            File outfile = new File(decFilepath);
            int read = 0;
            if (!outfile.exists())
                outfile.createNewFile();

            FileOutputStream decfos = new FileOutputStream(outfile);
            // Create Cipher using "AES" provider
            Cipher decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, mKey);
            CipherInputStream cis = new CipherInputStream(fis, decipher);

            // capture time it takes to decrypt file
            start = System.nanoTime();
            Log.d(TAG, String.valueOf(start));

            byte[] block = new byte[mBlocksize];

            while ((read = cis.read(block,0,mBlocksize)) != -1) {
                decfos.write(block,0, read);
            }
            cis.close();
            stop = System.nanoTime();

            Log.d(TAG, String.valueOf(stop));
            seconds = (stop - start) / 1000000;// for milliseconds
            Log.d(TAG, String.valueOf(seconds));

            decfos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return seconds;

    }

    public void setBlocksize(int blocksize) {
        mBlocksize = blocksize;
    }
    
}
