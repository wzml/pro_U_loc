#include <jni.h>
#include <string.h>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <openssl/bn.h>
#include <iostream>
#include <random>
#include <time.h>

BIGNUM *p = NULL, *g = NULL;
BIGNUM *x = NULL, *h = NULL;
//BIGNUM *y = NULL, *k = NULL;
BIGNUM *a = NULL;
BIGNUM *k1 = NULL, *k2 = NULL;
char str[250] = "981145611331336492272075765433436969079895053417314256701166191570572651304538152921873131048866194648648706663948709137272031151543192112419746424199";
char str1[250] = "895622041693019842074795319491210883528892654009909996384050941433948981362125895299678516801404809930015331445582247175993790631937889007010196719387";
//  加密并判断是否邻近
//void encryLocSub(BIGNUM *a) {
//    BIGNUM *t = BN_new();
//
//    BIGNUM *one = BN_new();
//    BN_one(one); // one = 1
//    BN_rand_range(t,p); // 0=<x<p
//    while(BN_cmp(t,one) < 1){    //t <= 1
//        BN_rand_range(t,p);  //  重新生成随机数 --- x赋值
//    }
//    BN_free(one); //  释放a的内存空间
//
//    BN_CTX *ctx = BN_CTX_new();
//    BN_mod_exp(ka1,g,t,p,ctx); //  h赋值, ka1 = g^t mod p
//    //  生成ka2
//    BIGNUM *r = BN_new(),*r1 = BN_new(),*r2 = BN_new();
//    BN_mul(r,a,h,ctx);   // r = a*h
//    BN_mul(r1,r,x,ctx);  //  r1 = r*x = a*h*x
//    BN_add(r,a,r1);   //  r = a+r1 = a+a*h*x
//    BN_add(r1,r,t);  //  r1 = r+t = t+a+a*h*x
//    BN_mod_exp(ka2,g,r1,p,ctx);    //  ka2 = g^r1 mod p = g^(t+a+a*h*x) mod p
//
//    //  生成ka
//    BN_mul(r,a,k,ctx);  //  r = a*k
//    BN_mod_exp(r1,k,r,p,ctx);  //  r1 = k^r mod p = k^(a*k) mod p
//    BN_add(r,a,t);  // r = a+t
//    BN_mod_exp(r2,g,r,p,ctx);  //  r2 = g^r mod p = g^(a+t) mod p
//    BN_mod_mul(ka,r1,r2,p,ctx);  //  ka = (r1*r2) mod p
//    BN_free(r);BN_free(r1);BN_free(r2);
//    BN_CTX_free(ctx);
//}

// 仅加密位置a
void encryLoc(){
    BIGNUM *t = BN_new();
    //  获取随机数t
    BIGNUM *one = BN_new();
    BN_one(one); // one = 1
    BN_rand_range(t,p); // 0=<t<p
    while(BN_cmp(t,one) < 1){    //t <= 1
        BN_rand_range(t,p);  //  重新生成随机数 --- t赋值
    }
    //free(one);
    BN_free(one); //  释放1的内存空间

    //  生成k1
    BN_CTX *ctx = BN_CTX_new();
    BN_mod_exp(k1,h,t,p,ctx); //  k1 = h^t mod p
    //  生成k2
    BIGNUM *r = BN_new(),*r1 = BN_new();
    BN_mul(r,a,t,ctx);   // r = a*t
    BN_mod_exp(r1,h,r,p,ctx);  //  r1 = h^r mod p = h^(at) mod p
    BN_mod_exp(r,g,a,p,ctx);  //  r = g^a mod p
    //BN_mul(r1,a,t,ctx);  //  r1 = a*t
    //BN_mod_exp(r2,h,r1,p,ctx);  //  r2 = h^(at) mod p
    //BN_add(r,a,r1);   //  r = a+r1 = a+a*h*x
    //BN_add(r1,r,t);  //  r1 = r+t = t+a+a*h*x
    //BN_mod_exp(ka2,g,r1,p,ctx);    //  ka2 = g^r1 mod p = g^(t+a+a*h*x) mod p
    BN_mod_mul(k2,r1,r,p,ctx);  //  k2 = (r1 * r) mod p = (h^(at) % p)*(g^a % p) mod p
    BN_free(r);BN_free(r1);
    BN_CTX_free(ctx);
}

