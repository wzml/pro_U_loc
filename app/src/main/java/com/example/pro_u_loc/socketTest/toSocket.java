package com.example.pro_u_loc.socketTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class toSocket {

    public String getvalue(String y,String yz,String x) throws IOException {
        String value = "";
        Socket socket  = new Socket();
        try{
            socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        }catch(Exception e){
            e.printStackTrace();
        }

        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);
        int num = 21;  //21表示查找id = id的x项值
        System.out.println(num);
        //根据输入num服务器对数据库进行不同操作
        pw.write(num+"\n");pw.flush();
        pw.write(y+"\n");pw.flush();
        pw.write(yz+"\n");pw.flush();
        pw.write(x+"\n");pw.flush();
        //返回数据库是否操作成功
        value = SocBuf.readLine();  //获取操控结果
        System.out.println(value);
        os.close();
        Socin.close();
        socket.close();
        return value;
    }

}
