package com.example.testbignum.AEStest;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtilsz {
    public String ivParameter = "1234567890123456";
    public byte[] keyp = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p'};
    private static final String CiperMode = "AES/CBC/PKCS5Padding";

    public String encrypt(String encData,String key)throws Exception{
        try{
            Cipher cipher = Cipher.getInstance(CiperMode);
            byte[] raw = genkey(key,true);  //  生成16位长的密钥
            SecretKeySpec skeySpec = new SecretKeySpec(raw,"AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());  //  使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE,skeySpec,iv);
            //"utf-8"
            byte[] encrypted = cipher.doFinal(encData.getBytes());
            return Base64.encodeToString(encrypted,Base64.DEFAULT);
            //return encrypted.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public String decrypt(String decData,String key) throws Exception{
        try {
            byte[] encrypted1 = Base64.decode(decData.getBytes(),Base64.DEFAULT);
            byte[] raw = genkey(key,false);
            Cipher cipher = Cipher.getInstance(CiperMode);
            SecretKeySpec skeySpec = new SecretKeySpec(raw,"AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE,skeySpec,iv);
            //byte[] encrypted1 = decData.getBytes();
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original,"utf-8");
            return originalString;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private byte[] genkey(String key,boolean enc){
        byte[] key0;
        byte[] key1 = new byte[16];
//        if (enc == false){
//            try {
//                key0 = key.getBytes("ASCII");
//            } catch (UnsupportedEncodingException e) {
//                key0 = key.getBytes();
//                e.printStackTrace();
//            }
//        }else
            key0 = key.getBytes();
        if(key.length() < 16){
            int i;
            for(i = 0;i < key.length();i++){
                key1[i] = key0[i];
            }
            for ( i = key.length();i < 16;i++){
                key1[i] = keyp[i];
            }
            //key1[16] = '\0';
            return key1;
        }else if (key.length() > 16){
            int i;
            for(i = 0;i < 14;i++){
                key1[i] = key0[i];
            }
            key1[14] = key0[key.length()-1];
            key1[15] = key0[key.length()-2];
            //key1[16] = '\0';
            return key1;
        }
        return key0;
    }

}
