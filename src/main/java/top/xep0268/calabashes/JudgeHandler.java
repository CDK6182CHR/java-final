package top.xep0268.calabashes;

import top.xep0268.calabashes.items.Item;

import java.util.concurrent.*;
import java.util.*;

public class JudgeHandler implements Runnable {
    private static final JudgeHandler instance=new JudgeHandler();
    private final BlockingQueue<Judger> queue=new LinkedBlockingQueue<>(200000);
//    private Queue<Judger> queue=new LinkedList<>();
    public static JudgeHandler getInstance(){
//        if(instance==null)
//            instance=new JudgeHandler();
        return instance;
    }

    /**
     * 没有judger在队列里时，这里一直占用着queue，所以子线程全死掉了？？
     * 修订：每0.5个时钟周期调起一次，处理完队列就退出。
     */
    @Override
    public void run(){
//        System.out.println("Entering JudgeHandler::run()");
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timeout! JudgeHandler::run");
            }
        },300); //排除run()阻滞线程所致
        try{
//            synchronized (queue){
                while(!queue.isEmpty()){
                    Judger judger=queue.take();
                    judger.judge();
                }
//            }
        }catch (InterruptedException e){
            //正常退出？
            System.out.println("JudgeHandler::run interrupted");
        }
        timer.cancel();
//        System.out.println("Exiting JudgeHandler::run()");
    }

    public void addJudgeTask(Item living1, Item living2){
        long tm=System.currentTimeMillis();
        Judger judger=new Judger(living1, living2);
        System.out.println("addJudgeTask entered "+judger);
        try {
            queue.put(judger);
        }catch (InterruptedException e){
            System.out.println("addJudgeTask interrupted "+judger);
        }
        System.out.println("addJudgeTask exited "+judger+" tm="+(System.currentTimeMillis()-tm));
    }

    public int waitingCount(){
        return queue.size();
    }

}
