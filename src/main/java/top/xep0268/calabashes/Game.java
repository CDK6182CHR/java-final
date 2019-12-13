package top.xep0268.calabashes;

import top.xep0268.calabashes.field.BattleField;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.items.*;
import top.xep0268.calabashes.formations.*;
import top.xep0268.calabashes.log.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 游戏总控平台和主窗口实现。
 * 主界面用Pane实现。
 */
public class Game implements Runnable{
    private BattleField field=BattleField.getInstance();
    private Elder elder;
    private SnakeDemon snakeDemon;
    public static final int INTERVAL=400;  //时钟周期毫秒数
    private GridPane pane;  //程序主界面
    @FXML
    private ListView<String> listView,calabashList,demonList;
//    private TableView

    private boolean executing=false;

    private List<Living>
            activeLivings=new ArrayList<>(),
            deadLivings=new ArrayList<>();
    public volatile long startTime;//记录按下空格键的时间
    private ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(20);
    private JudgeHandler judgeHandler=JudgeHandler.getInstance();

    private EventWriter eventWriter;

    private static final Game instance=new Game();//只在类加载的时候创建一个对象
    public static Game getInstance(){
        return instance;
    }
    private boolean inVideo=false;

    private Game(){
        //创建界面
        initUI();
        System.out.println("Game init ok");
    }

