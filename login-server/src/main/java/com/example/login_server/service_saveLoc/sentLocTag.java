package com.example.login_server.service_saveLoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class sentLocTag {
    static {
        System.loadLibrary("native_lib");
    }
    //manageTxt txtHelper = new manageTxt();
    //socket连接服务器传输当前用户位置tag
    public void findserver(String myName,String k1,String k2,String ctr) throws IOException {
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7325),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        pw.write(myName+"\n");pw.flush();
        pw.write(k1+"\n");pw.flush();
        pw.write(k2+"\n");pw.flush();
        pw.write(ctr+"\n");pw.flush();
        os.close();
        Socin.close();
        socket.close();
    }

    public String findName(String id)throws IOException {
        String name = "";
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        pw.write(1+"\n");
        pw.flush();
        pw.write(id+"\n");
        pw.flush();
        //  获取当前用户name
        name = SocBuf.readLine();
        os.close();
        Socin.close();
        socket.close();
        return name;
    }

}
