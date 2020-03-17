package com.example.testbignum.AEStest;

public class AEStest {
    public static void main(String[] args) throws Exception{
        String content = "102009862644640823556228816978959977369968650415611247890918102926379260326023414536954187279361835941277010271182216927649680652608774960480147639492";
        String xxxxxxx = "102009862644640823556228816978959977369968650415611247890918102926379260326023414536954187279361835941277010271182216927649680652608774960480147639492";
        //String hexstr= "2CDA728B4C6C50F91104CF868E36770D3FDCBE7B81450BDA2EAC6F38E8A9F391E6326738BF4F23B3AF0A05B791F8A64CEA8FBB3AD0CC806D36223FFF4CE2D2B6D8F732BE6FFCDB8FFCDE7EC9E19099331BBD9B789725F1668AB7B067FDD3823E066D720098F0AB69C944591989FC9F019192B989E46F562E1450073D4AD9D6284A10351D4B51D26FEA0F940D8B74DD219AF7440A3B0BB47AEE739FEFDB43D477";
        String key = "1";
        AESUtilsz aesUtilsz = new AESUtilsz();
        String data = "cdsabjdkhsiuaykjadasca";
        System.out.println("加密数据："+data+"\n");
        String str = aesUtilsz.encrypt(data,"1");
        System.out.println("加密结果：\n"+str);
        System.out.println("解密结果：\n"+aesUtilsz.decrypt("str","1"));
//        byte[] cipherBytes = AESUtils.encrypt(content.getBytes(),key.getBytes());
//        String hexstr = AESUtils.parseByte2HexStr(cipherBytes);
//        System.out.println("加密后的数据为："+hexstr);
//        byte[] twostr = AESUtils.parseHexStr2Byte(hexstr);
//        byte[] plainBytes = AESUtils.decrypt(twostr,key.getBytes());
//        System.out.println("解密后的明文为："+new String(plainBytes));
    }
}
