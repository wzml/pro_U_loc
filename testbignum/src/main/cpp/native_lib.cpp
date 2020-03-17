//
// Created by Lenovo on 2020/2/10.
//

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
#include <assert.h>
#include <openssl/aes.h>

BIGNUM *p = NULL, *g = NULL;
BIGNUM *x = NULL, *h = NULL;
BIGNUM *y = NULL, *k = NULL;
BIGNUM *ka1 = BN_new(), *ka2 = BN_new(),*ka = BN_new();
char str[250] = "981145611331336492272075765433436969079895053417314256701166191570572651304538152921873131048866194648648706663948709137272031151543192112419746424199";
char str1[250] = "895622041693019842074795319491210883528892654009909996384050941433948981362125895299678516801404809930015331445582247175993790631937889007010196719387";
//  加密并判断是否邻近
void encryLocSub(BIGNUM *a) {
    BIGNUM *t = BN_new();

    BIGNUM *one = BN_new();
    BN_one(one); // one = 1
    BN_rand_range(t,p); // 0=<x<p
    while(BN_cmp(t,one) < 1){    //t <= 1
        BN_rand_range(t,p);  //  重新生成随机数 --- x赋值
    }
    BN_free(one); //  释放a的内存空间

    BN_CTX *ctx = BN_CTX_new();
    BN_mod_exp(ka1,g,t,p,ctx); //  h赋值, ka1 = g^t mod p
    //  生成ka2
    BIGNUM *r = BN_new(),*r1 = BN_new(),*r2 = BN_new();
    BN_mul(r,a,h,ctx);   // r = a*h
    BN_mul(r1,r,x,ctx);  //  r1 = r*x = a*h*x
    BN_add(r,a,r1);   //  r = a+r1 = a+a*h*x
    BN_add(r1,r,t);  //  r1 = r+t = t+a+a*h*x
    BN_mod_exp(ka2,g,r1,p,ctx);    //  ka2 = g^r1 mod p = g^(t+a+a*h*x) mod p

    //  生成ka
    BN_mul(r,a,k,ctx);  //  r = a*k
    BN_mod_exp(r1,k,r,p,ctx);  //  r1 = k^r mod p = k^(a*k) mod p
    BN_add(r,a,t);  // r = a+t
    BN_mod_exp(r2,g,r,p,ctx);  //  r2 = g^r mod p = g^(a+t) mod p
    BN_mod_mul(ka,r1,r2,p,ctx);  //  ka = (r1*r2) mod p
    BN_free(r);BN_free(r1);BN_free(r2);
    BN_CTX_free(ctx);
}

//   初始化p,g,x,h,y,k
void initnum(int comm){
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
    if(comm == 1  && y == NULL){  // 初始化y,k
        y = BN_new();
        k = BN_new();
        BIGNUM *one = BN_new();
        BN_one(one); // one = 1
        BN_rand_range(y,p); // 0=<y<p
        while(BN_cmp(y,one) < 1){    //y <= 1
            BN_rand_range(y,p);  //重新生成随机数 --- y赋值
        }
        BN_free(one); //释放a的内存空间

        BN_CTX *ctx = BN_CTX_new();
        BN_mod_exp(k,g,y,p,ctx); //k赋值, k = g^y mod p
        BN_CTX_free(ctx);
    }
}

// 仅加密位置a
void encryLoc(BIGNUM *a){
    BIGNUM *t = BN_new();

    //  获取随机数x
    BIGNUM *one = BN_new();
    BN_one(one); // one = 1
    BN_rand_range(t,p); // 0=<x<p
    while(BN_cmp(t,one) < 1){    //t <= 1
        BN_rand_range(t,p);  //  重新生成随机数 --- x赋值
    }
    BN_free(one); //  释放a的内存空间

    //  生成ka1
    BN_CTX *ctx = BN_CTX_new();
    BN_mod_exp(ka1,h,t,p,ctx); //  h赋值, ka1 = g^t mod p
    //  生成ka2
    BIGNUM *r = BN_new(),*r1 = BN_new(),*r2 = BN_new();
    //BN_mul(r,a,h,ctx);   // r = a*h
    BN_mod_exp(r,g,a,p,ctx);  //  r = g^a mod p
    BN_mul(r1,a,t,ctx);  //  r1 = a*t
    BN_mod_exp(r2,h,r1,p,ctx);  //  r2 = h^(at) mod p
    //BN_add(r,a,r1);   //  r = a+r1 = a+a*h*x
    //BN_add(r1,r,t);  //  r1 = r+t = t+a+a*h*x
    //BN_mod_exp(ka2,g,r1,p,ctx);    //  ka2 = g^r1 mod p = g^(t+a+a*h*x) mod p
    BN_mod_mul(ka2,r,r2,p,ctx);  //  ka2 = (r * r2) mod p = (g^a % p)*(h^(at) % p) mod p
    BN_free(r);BN_free(r1);BN_free(r2);
    BN_CTX_free(ctx);
}

