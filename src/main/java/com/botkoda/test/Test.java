package com.botkoda.test;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class Test {
    private static final Logger log = Logger.getLogger(Test.class);
    private static final List<Integer> randoms = new LinkedList<>();
    private static Lock lock = new ReentrantLock();
    private static Condition cond = lock.newCondition();

    private static final Thread writer = new Thread(() -> {
//        блокировка на коллекцию
        synchronized (randoms) {

            System.out.println("пишу");
//            генерация массива со случайным кол-вом и значением с 0 до 100
            randoms.addAll(
                    new Random()
                            .ints(0, 100)
                            .limit(new Random().nextInt(100) - 1)
                            .boxed()
                            .collect(Collectors.toList())
            );
//            логируем
            log.info( "генерация массива:" +randoms);
            System.out.println();
            randoms.notify();
        }
    });

    private static final Thread reader = new Thread(() -> {
        synchronized (randoms) {
            try {
                while (!randoms.isEmpty()) {
                    System.out.println("читаю");
                    for (int i : randoms) {
                        System.out.print(i+", ");
                    }
                    randoms.clear();
                    log.info("очистка массива");
                }
//                если коллекция пустая то ждем
                randoms.wait();
            } catch (InterruptedException e) {
                log.error("",e.fillInStackTrace());
            }

        }


    });

    public static void main(String[] args) throws Exception {

        reader.setDaemon(true);
        writer.setDaemon(true);
        writer.start();
        reader.start();
        Thread.sleep(5000);

    }
}