package com.example.es_demo.Thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSyncByLockExample {
    private static Lock lock = new ReentrantLock();
    private static Condition A = lock.newCondition();
    private static Condition B = lock.newCondition();
    private static Condition C = lock.newCondition();

    private static int count = 0;

    static class ThreadA extends Thread {

        public void run(){
            try {
                lock.lock();
                for (int i=0; i<10; i++){
                    while (count % 3 != 0)
                        A.await(); //A释放lock锁
                    System.out.println("A");
                    count++;
                    B.signal(); //A执行完唤醒B线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadB extends Thread {

        public void run(){
            try {
                lock.lock();
                for (int i=0; i<10; i++){
                    while (count % 3 != 1)
                        B.await(); //B释放lock锁, 当前面A线程执行后会通过B.signal()唤醒该线程
                    System.out.println("B");
                    count++;
                    C.signal(); //B执行完唤醒C线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadC extends Thread {

        public void run(){
            try {
                lock.lock();
                for (int i=0; i<10; i++){
                    while (count % 3 != 2)
                        C.await(); //C释放lock锁, 当前面B线程执行后会通过C.signal()唤醒该线程
                    System.out.println("C");
                    count++;
                    A.signal(); //C执行完唤醒A线程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}
