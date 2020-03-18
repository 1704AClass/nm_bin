package com.ningmeng.manage_course.dao;

import com.alibaba.fastjson.JSON;

import java.util.Random;

/**
 * Created by wangb on 2020/3/17.
 */

public class zfc {
    //主方法
    public static void main(String[] args) throws Exception {
        Chou1();//调用抽奖方法
    }
    //抽奖方法
    public static void Chou(){
        Random rd = new Random();
        String str = "";
        for(int i=0;i<4;i++){
            String s = rd.nextInt(2)+"";
            str = str + randomSelect(s);
        }
        System.out.println("您抽奖的号码为"+str);
        System.out.println("抽奖结果为："+drawLottery(str));
    }
    public static void Chou1(){
        String str="{\"name\":\"xiao\"}";
        System.out.println(JSON.parse (str));
        System.out.println(JSON.toJSONString (str));
    }

    //奖项
    private static String drawLottery(String str) {
        //判断★和☆是否中奖
        if(str.equals("★★★★")){
            return "一等奖";
        }else if(str.equals("★★★☆")){
            return "二等奖";
        }else if(str.equals("★★☆☆")){
            return "三等奖";
        }else {
            return "谢谢参与!!!";
        }
    }

    //抽奖号码
    private static String randomSelect(String s) {
        //得到数值返回号码★和☆
        if(s.equals("1")){
            return "★";
        }else{
            return "☆";
        }
    }

}