//  生成公钥与私钥:x,h
void pubAndsec(){
    p = BN_new();
    g = BN_new();
    BN_dec2bn(&p,str); //p赋值
    BN_dec2bn(&g,str1); //g赋值
    if(x == NULL){ //x,h赋值
        x = BN_new();
        h = BN_new();
        BIGNUM *one = BN_new();
        BN_one(one); // one = 1
        BN_rand_range(x,p); // 0=<x<p
        while(BN_cmp(x,one) < 1){    //x <= 1
            BN_rand_range(x,p);  //重新生成随机数 --- x赋值
        }
        BN_free(one); //释放a的内存空间

        BN_CTX *ctx = BN_CTX_new();
        BN_mod_exp(h,g,x,p,ctx); //h赋值, h = g^x mod p
        BN_CTX_free(ctx);
    }
}

//  将两个大整数写至txt
char write2totxt(BIGNUM *txt1,BIGNUM *txt2){
    char res = 'N';
    FILE *fd;
    char path[] = "/sdcard/NeedInfo/info.txt";
    fd = fopen(path,"rw+"); //  追加读写方式
    if(fd == NULL){
        return res;
    }
    //   向txt文本按行存入txt1,txt2;
    char *resh;
    resh = BN_bn2dec(txt1);
    fprintf(fd,"%s\n",resh);  //  存入公钥
    char *resx;
    resx = BN_bn2dec(txt2);
    fprintf(fd,"%s\n",resx);  //  存入私钥
    //fscanf(fd,"%[^\n]",buffer); //  读取一行字符，中间是正则表达式，表示不遇到换行符就一直读
    //fgetc(fd);  //  将文件句柄向后移动一个字符，相当于跳过\n换行符，下次就是从下一行开始读取
    // fread(buffer, sizeof(char),5,fd);
    OPENSSL_free(resh);
    OPENSSL_free(resx);
    fclose(fd);
    return 'Y';
}

