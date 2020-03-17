package com.example.socket_test.service_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class addFriendUtils {

    //socket连接服务器传输当前用户位置tag
    public void findserver(String myName,String tag) throws IOException {
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7325),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        pw.write(myName+"\n");
        pw.flush();
        pw.write(tag+"\n");
        pw.flush();
        os.close();
        Socin.close();
        socket.close();
    }

}
