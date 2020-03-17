package com.example.testbignum.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class encryptTag {
    public int p = 997;
    public static int g = 7;
    public int d;
    static String dateC;

    public void encryptMain(int a){
        //  获取私钥：x
//        int x = RandomNumber();   //  静态方法调用非静态方法
//        System.out.println("私钥x:" + x);
//        int a = 31;   //  a为位置标签

        //  加密上传
        sendLoc(a);

        //  注意所有除法运算用费马小定理求逆元（p为素数）

        //  解密判断             ka1,kb1,ka2,kb2,c,x
//        if(judgeNext(421,363,427,802, "202001281336",954)) {
//            System.out.println("双方邻近！");
//        } else {
//            System.out.print("双方不临近！");
//        }

    }

    //  解密判断是否邻近
    public boolean judgeNext(int ka1, int ka2, int u2, int u3, String date, int x) {
        boolean next0 = false;
        long t1, t2, c;
        long u1;
        c = getc(date);

        long ka21 = ModeP(ka2,p-2);  //  ka2的逆元ka21:= ka2^(p-2)
        u1 = ka1 * ka21;             //  u1 = ka1 * ka2^(-1)

        long u31 = ModeP(u3,p-2);   //  求u3的逆元u31 := u3^(-1) = u3^(p-2)
        t1 = ModeP(u2*u31,x);
        t2 = ModeP(u1,x);

        long j = ModeP(t1,c);      //  求t1^c
        long t21 = ModeP(t2,p-2);  //  求t2的逆元t21:=t2^(-1) = t2^(p-2);
        if( (j * t21) % p == 1) {
            next0 = true;
        }
        return next0;
    }

    //  加密获取当前c
    public int setc() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        dateC= df.format(date);   //  上传至服务器的c
        System.out.println("上传数据库的c:"+dateC);
        DateFormat dd = new SimpleDateFormat("dd");
        d = Integer.parseInt(dd.format(date));  // 用于计算的c
        System.out.println("当前计算的c:"+d);
        return d;
    }

    //  加密上传数据
    public String [] sendLoc(int a) {
        long ka1, ka2, s;
        String [] res = new String[3];
        int t = RandomNumber();  //  获取随机值[2,p-2]
        setc();
        s = d * t;
        System.out.println("d:"+d);
        int a1 = g;
        ka1 = ModeP(a1,s);
        s = a + t;
        ka2 = ModeP(a1,s);
        res[0] = String.valueOf(ka1); res[1] = String.valueOf(ka2); res[2] = dateC;
        System.out.printf("上传服务器的值（%d，%d，%s）",ka1,ka2,dateC);
        return res;
    }

    //  解密判断当前计算c
    public int getc(String date) {
        int d;
        d = Integer.parseInt(date.substring(6, 8));
        return d;
    }

    //  求(a^s) % p
    public long ModeP(long a,long s) {
        long sum = 1;
        a = a % p;

        while(s > 0) {
            if(s % 2 == 1) {
                sum = (sum * a) % p;
            }

            s /= 2;
            a = (a * a) % p;
        }

        return sum;
    }

    //  生成随机数
    public int RandomNumber() {
        int t = 0;
        Random r = new Random();
        t = r.nextInt(p-4) + 2;  // 生成[2,p-2]区间的整数
        //  System.out.println("t的值为："+t);
        return t;
    }

}
