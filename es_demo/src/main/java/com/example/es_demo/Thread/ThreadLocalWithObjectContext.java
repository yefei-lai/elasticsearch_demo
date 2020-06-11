package com.example.es_demo.Thread;

import com.example.es_demo.model.MovieModel;

/**
 * 如何使用 ThreadLocal
 */
public class ThreadLocalWithObjectContext implements Runnable {

    private static ThreadLocal<MovieModel> threadLocal = new ThreadLocal<>();

    private String movieName;

    public ThreadLocalWithObjectContext(String movieName){
        this.movieName = movieName;
    }

    @Override
    public void run() {
        threadLocal.set(new MovieModel(movieName));
        System.out.println("thread movieModel for given name: " + movieName + " is: " + threadLocal.get());
    }


    public static void main(String[] args) {
        ThreadLocalWithObjectContext threadLocalWithObjectContext1 = new ThreadLocalWithObjectContext("升级");
        ThreadLocalWithObjectContext threadLocalWithObjectContext2 = new ThreadLocalWithObjectContext("少数派报告");
        new Thread(threadLocalWithObjectContext1).start();
        new Thread(threadLocalWithObjectContext2).start();
    }
}
