package com.xsh.test01;

public class DeadThreadTest {

        public static void main(String[] args) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 线程t1开始");
                new DeadThread();
            }, "t1").start();

            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 线程t2开始");
                new DeadThread();
            }, "t2").start();
        }


}
