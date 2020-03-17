package com.example.login_server.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class addFriendUtils {

    //socket连接服务器判断是否存在id用户，返回其name
    public String[] findserver(String myid,String id,String num) throws IOException {
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        System.out.println(num);
        //1表示查找，后面接id
        if(num.equals("1"))
        {
            pw.write(1+"\n");
            pw.flush();
            pw.write(myid+"\n");
            pw.flush();
            pw.write(id+"\n");
            pw.flush();
        } else if(num.equals("2")){   //2表示添加好友发送申请
            pw.write(2+"\n");
            pw.flush();
            pw.write(myid+"\n");
            pw.flush();
            pw.write(id+"\n");
            pw.flush();
        } else if(num.equals("3")){  //查找好友信息
            pw.write(3+"\n");
            pw.flush();
            System.out.println(myid);
            pw.write(myid+"\n");
            pw.flush();
        } else {         //4,查找被邀请好友姓名列表
            pw.write(4+"\n");
            pw.flush();
            pw.write(myid+"\n");
            pw.flush();
        }

        String name[] = new String[10];
        if(num.equals("3")){    //返回name（昵称）数组
            int i = 0;
            String name1 = SocBuf.readLine();
            while(!name1.equals("F")){ //服务器传来的值不是F就一直获取好友姓名
                if(!name1.equals(""))
                    name[i] = name1;
                //name[i] = name1;
                System.out.println(name1);
                if(!name1.equals(""))//不等于“”才自加
                    i++;
                name1 = SocBuf.readLine();
            }
            name[i] = "F";
        }else if(num.equals("4")){
            int i = 0;
            String name1 = SocBuf.readLine();
            while(!name1.equals("F")){ //服务器传来的值不是F就一直获取好友姓名
                if(!name1.equals("")) {
                    name[i] = name1;
                    i++;
                }
                name1 = SocBuf.readLine();
            }
            name[i] = "F";
        } else{
           name[0] = SocBuf.readLine();
           System.out.println(name[0]);
        }
        os.close();
        Socin.close();
        socket.close();
        return name;
    }

    public String invite(String myid, String name, int num) throws IOException{
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        System.out.println(num);
        //根据输入num服务器对数据库进行不同操作
        pw.write(num+"\n");pw.flush();
        pw.write(myid+"\n");pw.flush();
        pw.write(name+"\n");pw.flush();
        //返回数据库是否操作成功
        String res = "F";
        res = SocBuf.readLine();  //获取操控结果
        System.out.println(res);
        os.close();
        Socin.close();
        socket.close();
        return res;
    }

    //输入myid，好友name，查找添加状态--服务端还未添加响应操作
    public String findCondi(String myid, String name) throws IOException{
        String conDi = null;
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);
        int num = 13;  //13表示查找用户名为
        System.out.println(num);
        //根据输入num服务器对数据库进行不同操作
        pw.write(num+"\n");pw.flush();
        pw.write(myid+"\n");pw.flush();
        pw.write(name+"\n");pw.flush();
        //返回数据库是否操作成功
        conDi = SocBuf.readLine();  //获取操控结果
        System.out.println(conDi);
        os.close();
        Socin.close();
        socket.close();
        return conDi;
    }

    //  输入用户第y项值yz，查找其的第x项值 操作数字以2开头
    public String getvalue(String y,String yz,String x) throws IOException{
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

    //  已知用户第y项值为yz,修改用户第x项为xz
    public void updatevalue(String y,String yz,String x,String xz)throws IOException{
        String value = "";
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);
        int num = 31;  //31表示修改
        System.out.println(num);
        //根据输入num服务器对数据库进行不同操作
        pw.write(num+"\n");pw.flush();
        pw.write(y+"\n");pw.flush();
        pw.write(yz+"\n");pw.flush();
        pw.write(x+"\n");pw.flush();
        pw.write(xz+"\n");pw.flush();
        //返回数据库是否操作成功
        value = SocBuf.readLine();  //获取操控结果
        System.out.println(value);
        os.close();
        Socin.close();
        socket.close();
    }

    //  已知用户名，返回字符串数组
    public ArrayList getFriName(String myname) throws IOException{
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);
        //41表示获取在线好友
        //System.out.println(num);
        //根据输入num服务器对数据库进行不同操作
        pw.write("41\n");pw.flush();
        pw.write(myname+"\n");pw.flush();
        //  返回数据库是否操作成功
        ArrayList list = new ArrayList();
        String value = SocBuf.readLine();
        while(!value.equals("F")){  //  服务器传来的值不是F就一直获取字符串的值
            if(!value.equals(""))
                list.add(value);
            value = SocBuf.readLine();
        }
        os.close();
        Socin.close();
        socket.close();
        return list;
    }

    //  已知好友昵称，返回所有k1,k2   若好友ctr与该用户ctr之差>10 则判为不临近 (用户gps不准确不能及时更新tag位置信息)
    public ArrayList getFriK(String myctr,ArrayList friName)throws IOException{
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);
        //  42表示获取在线好友的k值
        //根据输入num服务器对数据库进行不同操作
        pw.write("42\n");pw.flush();
        pw.write(myctr+"\n");pw.flush();  //  上传当前用户ctr
        pw.write(friName.size()+"\n");pw.flush();  //  传送好友数量
        String friName1;
        for(int i = 0;i < friName.size();i++){  //  传送所有好友name
            friName1 = friName.get(i).toString();
            pw.write(friName1+"\n");pw.flush();
        }
        //  返回数据库是否操作成功
        ArrayList list = new ArrayList();
        String value = SocBuf.readLine();
        while(!value.equals("F")){  //  服务器传来的值不是F就一直获取字符串的值
            if(!value.equals(""))
                list.add(value);
            value = SocBuf.readLine();
        }
        os.close();
        Socin.close();
        socket.close();
        return list;
    }

}
