package com.example.es_demo.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ABC交替打印10次
 */
public class ThreadSyncExample implements Runnable{

    private String name;
    private Object prev;
    private Object self;

    public ThreadSyncExample(String name, Object prev, Object self){
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count = 10;
        while ( count > 0) {
            synchronized (prev){ //先获取 prev 锁
                synchronized (self){ //再获取 self 锁
                    System.out.println(name);
                    count--;

                    self.notifyAll();//唤醒其他线程竞争self锁，注意此时self锁并未立即释放。
                }
                // 此时执行完self的同步块，这时self锁才释放
                try {
                    if (count == 0){//如果count==0,表示这是最后一次打印，通过notifyAll()操作释放对象锁（使线程停止）
                        prev.notifyAll();
                    }else {
                        prev.wait();//立即释放 prev锁，当前线程休眠，等待唤醒
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws Exception{

//        Thread thread1 = new Thread(() ->
//                System.out.println("A")
//                );
//        Thread thread2 = new Thread(() ->
//                System.out.println("B")
//        );
//        Thread thread3 = new Thread(() ->
//                System.out.println("C")
//        );
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        int i = 0;
//        while (i <= 10){
//            executorService.submit(thread1);
//            executorService.submit(thread2);
//            executorService.submit(thread3);
//            i ++;
//        }

        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        ThreadSyncExample pa = new ThreadSyncExample("A", c, a);
        ThreadSyncExample pb = new ThreadSyncExample("B", a, b);
        ThreadSyncExample pc = new ThreadSyncExample("C", b, c);

        new Thread(pa).start();
        Thread.sleep(100);//保证ABC的启动顺序
        new Thread(pb).start();
        Thread.sleep(100);
        new Thread(pc).start();
        Thread.sleep(100);
    }
}
