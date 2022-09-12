package com.xsh.test01;

import com.xsh.handler.RepeatRequestException;

public class TestException {

    public static void main(String[] args) {
        try{
            String response = "因短时间内频繁访问，ip已被拉黑，拒绝访问;" ;


            throw new RepeatRequestException(response);

        }catch(Throwable e){
            System.out.println("进入捕获");
           // e.printStackTrace();
            System.out.println("捕获异常");

        }
    }
}
