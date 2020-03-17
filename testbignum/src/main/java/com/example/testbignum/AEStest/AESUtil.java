package com.example.testbignum.AEStest;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final int IV_SIZE = 16;
    private static final int KEY_SIZE = 32;
    private static final String IV_FILE_NAME = "AES_IV";
    private static final String SALT_FILE_NAME = "AES_SALT";

    public static byte[] enctypt(Context context,String data,String password){
        return encryptData(data.getBytes(),retrieveIv(context),deriveKeySecurely(context,password,KEY_SIZE));
    }

    public static byte[] decrypt(Context context,byte[] data,String password){
        return decryptData(data,retrieveIv(context),deriveKeySecurely(context,password,KEY_SIZE));
    }

    private static byte[] decryptData(byte[] data, byte[] iv, SecretKey key) {
        return encryptOrDecrypt(data,key,iv,false);
    }

    private static byte[] encryptData(byte[] data, byte[] iv, SecretKey key) {
        return encryptOrDecrypt(data,key,iv,true);
    }

    private static byte[] encryptOrDecrypt(byte[] data, SecretKey key, byte[] iv, boolean isEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(isEncrypt?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE,key,new IvParameterSpec(iv));
            return cipher.doFinal(data);
        }catch (GeneralSecurityException e){
            throw new RuntimeException("This is unconceivale!",e);
        }
    }

    private static SecretKey deriveKeySecurely(Context context, String password, int keySize) {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(),retrieveSalt(context),100,keySize*8);
        try{
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes,"AES");
        }catch (Exception e){
            throw new RuntimeException("Deal with exceptions properly!",e);
        }
    }

    private static byte[] retrieveSalt(Context context) {
        byte[] salt = new byte[KEY_SIZE];
        readFromFileOrCreateRandom(context,SALT_FILE_NAME,salt);
        return salt;
    }

    private static byte[] retrieveIv(Context context) {
        byte[] iv = new byte[IV_SIZE];
        readFromFileOrCreateRandom(context,IV_FILE_NAME,iv);
        return iv;
    }

    private static void readFromFileOrCreateRandom(Context context, String fileName, byte[] iv) {
        if(fileExists(context,fileName)){
            readBytesFromFile(context,fileName,iv);
            return;
        }
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        writeToFile(context,fileName,iv);
    }

    private static void writeToFile(Context context, String fileName, byte[] bytes) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Couldn't write to" + fileName,e);
        }
    }

    private static void readBytesFromFile(Context context, String fileName, byte[] bytes) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            int numBytes = 0;
            while (numBytes < bytes.length){
                int n = fis.read(bytes,numBytes,bytes.length-numBytes);
                if(n <= 0){
                    throw new RuntimeException("counldn't read from" + fileName);
                }
                numBytes += n;
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Counldn't read from" + fileName,e);
        }
    }

    private static boolean fileExists(Context context, String FileName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),FileName);
        return file.exists();
    }
}