//  encbuf存储加密后的数据
void AES_ECB_enc(unsigned char aes_keybuf[16+1], unsigned char buf[160], unsigned char Encbuf[160]){
    AES_KEY aesKey;
    AES_set_encrypt_key(aes_keybuf,128,&aesKey);

    //  数据加密
    for(int i = 0;i < 160;i += 16){
        AES_encrypt(buf+i,Encbuf+i,&aesKey);
    }
}

//  Decbuf存储解密后的数据
void AES_ECB_dec(unsigned char (&rsa_keybuf)[16+1],unsigned char(&Encbuf)[160],unsigned char (&Decbuf) [160]){
    AES_KEY rsa_aeskey;
    AES_set_decrypt_key(rsa_keybuf,128,&rsa_aeskey);
    //  数据解密
    for(int i= 0;i < 160;i+=16){
        AES_decrypt(Encbuf+i,Decbuf+i,&rsa_aeskey);
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_testbignum_native_1lib_getRand(JNIEnv * env, jclass clazz) {
    FILE *fd;
    char path[] = "/sdcard/NeedInfo/info.txt";
    fd = fopen(path,"rw+");
    if(fd == NULL){
        return env->NewStringUTF("con't open file");
    }
    char buffer[200] = "csdcsda";
    //memset(buffer,0,200); //  初始化
    char buffer1[20] = "sdsasa";
    fprintf(fd,"%s\n",buffer);
    fputs(buffer1,fd);  //  写入txt文本
    fputc('\n',fd);
    fputs(buffer1,fd);
    fputc('\n',fd);
    //fscanf(fd,"%[^\n]",buffer); //  读取一行字符，中间是正则表达式，表示不遇到换行符就一直读
    //fgetc(fd);  //  将文件句柄向后移动一个字符，相当于跳过\n换行符，下次就是从下一行开始读取
   // fread(buffer, sizeof(char),5,fd);
    fclose(fd);
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

//  加密
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_testbignum_native_1lib_getAesEnc(JNIEnv *env, jclass clazz, jstring key,
                                                  jstring content) {
    unsigned  char aes_keybuf[16+1];
    memset(aes_keybuf,'1',16);
    aes_keybuf[16] = '\0';
    char *key1 = const_cast<char *>((env)->GetStringUTFChars(key, 0));
    int j = strlen(key1);  //  密码长度
    for(int i = 0;i < j;i++){
        aes_keybuf[i] = *(key1+i);
    }
    unsigned char buf[160]={0};
    memset(buf,'a',159);
    buf[159] = '\0';
    char *content1 = const_cast<char *>((env)->GetStringUTFChars(content, 0));
    j = strlen(content1);
    for(int i = 0;i < j;i++){
        buf[i] = *(content1+i);
    }
    unsigned char Encbuf[160];  //  aes加密数据
    unsigned char Decbuf[160];
    memset(Encbuf,0, sizeof(Encbuf));
    memset(Decbuf,0, sizeof(Decbuf));
    AES_ECB_enc(aes_keybuf,buf,Encbuf);
    char *das;
    j = sizeof(Encbuf);
    for(int i = 0;i < j;i++){
        *(das+i) = Encbuf[i];
    }
    //int t = strlen(buf);
    return env->NewStringUTF(das);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_testbignum_native_1lib_getAesDec(JNIEnv *env, jclass clazz, jstring key,jstring content) {

    unsigned  char aes_keybuf[16+1];
    memset(aes_keybuf,'1',16);
    aes_keybuf[16] = '\0';
    char *key1 = const_cast<char *>((env)->GetStringUTFChars(key, 0));
    int j = strlen(key1);  //  密码长度
    for(int i = 0;i < j;i++){
        aes_keybuf[i] = *(key1+i);
    }
    unsigned char Encbuf[160];  //  aes加密数据
    unsigned char Decbuf[160];  //  aes解密数据
    memset(Encbuf,0, sizeof(Encbuf));
    memset(Decbuf,0, sizeof(Decbuf));
    char *content1 = const_cast<char *>((env)->GetStringUTFChars(content, 0));
    j = strlen(content1);
    for(int  i = 0;i < j;i++){
        Encbuf[i] = *(content1+i);
    }
    AES_ECB_dec(aes_keybuf,Encbuf,Decbuf);
    char *das;
    j = sizeof(Decbuf);
    for(int i = 0;i < j;i++){
        *(das+i) = Decbuf[i];
    }
    return env->NewStringUTF(das);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_testbignum_native_1lib_testaes(JNIEnv *env, jclass clazz, jstring key,
                                                jstring content) {
    // TODO: implement testaes()
    const unsigned char *userkey;
    unsigned char date[AES_BLOCK_SIZE*3];
    unsigned char encrypt[AES_BLOCK_SIZE*3 + 4];
    unsigned char plain[AES_BLOCK_SIZE*3];
    AES_KEY aesKey;

    memset((void *)userkey,'k',AES_BLOCK_SIZE);
    memset((void *)date,'p',AES_BLOCK_SIZE*3);
    memset((void *)encrypt,0,AES_BLOCK_SIZE*6);
    memset((void *)plain,0,AES_BLOCK_SIZE*3);

    //设置加密key及密钥长度
    AES_set_encrypt_key(userkey, AES_BLOCK_SIZE * 8, &aesKey);
}