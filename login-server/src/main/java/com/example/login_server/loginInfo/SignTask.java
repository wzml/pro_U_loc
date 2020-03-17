package com.example.login_server.loginInfo;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignTask extends AsyncTask<String,Integer,String> {
    private MessageResponse msgRes;
    private String idzj;
    public void setMsgRes(MessageResponse msgRes){
        this.msgRes = msgRes;
    }
    @Override
    protected String doInBackground(String[] params) {
        String param = "name=" + params[0] + "&password=" + params[1] + "&pubkey=" + params[2] + "&seckey=" + params[3];
        return this.sendPost("http://118.31.61.122:8080/Login/sign",param);
    }

    public String sendPost(String url,String params){
        String result = "";
        try{
            URL realurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)realurl.openConnection();
            conn.setConnectTimeout(6000); //设置超时时间
            conn.setRequestMethod("POST");
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
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
    protected void onPostExecute(String id){   //  ps：俺也不知道那边得类返回方法为啥无法更新UI，使用Looper无效，更改为此处更新UI显示成功哈哈哈哈哈哈！！！
        if(id != ""){
            idzj = id;
            Toast.makeText(SignActivity.signtivity,"Sign Successful!",Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(SignActivity.signtivity);
            dialog.setTitle("注册成功：");
            dialog.setMessage("您注册的id是："+id);
            dialog.setPositiveButton("OK",new okClick());
            dialog.create();
            dialog.show();
            //msgRes.onReceivedSuccess(id);
        } else {
            Toast.makeText(SignActivity.signtivity,"Sign false!",Toast.LENGTH_LONG).show();
        }
    }

    class okClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog,int which){
            dialog.cancel();
            msgRes.onReceivedSuccess(idzj);
        }
    }
}
