package com.example.testbignum.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testbignum.native_lib;

//  SQLite工具类
public class SqliteMain extends SQLiteOpenHelper {
    static {
        System.loadLibrary("native_lib");
    }

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "zllsqlite";  //  数据库的名
    public static final String TABLE_Users = "userinfo";  //  表名：存储用户账号信息（id,name,password,encryp,encryg）/公钥(pubkey),私钥(seckey),包括多个用户
    public static final String TABLE_FRIENDS = "frinfo";  //  TABLE_NAME+表名：存储好友账号信息（id,name）/公钥(pubkey),不同用户用不同表
    public static String TABLE_NAME = null;  //  表项前面name

    public SqliteMain(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public String getcpp(){
        return  native_lib.getRand();
    }
    //   根据用户昵称创建好友表
    public void setName(String name){    //  设置好友信息表名前缀,并完善数据库表添加
        TABLE_NAME = name;
        //  创建该用户好友信息表
        SQLiteDatabase db = this.getReadableDatabase();
        createFriTable(db);
    }

    @Override  //  运行app时就已创建用户信息表
    public void onCreate(SQLiteDatabase db) {   //  创建用户信息表以及好友信息表
        String sql = "create table if not exists " + TABLE_Users + "(id integer primary key,name varchar(10),password varchar(10)," +
                     "encryp varchar(250),encryg varchar(250),pubkey varchar(250),seckey varchar(250))";
        db.execSQL(sql);
    }

    @Override  //  更新数据库版本
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //  更新数据库
        if(newVersion > oldVersion){
            String sql = "DROP TABLE IF EXISTS " + TABLE_Users;
            db.execSQL(sql);
            sql = "DROP TABLE IF EXISTS " + TABLE_NAME+TABLE_FRIENDS;
            db.execSQL(sql);
            onCreate(db);
        }
    }

    //  增：
    //  向用户信息表插入记录
    public void addUserRecord(int id,String name,String password,String p,String g,String x,String h){
        ContentValues contentValues = new ContentValues();
        //  插入用户信息
        contentValues.put("id",id);contentValues.put("name",name);contentValues.put("password",password);
        contentValues.put("encryp",p);contentValues.put("encryg",g);contentValues.put("pubkey",h);contentValues.put("seckey",x);
        getWritableDatabase().insert(TABLE_Users,null,contentValues);

        //  创建该用户的好友表
        setName(name);
    }

    //  向好友信息表插入记录
    public void addFriRecord(int id,String name,String k){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);contentValues.put("name",name);contentValues.put("pubkey",k);
        getWritableDatabase().insert(TABLE_NAME+TABLE_FRIENDS,null,contentValues);
    }

    //  新增好友信息表
    public void createFriTable(SQLiteDatabase db){
        String sql = "create table if not exists " + TABLE_NAME+TABLE_FRIENDS + "(id integer primary key,name varchar(10),pubkey varchar(250))";
        db.execSQL(sql);
    }

    //  删：
    //  向表tableName 删除以id为主键的记录
    public void deleteRecord(String id,String tableName){
        getWritableDatabase().delete(tableName,"id = ?",new String[]{id});
    }

    //  删除所有好友表，清空用户表
    public void clearTable(){
        SQLiteDatabase database = this.getReadableDatabase();
        //  删除所有好友表
        int i = 0;
        Cursor c = database.rawQuery("select name from " + TABLE_Users,null);
        while(c.moveToNext()){
            TABLE_NAME = c.getString(c.getColumnIndex("name"));
            database.execSQL("drop table if exists "+TABLE_NAME+TABLE_FRIENDS);
        }
        c.close();
        //  删除用户表所有信息
        database.execSQL("delete from "+ TABLE_Users);
    }

    //  改：
    //  更改数据库name+frinfo表中id为id的属性：sx，修改为val,
    public void updateRecord(String id,String sx,String val){
        ContentValues contentValues = new ContentValues();
        contentValues.put(sx,val);
        getWritableDatabase().update(TABLE_NAME+TABLE_FRIENDS,contentValues,"id=?",new String[]{id});
    }

    //  查：
    //  查询用户信息 -- 根据id
    public void queryUserRecord(String id,SQLiteDatabase db){
        String name = null,p = null,g = null,h = null,x = null;
        Cursor cursor = db.query(TABLE_Users,null,"id="+id,null,null,null,null);
        int count = cursor.getCount(); //  查询到的总记录数
        while(cursor.moveToNext()){
            name = cursor.getString(2);  //  第一列应当是id
            p = cursor.getString(4);
            g = cursor.getString(5);
            h = cursor.getString(6);
            x = cursor.getString(7);
        }
        cursor.close();  //  关闭连接
    }
}
