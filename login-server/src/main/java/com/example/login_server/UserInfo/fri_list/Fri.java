package com.example.login_server.UserInfo.fri_list;

public class Fri {
    private int imageId;
    private String name;
    private String conDi;
    public Fri(int imageId,String name){
        super();
        this.imageId = imageId;
        this.name = name;
    }
//    public Fri(int imageId,String name,String conDi){ //重载函数
//        super();
//        this.imageId = imageId;
//        this.name = name;
//        this.conDi = conDi;
//    }
    public int getImageId(){
        return imageId;
    }
    public void setImageId(int imageId){
        this.imageId = imageId;
    }
    public  String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setConDi(String conDi){
        this.conDi = conDi;
    }
    public String getconDi(){
        return conDi;
    }
}