//  从txt获取第i行字符串
char* getLineOfTxt(int i){
    FILE *fd;
    char path[] = "/sdcard/NeedInfo/info.txt";
    fd = fopen(path,"rw+");
    if(fd == NULL){
        return NULL;
    }
    char *buffer,*gj;
    buffer = (char *)malloc(200);gj = (char *)malloc(200);  //  必须先申请空间！！！
    for(int j = 0;j < i-1;j++){  //  前面的跳过
        fscanf(fd,"%[^\n]",gj); //  读取一行字符，中间是正则表达式，表示不遇到换行符就一直读
        fgetc(fd);  //  将文件句柄向后移动一个字符，相当于跳过\n换行符，下次就是从下一行开始读取
    }
    fscanf(fd,"%[^\n]",buffer);
    fgetc(fd);
    // fread(buffer, sizeof(char),5,fd);
    fclose(fd);
    return buffer;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_login_1server_MyUtils_native_1lib_stringFromJNI(JNIEnv *env, jclass clazz) {
    char *buffer;
    buffer = getLineOfTxt(2);
    return env->NewStringUTF(buffer);
    //  仅加密
//   if (p == NULL){
//        initnum(0);
//    }
    //   加密并判断邻近
//    if (p == NULL){
//        initnum(1);
//    }

    //  仅加密 a为位置坐标
//    BIGNUM *a = BN_new();
//    BN_one(a);
//    //  计算仅加密所需时间
//    int start,finish;
//    int duration;
//    start = clock();
//    encryLoc(a);  // 加密位置a
//    //encryLocSub(a);  //  加密并判断是否邻近
//    finish = clock();
//    duration = finish - start;  //  单位：ms
//    char hello1[1024];
//    sprintf(hello1,"%dms",duration); //将整型转为字符型
//    //const char *ww = "ms";
//    //strcat(hello1,ww);
//    //hello1 = BN_bn2dec(ka2);
//    //std::string hello  = hello1;
//    //std::string hello = std::to_string(duration) + "ms";  //  c++11标准增加的全局函数std::to_string,将整型转为字符型
//    BN_free(g);BN_free(p);BN_free(x);BN_free(h);
//    BN_free(ka1),BN_free(ka2);
//    return env->NewStringUTF(hello1);
}

//  获取公钥与私钥
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_login_1server_MyUtils_native_1lib_getHX(JNIEnv *env, jclass clazz) {
    pubAndsec();  //  初始化生成私钥公钥
    //  将公钥私钥写至txt
    char res = write2totxt(h,x);
    BN_free(p);BN_free(g);BN_free(h);BN_free(x);
    p = NULL,g = NULL,h = NULL,x = NULL;
    if(res == 'N'){
        return env->NewStringUTF("N");
    }
    return env->NewStringUTF("Y");
}

//  获取加密信息k1,k2
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_login_1server_MyUtils_native_1lib_getk1k2(JNIEnv *env, jclass clazz, jstring tag,jstring h1) {
    //  jstring转换
    char *stra = const_cast<char *>((env)->GetStringUTFChars(tag, 0));
    char *strh = const_cast<char *>((env)->GetStringUTFChars(h1, 0));
    //  初始化p,g,h,a
    if(p == NULL){
        p = BN_new();g = BN_new();
        BN_dec2bn(&p,str); //  p赋值
        BN_dec2bn(&g,str1); //  g赋值
    }
   if (h == NULL) {
       h = BN_new();a = BN_new();
       BN_dec2bn(&h,strh); //  h赋值
       BN_dec2bn(&a,stra);  // a赋值
   }
   if(k1 == NULL){  //  k1,k2初始化
       k1 = BN_new();
       k2 = BN_new();
   }
   //  加密--得到k1,k2
   encryLoc();
   //  写至txt
   char res = write2totxt(k1,k2);
   BN_free(p);BN_free(g);
   BN_free(h);BN_free(a);
   BN_free(k1);BN_free(k2);
   p = NULL,g = NULL;
   h = NULL,a = NULL;
   k1 = NULL,k2 = NULL;
    if(res == 'N'){
        return env->NewStringUTF("N");
    }
    return env->NewStringUTF("Y");
}

//  判断好友是否邻近，邻近返回“Y”,反之返回“N”
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_login_1server_MyUtils_native_1lib_judClose(JNIEnv *env, jclass clazz) {
    if(p == NULL){
        p = BN_new();g = BN_new();
        BN_dec2bn(&p,str); //  p赋值
        BN_dec2bn(&g,str1); //  g赋值
    }
    char *V1 = getLineOfTxt(1);  //  获取第一行
    char *V2 = getLineOfTxt(2);  //  获取第二行
    char *tag = getLineOfTxt(3);  //  获取第三行
    char loc = 'N';

    if(V1 != NULL && V2 != NULL && tag != NULL){
        //  初始化大整数
        BIGNUM *Vb1 = BN_new();BIGNUM *Vb2 = BN_new();BIGNUM *a = BN_new();
        BN_dec2bn(&Vb1,V1);BN_dec2bn(&Vb2,V2);BN_dec2bn(&a,tag);
        //  计算邻近与否
        BN_CTX *ctx = BN_CTX_new();
        BIGNUM *r = BN_new(),*r1 = BN_new(),*r2 = BN_new();

        BN_mod_exp(r,Vb1,a,p,ctx);    //  r = Vb1^a mod p
        BN_mod_exp(r1,g,a,p,ctx);     // r1 = g^a mod p
        BN_mod_mul(r2,r1,r,p,ctx);    // r2 = (r1 * r) mod p = (g^a % p)*(Vb1^a % p) mod p
        BN_mod_inverse(r,Vb2,p,ctx);  //  r = 1/Vb2 mod p :r是Vb2的模逆,如果计算错误r = NULL
        BN_mod_mul(r1,r,r2,p,ctx);   //  r1 = (r * r2) mod p = ((g^a * Vb1^a)/Vb2) mod p
        if(BN_is_one(r1) == 1){  //  r1 == 1,两用户邻近
            loc = 'Y';
        }

        BN_free(r);BN_free(r1);BN_free(r2);
        BN_free(Vb1);BN_free(Vb2);BN_free(a);
        BN_CTX_free(ctx);
    }

    BN_free(p);BN_free(g);
    p = NULL,g = NULL;

    if(loc == 'N')  //  不邻近
        return env->NewStringUTF("N");
    return env->NewStringUTF("Y");
}