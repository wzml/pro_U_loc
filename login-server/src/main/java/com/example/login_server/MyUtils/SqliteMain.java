package com.example.login_server.MyUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//  SQLite工具类
public class SqliteMain extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "AsSqlite";  //  数据库的名
    public static final String TABLE_Users = "userinfo";  //  表名：存储用户账号信息（id,name,password,online）/公钥(pubkey),私钥(seckey),包括多个用户
    public static final String TABLE_FRIENDS = "frinfo";  //  TABLE_NAME+表名：存储好友账号信息（id,name）/公钥(pubkey)/k1,k2,close,online,不同用户用不同表
    public static String TABLE_NAME = null;  //  表项前面name
//    public static SqliteMain mInstance = null;
//
//    public synchronized static SqliteMain getInstance(Context context){
//        if(mInstance == null){
//            mInstance = new SqliteMain(context);
//        }
//        return mInstance;
//    }

    public SqliteMain(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

//    public String getcpp(){
//        return  native_lib.stringFromJNI();
//    }

    @Override  //  运行app时就已创建用户信息表
    public void onCreate(SQLiteDatabase db) {   //  创建用户信息表以及好友信息表
        String sql = "create table if not exists " + TABLE_Users + "(id integer primary key,name varchar(10),password varchar(10)," +
                     "online char(1),pubkey varchar(250),seckey varchar(250),tag varchar(10))";
        db.execSQL(sql);
        //db.close();
    }

    @Override  //  更新数据库版本
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //  更新数据库
        if(newVersion > oldVersion){
            String sql = "DROP TABLE IF EXISTS " + TABLE_Users;
            db.execSQL(sql);
            sql = "DROP TABLE IF EXISTS " + TABLE_NAME+TABLE_FRIENDS;
            db.execSQL(sql);
            onCreate(db);
            db.close();
        }
    }

    //   根据用户昵称创建好友表
    public void setName(SQLiteDatabase db,String name){    //  设置好友信息表名前缀,并完善数据库表添加
        TABLE_NAME = name;
        //  创建该用户好友信息表
        createFriTable(db);
    }

    //  增：
    //  向用户信息表插入记录
    public void addUserRecord(SQLiteDatabase db,String id,String name,String password,String online,String h,String x){
//        ContentValues contentValues = new ContentValues();
//        //  插入用户信息 --  online = Y/N,默认tag为F
//        contentValues.put("id",id);contentValues.put("name",name);contentValues.put("password",password);
//        contentValues.put("online",online);contentValues.put("pubkey",h);contentValues.put("seckey",x);
//        contentValues.put("tag","F");
//        getWritableDatabase().insert(TABLE_Users,null,contentValues);
          String sql = "insert into "+TABLE_Users+"(id,name,password,online,pubkey,seckey,tag) values('"+
                                                  id+"','"+name+"','"+password+"','"+online+"','"+h+"','"+x+"','"+"F');";
          db.execSQL(sql);
          //  创建该用户的好友表
          setName(db,name);
    }

    //  向好友信息表插入记录 --  添加好友后一般不会插入好友是否在线及k1,k2信息
    public void addFriRecord(SQLiteDatabase db,int id,String name,String k){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);contentValues.put("name",name);contentValues.put("pubkey",k);
        db.insert(TABLE_NAME+TABLE_FRIENDS,null,contentValues);
    }

    //  新增好友信息表
    public void createFriTable(SQLiteDatabase db){
        String sql = "create table if not exists " + TABLE_NAME+TABLE_FRIENDS + "(id integer primary key,name varchar(10),pubkey varchar(250)," +
                                                                                "k1 varchar(250),k2 varchar(250),close char(1),online char(1))";
        db.execSQL(sql);
    }

    //  删：
    //  向表tableName 删除以id为主键的记录
    public void deleteRecord(SQLiteDatabase db,String id,String tableName){
        db.delete(tableName,"id = ?",new String[]{id});
    }

    //  删除所有好友表，清空用户表
    public void clearTable(SQLiteDatabase database){
        //  删除所有好友表
        try{
            Cursor c = database.rawQuery("select name from " + TABLE_Users,null);
            while(c.moveToNext()){
                TABLE_NAME = c.getString(c.getColumnIndex("name"));
                database.execSQL("drop table if exists "+TABLE_NAME+TABLE_FRIENDS);
            }
            close(c);
        }catch (Exception e){
            e.printStackTrace();
        }
        //  删除用户表所有信息
        //database.execSQL("drop table if exists "+ TABLE_Users);  //   删除用户表  ---当修改用户表格式时需要此步骤
        database.execSQL("delete from "+ TABLE_Users);
        //database.close();
    }

    //  改：
    //  更改数据库name+frinfo表中id为id的属性：sx，修改其值为val,
    public void updateNameFriRecord(SQLiteDatabase db,String myid,String id,String sx,String val){
        String name = queryUserRecord(db,myid,"name");
        TABLE_NAME = name;
        ContentValues contentValues = new ContentValues();
        contentValues.put(sx,val);
        db.update(TABLE_NAME+TABLE_FRIENDS,contentValues,"id=?",new String[]{id});
    }

    //  更改数据库USERS表中的id = id，x列的值为newx
    public void  updateUserInfo(SQLiteDatabase db,String id,String x,String newx){
        ContentValues contentValues = new ContentValues();
        contentValues.put(x,newx);
        db.update(TABLE_Users,contentValues,"id=?",new String[]{id});
        //getReadableDatabase().close();
    }

    //  查：
    //  查询用户信息 -- 已知id,查询y字段的值
    public String queryUserRecord(SQLiteDatabase db,String id,String y){
        String  res = null;
        Cursor cursor = db.query(TABLE_Users,null,"id='"+id+"'",null,null,null,null);
        if(cursor.moveToNext()){
            res = cursor.getString(cursor.getColumnIndex(y));
        }
        close(cursor);
        return res;
    }

    //  查询User表是否存在id用户  返回boolean:true/false
    public Boolean queryExistId(SQLiteDatabase db,String id){
        Boolean flag = false;
        Cursor cursor = db.query(TABLE_Users,null,"id='"+id+"'",null,null,null,null);
        int count = cursor.getCount(); //  查询到的总记录数
        if(count != 0){
            flag = true;
        }
        close(cursor);
        return flag;
    }

    //  查询User表哪个用户当前状态为在线
    public String queryOnlineId(SQLiteDatabase db){
        String id = null;
        Cursor cursor = db.query(TABLE_Users,new String[]{"id"},"online="+"'Y'",null,null,null,null);
        if(cursor.moveToNext()){
            id = cursor.getString(0);
        }
        close(cursor);  //  关闭连接
        return id;
    }

    //  关闭连接
    public void close(Cursor cursor) {
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
    }
}
