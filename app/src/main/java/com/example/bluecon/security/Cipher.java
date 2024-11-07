package com.example.bluecon.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Date;

public class Cipher {
    private static final String ARIA_KEY = "v1N5S2Nc&hYdRo92n#vI*Hv1&";
    private static final int ARIA_BLOCK_SIZE = 16;
    private static final int TIME_LENGTH = 7;
    private static final String ADD_TAIL_TXT = "vIn5s2n";

    public static String getTimeMiliTime() {
        Date date = new Date();
        String rtv = date.getTime() + "";

        try {
            int startLength = rtv.length() - TIME_LENGTH;
            rtv = rtv.substring(startLength);
            //System.out.println(rtv);
        } catch (Exception e) {
            return "";
        }

        return rtv;
    }

    public static String encrypt(String data, byte[] key, int keySize, String charset) throws UnsupportedEncodingException, InvalidKeyException {
        byte[] encrypt = null;
        if (charset == null) {
            encrypt = BlockPadding.getInstance().addPadding(data.getBytes(), ARIA_BLOCK_SIZE);
        } else {
            encrypt = BlockPadding.getInstance().addPadding(data.getBytes(charset), ARIA_BLOCK_SIZE);
        }

        ARIAEngine engine = new ARIAEngine(keySize);
        engine.setKey(key);
        engine.setupEncRoundKeys();

        int blockCount = encrypt.length / ARIA_BLOCK_SIZE;
        for (int i = 0; i < blockCount; i++) {

            byte[] buffer = new byte[ARIA_BLOCK_SIZE];
            System.arraycopy(encrypt, (i * ARIA_BLOCK_SIZE), buffer, 0, ARIA_BLOCK_SIZE);

            buffer = engine.encrypt(buffer, 0);
            System.arraycopy(buffer, 0, encrypt, (i * ARIA_BLOCK_SIZE), buffer.length);
        }

        return Base64.toString(encrypt);
    }

    public static String decrypt(String data, byte[] key, int keySize, String charset) throws UnsupportedEncodingException, InvalidKeyException {

        byte[] decrypt = Base64.toByte(data);

        ARIAEngine engine = new ARIAEngine(keySize);
        engine.setKey(key);
        engine.setupDecRoundKeys();

        int blockCount = decrypt.length / ARIA_BLOCK_SIZE;
        for (int i = 0; i < blockCount; i++) {

            byte[] buffer = new byte[ARIA_BLOCK_SIZE];
            System.arraycopy(decrypt, (i * ARIA_BLOCK_SIZE), buffer, 0, ARIA_BLOCK_SIZE);

            buffer = engine.decrypt(buffer, 0);
            System.arraycopy(buffer, 0, decrypt, (i * ARIA_BLOCK_SIZE), buffer.length);
        }

        if (charset == null) {
            return new String(BlockPadding.getInstance().removePadding(decrypt, ARIA_BLOCK_SIZE));
        } else {
            return new String(BlockPadding.getInstance().removePadding(decrypt, ARIA_BLOCK_SIZE), charset);
        }
    }

    public static String Encode(String value) {
        value = value.trim() + ADD_TAIL_TXT;
        String result = "";
        try {
            String addKey = getTimeMiliTime();
            String ChangeKey = ARIA_KEY + addKey;

            byte[] key = ChangeKey.getBytes();
            result = Cipher.encrypt(value, key, key.length * 8, "UTF-8");
            result = result + addKey;

        } catch (Exception e) {
            return "error";
        }
        return result;
    }

    public static String Decode(String value) {
        String result = "";
        try {
            int startLength = value.length() - TIME_LENGTH;

            String addKey = value.substring(startLength);
            value = value.substring(0, startLength);

            String ChangeKey = ARIA_KEY + addKey;

            byte[] key = ChangeKey.getBytes();
            result = Cipher.decrypt(value, key, key.length * 8, "UTF-8");

            int find = result.indexOf(ADD_TAIL_TXT);
            result = result.substring(0, find);

        } catch (Exception e) {
            return "error";
        }

        return result;
    }
}
