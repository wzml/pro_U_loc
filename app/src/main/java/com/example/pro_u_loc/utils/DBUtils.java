package com.example.pro_u_loc.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static final String TAG = "DBUtils";

    private static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");  // 加载驱动
            String ip = "118.31.61.122";
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/" + dbName,"ju","Judge123");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public  static String judgeUserinfo(int i,String id,String password) { //返回值为null或name
        Connection conn = getConnection("test");
        try{
            Statement st = conn.createStatement();
            if(i == 1) { //登录查询信息
                String name = null;
                String sql = "select name from people where id = '" + id + "' and password = '" + password + "'";
                ResultSet res = st.executeQuery(sql);
                if(res == null) {
                    return null;
                } else {
                    if(res.next()) { //指向下一行，也就是第一行结果集
                        name = res.getString("name");
                    }
                }
                conn.close();
                st.close();
                res.close();
                return name;

            } else if(i == 2) { //注册账号信息
                String id1 = null;
                String sql = "select id from people";
                ResultSet res = st.executeQuery(sql);
                if(res == null) { //没有任何用户
                    id1 = "12345678";
                    return id1; //创建一个用户账号为12345678
                } else {  //有用户信息
                        res.last(); //res在结果集最后一行
                        id1 = res.getString("id"); //此时id1存储最后一个Id账号
                        int id2 = Integer.valueOf(id1) + 1;
                        id1 = String.valueOf(id2);   //id+1
                }
                conn.close();
                st.close();
                res.close();
                return id1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(conn != null) { //关闭连接
                try{
                    conn.close();
                } catch (SQLException e1){
                    e1.printStackTrace();
                }
            }
            Log.d(TAG,"数据操作异常");
            return null;
        }
        return null; //i既不是1也不是2
    }

    //插入数据
    public static int insertmessage(String id,String name,String password) {
        Connection conn1 = getConnection("test");
        try{
            //Statement st = conn1.createStatement();
            String sql = "insert into people (id,name,password) values (?,?,?);";
            PreparedStatement pstm = conn1.prepareStatement(sql);
            //通过setString给3个问号赋值
            pstm.setString(1,id);
            pstm.setString(2,name);
            pstm.setString(3,password);
            //更新数据库
            pstm.executeUpdate();
            conn1.close();
            pstm.close();  //关闭访问
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            if(conn1 != null) { //关闭连接
                try{
                    conn1.close();
                } catch (SQLException e1){
                    e1.printStackTrace();
                }
            }
            Log.d(TAG,"数据操作异常");
            return 0;
        }
    }

}
