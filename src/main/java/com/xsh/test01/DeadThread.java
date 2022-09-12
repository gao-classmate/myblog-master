package com.xsh.test01;

public class DeadThread {

        static {
            if (true) {
                System.out.println(Thread.currentThread().getName() + "\t 初始化当前类");
                while(true) {

                }
            }
        }

}
