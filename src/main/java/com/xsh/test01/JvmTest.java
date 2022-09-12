package com.xsh.test01;

public class JvmTest {
    private static final String name="常量";

    private static int num=5;

    private String club="因你太美";



    static{
        System.out.println(name);
        System.out.println(num);

    }

    {
        System.out.println(club);
    }

    public JvmTest(){
        System.out.println("构造函数初始化");
    }

    public static void main(String[] args) {
        new JvmTest();


    }
}