    private void initUI() {
//        pane=new HBox();
//        GridPane gridPane=field.getGridPane();
//        pane.prefHeightProperty().bind(gridPane.prefHeightProperty());
//        HBox.setHgrow(gridPane, Priority.ALWAYS);
//        listView=new ListView<>();
//        calabashList=new ListView<>();
//        demonList=new ListView<>();
//        HBox subHBox=new HBox(calabashList,demonList);
//        VBox vBox=new VBox(listView,subHBox);
//        Platform.runLater(()->{
//            pane.getChildren().add(gridPane);
//            pane.getChildren().add(vBox);
//        });
//        pane.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                System.out.println("event grabbed by mainPane "+event.getCode());
//            }
//        });
//

        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/MainPane.fxml"));
//            pane = FXMLLoader.load(getClass().getResource("/MainPane.fxml"));
            pane=loader.load();
//            pane.setOnKeyPressed(event -> System.out.println("Grab event in Game::initUI "+event.getCode()));
            MainPaneController controller=loader.getController();
            listView=controller.listView;
            listView.setEventDispatcher(new EventDispatcher() {
                @Override
                public Event dispatchEvent(Event event, EventDispatchChain tail) {
                    return tail.dispatchEvent(event);
                }
            });
            listView.setOnKeyPressed(null);
            calabashList=controller.calabashList;
            calabashList.setOnKeyPressed(null);
            demonList=controller.demonList;
            calabashList.setOnKeyPressed(null);
//            ColumnConstraints constraints=new ColumnConstraints();
//            constraints.setPercentWidth(80);
//            pane.getColumnConstraints().add(constraints);
//            constraints=new ColumnConstraints();
//            constraints.setPercentWidth(10);
//            pane.getColumnConstraints().add(constraints);
//            constraints=new ColumnConstraints();
//            constraints.setPercentWidth(10);
//            pane.getColumnConstraints().add(constraints);
        }catch (IOException e){
            System.err.println("caught IOException");
            e.printStackTrace();
        }
    }

    public void showKillEvent(Item living1, Item living2){
        String s=living1.livingName()+" kills "+living2.livingName()+" on "+currentTimeStamp();
        Platform.runLater(()-> listView.getItems().add(s));
    }

    public boolean isExecuting(){
        return executing;
    }

    public Pane getMainPane(){
        return pane;
    }

    /**
     * 2019.12.08，改为public，由Main调用。
     */
    public void initFormations(){
        clearCurrentTurn();
        elder=new Elder(new Position(0,0),field,this);
        snakeDemon=new SnakeDemon(new Position(Field.N-1,0),
                field,this,6);
        System.out.println("after init");
        field.draw();
        elder.embattleFormation(GooseFormation.class);
        snakeDemon.embattleFormation(SwingFormation.class);
        field.draw();
    }

    public boolean isInVideo(){
        return inVideo;
    }

    /**
     * 读取记录文件，并转发到相应的处理方法。
     * 处理方法概要
     * <ul>
     *     <li>对<code>LivingCreatedEvent</code>，创建对应的对象，启动线程。</li>
     *     <li>对<code>LivingMoveEvent</code>，将事件分发到<code>Living</code>对象。</li>
     *     <li>对<code>KillEvent</code>，直接在本类中定时处理。</li>
     * </ul>
     * 注意：应保证读取文件的耗时<b>远远小于</b>时钟周期时长。
     * @param file 指定的记录文件名，应当保证文件有效。
     */
    public void loadLog(File file){
        startTime=System.currentTimeMillis();
        inVideo=true;
        executing=true;
        clearCurrentTurn();
        try(EventReader reader=new EventReader(file)){
            AbstractEvent event0;
            List<AbstractEvent> list=new LinkedList<>();
            while((event0=reader.read())!=null){
                //转发消息
                list.add(event0);
            }
            Collections.sort(list);
            for(AbstractEvent event:list){
                if(event.getClass()==LivingCreatedEvent.class){
                    loadLiving((LivingCreatedEvent)event);
                }
                else if(event.getClass()==LivingMoveEvent.class){
                    scheduleMove((LivingMoveEvent)event);
                }
                else if(event.getClass()==KillEvent.class){
                    scheduleKill((KillEvent)event);
                }
            }
            scheduler.scheduleAtFixedRate(this,40,
                    INTERVAL,TimeUnit.MILLISECONDS);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,e.toString());
        }
    }

    private void loadLiving(LivingCreatedEvent event){
        Living living=event.getSubject();
        living.setInVideo(field,this);
        System.out.println("Living created: "+living+" @ "+event.getPosition()+
                "timestamp: "+event.getTimeStamp());
        field.addLiving(living,event.getPosition());
        if(living.getClass()==Elder.class)
            elder=(Elder)living;
        else if(living.getClass()==SnakeDemon.class)
            snakeDemon=(SnakeDemon)living;
        startLivingThread(living);
    }

    private void scheduleMove(LivingMoveEvent event){
        Item living=event.getSubject();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Schedule move executing: "+event+" current "+currentTimeStamp());
                if(!event.getOldPosition().equals(living.getPosition())){
                    System.out.println("move scheduler: not match position "+living+" "+
                            living.getPosition()+" but should have at "+
                            event.getOldPosition());
                }
                if(!living.move(event.dx(),event.dy()))
                    System.out.println("move failed!");
            }
        },event.getTimeStamp()-currentTimeStamp(),TimeUnit.MILLISECONDS);
    }

    private void scheduleKill(KillEvent event){
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Living killer=event.getSubject();
                Living victim=event.getObject();
//                assert killer.getPosition().adjacentWith(killee.getPosition());
                livingDies(victim);
                showKillEvent(killer,victim);
            }
        },event.getTimeStamp()-currentTimeStamp(),TimeUnit.MILLISECONDS);
    }

    /**
     * 添加线程，开始运行
     */
    public void begin(){
        if(executing)
            return;
        executing=true;
        startTime=System.currentTimeMillis();
//        initFormations();
        //启用输出
        try{
            eventWriter=new EventWriter();
        }catch(IOException e){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.ERROR,
                            "fetal: Cannot open log file.\n"+
                                    e.toString()
                    ).show();
                }
            });
        }
        //启动线程
        startLivingThread(elder);
        for(Living l:elder.getCalabashes())
            startLivingThread(l);
        startLivingThread(snakeDemon);
        ScorpionDemon scorpionDemon=snakeDemon.getScorpionDemon();
        startLivingThread(scorpionDemon);
        for(Living l:scorpionDemon.getFollowDemons())
            startLivingThread(l);
        scheduler.scheduleAtFixedRate(this,40,
                INTERVAL,TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(judgeHandler,50,INTERVAL/2,TimeUnit.MILLISECONDS);
    }

    private void startLivingThread(Living living){
        living.setMovable(false);
        activeLivings.add(living);
        living.start();
        ListView<String> lv;
        if(living.getClass().isAnnotationPresent(WithCalabash.class))
            lv=calabashList;
        else
            lv=demonList;
        Platform.runLater(()->lv.getItems().add(living.livingName()));
        if(eventWriter!=null)
            eventWriter.write(new LivingCreatedEvent(
                    living,living.getPosition(),currentTimeStamp()));
//        livingFutureMap.put(living,
//                scheduler.scheduleAtFixedRate(living,0,INTERVAL,TimeUnit.MILLISECONDS)
//        );
    }

    private int counter=0;
    @Override
    public synchronized void run(){
        ++counter;
        System.out.println("active count "+activeLivings.size());
        System.out.println(""+this+" Game::run begins counter:"+counter +", s:"+((double)System.currentTimeMillis()-startTime)/1000);
        System.out.println("waiting count "+judgeHandler.waitingCount());
        long tm=System.currentTimeMillis();
        checkIsOver();
        field.draw();
        System.out.println(scheduler.toString());
        System.out.println("Game::run for ms: "+(System.currentTimeMillis()-tm));
    }

    /**
     * 检查是否有一方死光了，如果是则结束游戏
     */
    private void checkIsOver(){
        if(isOver()) {
            scheduler.shutdownNow();
            System.out.println("Game Over!");
            field.draw();
            String result;
            if(activeLivings.isEmpty())
                result="平局";
            else if(activeLivings.get(0).getClass().
                    isAnnotationPresent(WithCalabash.class))
                result="葫芦娃胜!";
            else
                result="妖精胜!";
            for(Item living:activeLivings)
                living.interrupt();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.INFORMATION,"Game over!\n"+result).showAndWait();
//                    clearCurrentTurn();
                    clearCurrentTurn();
                    initFormations();
                }
            });
            executing=false;
        }
    }

    private boolean isOver(){
        System.out.println("check is Over: "+activeLivings.toString());
        if(activeLivings.isEmpty())
            return true;
        Item first=activeLivings.get(0);
        for(Item living:activeLivings){
            if(living.isAttackable(first))
                return false;
        }
        return true;
    }

    /**
     * 实质上只是加到队列里
     */
    public void decide(Living living1, Living living2){
//        blockingQueue.add(new Judger(living1,living2));
//        commitDecide();
//        notifyAll();
        judgeHandler.addJudgeTask(living1,living2);
    }

