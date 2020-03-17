package com.example.login_server.loginInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyTask extends AsyncTask<String,Integer,String> {
    private MessageResponse msgRes;
    public void setMsgRes(MessageResponse msgRes){
        this.msgRes = msgRes;
    }
    @Override
    protected String doInBackground(String[] params) {
        System.out.println(params[0]+params[1]);
        String param= "id="+params[0]+"&password=" + params[1];
        return this.sendPost("http://118.31.61.122:8080/Login/login",param);
    }

    public String sendPost(String url,String params){
        String result = "";
        try{
            URL realurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)realurl.openConnection();
            conn.setConnectTimeout(6000); //设置超时时间
            conn.setRequestMethod("POST");
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            //out.writeBytes(params);
            out.writeBytes(params);
            out.flush();
            out.close();
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null) {
                result = line;
            }
        } catch (MalformedURLException eio) {
            eio.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    protected void onPostExecute(String name){
        if(name != ""){
            msgRes.onReceivedSuccess(name);
            Toast.makeText(MainActivity.mactivity,"welcome!" + name,Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.mactivity,"id or password is wrong!",Toast.LENGTH_LONG).show();
        }
        msgRes.onReceivedSuccess(name);
    }
}
