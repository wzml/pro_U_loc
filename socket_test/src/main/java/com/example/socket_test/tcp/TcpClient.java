package com.example.socket_test.tcp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {
    public static void main(String[] args) throws Exception{
        try{
            //建立套接字

            //Socket socket = new Socket("172.16.254.9",2300);
            //Socket socket = new Socket("172.16.177.12",2300);
            Socket socket  = null;
            socket = new Socket();
            socket.connect(new InetSocketAddress("118.31.61.122",7327),8000);
            //建立连接
            InputStreamReader Sysin = new InputStreamReader(System.in);
            BufferedReader SysBuf = new BufferedReader(Sysin);

            InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
            BufferedReader SocBuf = new BufferedReader(Socin);

            PrintWriter Socout = new PrintWriter(socket.getOutputStream());

            //进行通信
            String readline = SysBuf.readLine();
            while(!readline.equals("bye")){
                Socout.println(readline);
                Socout.flush();
                //System.out.println("Client:"+readline);

                System.out.println("Server:"+SocBuf.readLine());
                readline = SysBuf.readLine();
            }

            //关闭IO和Socket
            Socout.close();
            Socin.close();
            socket.close();
        } catch (Exception e){
            System.out.println("Error:"+e);
        }
    }
}
