package com.example.testbignum.AEStest;

import android.os.Build;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    //  密钥长度：128，192或256
    private  static final int KEY_SIZE = 128;
    //  加密/解密算法名称
    private static final String ALGORITHM = "AES";
    //  随机数生成器（RNG）算法名称
    private static final String RNG_ALGORITHM = "SHA1PRNG";

    //  生成密钥对象
    private static SecretKey generateKey(byte[] key) throws Exception{
        SecureRandom random = null;
        if(Build.VERSION.SDK_INT >= 17){
            random = SecureRandom.getInstance(RNG_ALGORITHM,new CryptoProvider());
        }else{
            random = SecureRandom.getInstance(RNG_ALGORITHM);
        }
        //  创建安全随机数生成器
        //SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        //  设置 密钥key的字节数组作为安全随机数生成器的种子
        random.setSeed(key);

        //  创建AES算法生成器
        KeyGenerator gen = KeyGenerator.getInstance(ALGORITHM);
        //  初始化算法生成器
        gen.init(KEY_SIZE,random);

        //  生成AES密钥对象  也可直接创建密钥对象return new SecretKeySpec(key,ALGORITHM);
        return gen.generateKey();
    }

    //  明文加密
    public static byte[] encrypt(byte[] plainBytes,byte[] key) throws Exception{
        //  生成密钥对象
        SecretKey secKey = generateKey(key);
        //  获取AES密码器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //  初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE,secKey);
        //  加密数据 返回密文
        byte[] cipherBytes = cipher.doFinal(plainBytes);

        return cipherBytes;
    }

    //  密文解密
    public static byte[] decrypt(byte[] cipherBytes,byte[] key) throws Exception{
        //  生成密钥对象
        SecretKey seckey = generateKey(key);
        //  获取AES密码器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //  初始化密码器（解密模型）
        cipher.init(Cipher.DECRYPT_MODE,seckey);
        //  解密数据 返回明文
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return plainBytes;
    }

    public static byte[] decrypt1(byte[] content,String password){
        try{
            //  创建AES的Key生产者
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128,new SecureRandom(password.getBytes()));
            //  根据用户密码，生成一个密钥
            SecretKey secretKey = kgen.generateKey();
            //  返回基本编码格式密钥
            byte[] enCodeFormat = secretKey.getEncoded();
            //  转换为AES专用密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat,"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,key);
            byte[] result = cipher.doFinal(content);
            return result;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (NoSuchPaddingException e){
            e.printStackTrace();
        }catch (InvalidKeyException e){
            e.printStackTrace();
        }catch (IllegalBlockSizeException e){
            e.printStackTrace();
        }catch (BadPaddingException e){
            e.printStackTrace();
        }
        return null;
    }

    //  进制转换
    //  2进制转为16进制
    public static String parseByte2HexStr(byte buf[]){
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < buf.length;i++){
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if(hex.length() == 1){
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    //  16进制转为2进制
    public static byte[] parseHexStr2Byte(String hexStr){
        if(hexStr.length() < 1){
            return null;
        }
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i < hexStr.length()/2;i++){
            int high = Integer.parseInt(hexStr.substring(i*2,i*2+1),16);
            int low = Integer.parseInt(hexStr.substring(i*2+1,i*2+2),16);
            result[i] = (byte)(high * 16 + low);
        }
        return result;
    }
}