//    private synchronized void commitDecide(){
//        try {
//            while(true) {
//                while (blockingQueue.isEmpty())
//                    wait();
//                Judger judger = blockingQueue.take();
//                judger.judge();
//            }
//        }catch(InterruptedException e){
//            System.out.println("main thread interrupted");
////            System.exit(117);
//        }
//    }

    /**
     * 生物体死亡，移动引用，并停止线程
     */
    public synchronized void livingDies(Living living){
        living.die();
        boolean flag=activeLivings.remove(living);
        assert flag;  //如果assert failed，则表明线程冲突
//        ScheduledFuture<?> future=livingFutureMap.get(living);
//        future.cancel(true);
//        livingFutureMap.remove(living);
        deadLivings.add(living);
        ListView<String> lv;
        if(living.getClass().isAnnotationPresent(WithCalabash.class))
            lv=calabashList;
        else
            lv=demonList;
        Platform.runLater(()->lv.getItems().remove(living.livingName()));
        checkIsOver();
    }

    /**
     * 为living指定攻击目标
     * @param living 寻找攻击目标的生物
     * @return 被锁定为目标的生物。如果不存在了，返回Null.
     */
    public synchronized Item findEnemyFor(Item living){
        Collections.shuffle(activeLivings);
        for(Item another:activeLivings){ //ConcurrentModificationException??
            if(living.isAttackable(another))
                return another;
        }
        return null;
    }


    public void assertActive(Item living){
        if(!activeLivings.contains(living)) {
            System.out.println("assertActive failed! " + living);
            System.exit(189);
        }
    }

    public BattleField getField(){
        return field;
    }

    public EventWriter getEventWriter(){
        return eventWriter;
    }

    public void cleanup(){
        if(eventWriter!=null)
            eventWriter.cleanup();
        System.exit(0);
    }

    /**
     * 当前系统的时间，即用户操作开始后的正计时。
     * @return
     */
    public long currentTimeStamp(){
        return System.currentTimeMillis()-startTime;
    }

    /**
     * 一局结束的时候调用，收拾残局。
     * 保证：重复调用不会出现问题
     */
    private void clearCurrentTurn(){
        activeLivings.clear();
        deadLivings.clear();
        field.clearLivings();
        inVideo=false;
        elder=null;
        snakeDemon=null;
        scheduler.shutdownNow();
        scheduler=Executors.newScheduledThreadPool(20);
        listView.getItems().clear();
        calabashList.getItems().clear();
        demonList.getItems().clear();
    }
}
