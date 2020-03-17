package com.example.socket_test.tcp;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutionException;

public class TcpServer {
    public static void main(String [] args) throws Exception{
        try{
            //建立套接字
            ServerSocket server = new ServerSocket(2300);
            //监听
            Socket socket = server.accept();
            //建立连接
            InputStreamReader Sysin = new InputStreamReader(System.in);
            BufferedReader SysBuf = new BufferedReader(Sysin);

            InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
            BufferedReader SocBuf = new BufferedReader(Socin);

            PrintWriter Socout = new PrintWriter(socket.getOutputStream());

            //通信
            System.out.println("Client:" + SocBuf.readLine());
            String readline = SysBuf.readLine();
            while(!readline.equals("bye")){
                Socout.println(readline);
                Socout.flush();
                //System.out.println("Server:" + readline);

                String rl = SocBuf.readLine();
                if(!rl.equals("ok"))
                    System.out.println("Client:"+rl);
                else
                    break;

                readline = SysBuf.readLine();
            }

            //关闭IO/socket
            Socout.close();
            Socin.close();
            server.close();
        } catch (Exception e){
            System.out.println("Error:"+e);
        }
    }
}
