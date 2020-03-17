package com.example.testbignum.md5test;

public class testmain {
    public static void main(String[] args){
        MD5Encode md5Encode = new MD5Encode();
        System.out.println(md5Encode.getTime());
        String toserver = md5Encode.MD5EncodeUtf8("1","12354");
        System.out.println(toserver);
        toserver = md5Encode.MD5EncodeUtf8("1","12354");
        System.out.println(toserver);
        System.out.println(md5Encode.getTime());
        System.out.println(md5Encode.MD5EncodeUtf8("12354","1"));
    }
}
