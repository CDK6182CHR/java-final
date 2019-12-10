/*
 * 所有生物体的公共基类。
 */
package top.xep0268.calabashes.items;

import top.xep0268.calabashes.exceptions.PathNotFoundException;
import top.xep0268.calabashes.field.Block;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.log.LivingMoveEvent;
import javafx.application.Platform;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Math.abs;

public abstract class Living extends Thread implements Serializable {
    protected transient Field field;
//    private Field passedMap=new Field();
    protected final Position position;//此对象不可变
    protected boolean movable,active=true;
    protected Living enemy;  //锁定的攻击目标
    protected transient Game game;
    private transient Pane img;//采用懒构造模式
    private transient ImageView view;
    public transient int timeStamp=0;
    private boolean inVideo=false;//是否在回放模式
//    private transient ScheduledExecutorService scheduler;
    private static Random random=new Random();

    public Living(Position pos,Field field_,Game game_){
        position=pos.copy();
        field=field_;
        movable=true;
        game=game_;
    }

    public Pane getImg(){
        if(img==null){
            synchronized (this) {
                System.out.println("makeImg " + this);
                img = new AnchorPane();
                view = new ImageView(
                        String.valueOf(getClass().getResource(getResourceName())));
                view.setFitHeight(60);
                view.setFitWidth(60);
                img.getChildren().add(view);
//            img.setBackground(new Background(new BackgroundImage(new Image(
//                    String.valueOf(getClass().getResource(getResourceName()))
//            ),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,BackgroundSize.DEFAULT
//            )));
            }
        }
        return img;
    }

    protected abstract String getResourceName();

    public Position getPosition(){
        return position;
    }

    /*
     * 排序和排阵列时，已经定位到指定位置的对象不可再移动。
     */
    public void setMovable(boolean m){
        movable=m;
    }

    public boolean isMovable(){
        return movable;
    }

    /*
     * 只允许在8邻域内移动。即abs(dx),abs(dy)<=1.
     * 返回移动是否成功。如果目标位置被占用，则移动失败。
     */
    public synchronized boolean move(int dx,int dy){
        System.out.println("enter Living::move "+this);
        assert abs(dx)<=1 && abs(dy)<=1;
        Position oldPosition=position.copy();
        boolean flag = field.moveLiving(this,dx,dy);
        System.out.println("exit Living::move "+this);
        if(timeStamp>0&&flag){
            game.getEventWriter().write(new LivingMoveEvent(
                    this,position,game.currentTimeStamp(),oldPosition
            ));
        }
        return flag;
    }

    /**
     * 与another交换位置。要求二者必须是相邻的。
     * 相邻的检查交给Field完成。
     * 如果不相邻，返回false。
     */
    public boolean swapWith(Living another){
        assert another!=null;
        assert field.livingAt(position) == this;
        assert field.livingAt(another.position) == another;
        return field.swapLiving(this,another);
    }

    /**
     * 移动到pos所示位置，如果指定位置不相邻，或者该位置的对象不可移动，则返回false。
     */
    public boolean moveOrSwap(int dx,int dy){
        assert abs(dx)<=1 && abs(dy)<=1;
        Position target=new Position(position.getX()+dx,position.getY()+dy);
        if(field.livingAt(target)==null)
            return move(dx,dy);
        else if(field.livingAt(target).isMovable())
            return swapWith(field.livingAt(target));
        return false;
    }

    /**
     * 从当前位置取捷径走到position确定的位置。
     * 若有阻挡，对于movable的，直接和他交换；否则找一个方向绕开。
     * 如果找不到路径，返回false.
     */
    public boolean walkTowards(Position target) throws PathNotFoundException {
        // 新建标记面板，用于记录走过的地方。
        // 在走过的地方添加一个虚拟的Living对象。
        Field passed=new Field<Block>(Block.class);
        List<Living> called=new LinkedList<>();
        called.add(this);
//        boolean flag = pathTo(passed,target,called);
//        System.out.println("寻路历程"+toString()+"，结果"+flag);
//        passed.draw();
//        return flag;
        return pathTo(passed,target,called);
    }

    /**
     * 递归算法实现找路径过程。
     */
    private boolean pathTo(Field passed, Position target, List<Living> called)
            throws PathNotFoundException{
        if(position.equals(target))//基本情况，已经移动到位
            return true;
        Position.Direction direction=position.new Direction(target);
        Position toMove;
        //首先将当前位置标记为走过
        Living flag=new top.xep0268.calabashes.items.PassedFlag(position,passed,game);
        passed.addLiving(flag);
        for(int i=0;i<8;i++){
            toMove=direction.adjacentPosition();
            if(!field.unreachable(toMove) && passed.livingAt(toMove)==null ){
                moveOrSwap(direction.dx(),direction.dy());
                if(pathTo(passed,target,called))
                    return true;
                else
                    moveOrSwap(-direction.dx(),-direction.dy());
            }
            else if (field.inside(toMove)){
                //不能直接过去，可以考虑交换
                Living an=field.livingAt(toMove);
                if(an!=null)
                    if(an.exchangeable() && an.getClass()==getClass()
                            && called.indexOf(an)<0){
                        called.add(an);
                        if(an.pathTo(passed,target,called)){
                            moveOrSwap(direction.dx(),direction.dy());
                            return true;
                        }
                    }
            }
            direction.next();
        }
        throw new PathNotFoundException(this,target);
    }

    /**
     * 向目标走一步，在一个时钟周期内完成的动作。
     * 2019年12月10日修改并重新启用：引入随机性。选中目标所示的方向有更大的概率，但并非确定的。
     * @param target 目标生物所在位置。
     */
    public void aStepTowards(Position target){
        Position p=position.copy();
        Position.Direction dir=p.new Direction(target);
        dir=p.new Direction(chooseDirection(dir.code()));
        for(int i=0;i<8;i++){
            if(move(dir.dx(),dir.dy())){
                return;
            }
            else
                dir.next();
        }
        System.out.println("No available step targeting at "+target+" for "+this);
    }

    /**
     * 根据几何分布确定方向。
     * @param d0 参考方向代码，0~7
     * @return 目标方向代码，0~7
     */
    private int chooseDirection(int d0){
        //在0-4之间确定一个偏移量
        int c=random.nextInt(100);
        int d;
        if(c<30)//0.30
            d=0;
        else if(c<60)//0.15
            d=1;
        else if(c<80)//0.10
            d=2;
        else if(c<95)//0.075
            d=3;
        else//0.05
            d=4;
        if(d>=1&&d<=3){
            if(random.nextBoolean())
                return (d0+d)%8;
            else
                return (d0-d)%8;
        }
        return (d0+d)%8;
    }

    public void aRandomStep(){
        Position.Direction dir=position.new Direction(position); //构造随机方向
        for(int i=0;i<8;i++){
            if(/*passedMap.livingAt(dir.adjacentPosition())==null&&*/move(dir.dx(),dir.dy())){
//                passedMap.addLiving(new PassedFlag(dir.adjacentPosition(),passedMap,game));
                return;
            }
            else
                dir.next();
        }
        System.out.println("No available step for "+this);
    }

    public boolean exchangeable(){
        return false;
    }

    public boolean isActive(){
        return active;
    }

    /**
     * 寻找攻击对象。
     */
    protected void findEnemy(){
//        passedMap.clearLivings();
        enemy=game.findEnemyFor(this);
    }

    /**
     * 检查周围是否存在敌人。
     * 在微分的状态，可以认为整个棋盘都是静止的，所以不考虑动态情况
     *
     * 2019.11.26工作到此
     * 注记：synchronized似乎应该是检查两个生物关系的时候，而不是decide生死的时候？
     * 让所有线程排队并不是有意义的方法。
     * 或许不应该是中心化的判断？
     * 【目标】应该是让两个正在判断的生物进入线程锁定状态。如何实现？
     */
    private void checkEnemyToAttack(){
        Position p=position.copy();
        Position.Direction dir=p.new Direction(p);  //随机方向
//        PositionLoop:
        for (int i = 0; i < 8; i++, dir.next()) {
            System.out.println("checkEnemyToAttack on "+this+": "+i+", "+dir);
            Position pos=dir.adjacentPosition();
            System.out.println("adjacent position is "+pos);
            Living living = field.livingAt(pos);//确定这个导致线程死掉
            System.out.println("checkEnemyToAttack on "+this+": "+i+", "+living);
            if (living != null && living.isActive()&&isAttackable(living)) {
                System.out.println("checkEnemyToAttack: loop on "+this+", "+living);
                //                        while (lock.isLocked()) {
//                            System.out.println("before-wait in Living"+this);
//                            condition.await(20,TimeUnit.MILLISECONDS);
//                            System.out.println("after-wait in Living "+this);
//                            if(lock.isLocked()){
//                                System.out.println("release this forcely "+this);
//                                continue PositionLoop;
//                            }
//                        }
//                        System.out.println("wait in Living over "+this);
//                        lock.lock();
                game.decide(this, living);
//                    }catch (InterruptedException e){ //代表死亡
//                        System.out.println("interrupted in Living "+this);
//                        return;
                if (!active)
                    return;
            }
        }
//            dir.aStep();//这是个死循环！！艹
    }

    /**
     * 判断指定的生物是否是活的敌人
     */
    public abstract boolean isAttackable(Living living);

    /**
     * Runnable接口方法。
     * 实质上是更新状态，每个周期调用一次。
     *
     * 2019.11.30注记：目前看来至少有两种死掉的原因
     * 成功运行到before check的，和在此前就死掉的。
     */
    @Override
    public void run(){
        if(inVideo){
            playVideo();
        }
        else{
            live();
        }
    }

    private synchronized void live(){
        try{
            TimeUnit.MILLISECONDS.sleep(100);
            while(!Thread.interrupted()){
                ++timeStamp;
                update();
                TimeUnit.MILLISECONDS.sleep(Game.INTERVAL);
            }
        }catch (InterruptedException e){
            System.out.println("Interrupted Living "+this);
        }
    }

    private synchronized void update(){
        if(!active){
            return;
        }
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               System.out.println(Living.this);
                               System.out.println("timeout!"+Living.this);
                           }
                       },80
        );
        System.out.println("+{"+this+"}");
        long startT = System.currentTimeMillis();
        if(enemy==null)
            findEnemy();
        else if(!enemy.isActive())
            findEnemy();
        System.out.println("Living::run "+this+", enemy is "+enemy);
        if(enemy!=null)
            aStepTowards(enemy.getPosition());
//            aRandomStep();
        else
            aRandomStep();
        System.out.println("before check "+this);
        checkEnemyToAttack();
        System.out.println("-{"+this+"} ran for "+(System.currentTimeMillis()-startT)+" enemy is "+enemy);
        timer.cancel();
    }

    /**
     * 回放模式
     */
    private synchronized void playVideo(){

    }

    public void die(){
        active=false;
        interrupt();
        System.out.println(this.toString()+" died");
        Platform.runLater(() -> {
            view.setImage(new Image(String.valueOf(getClass().getResource(getResourceName()))));
        });
    }

    public int getTimeStamp(){
        return timeStamp;
    }

    /**
     * 设置为回放模式，且不可恢复
     */
    public void setInVideo(Field field,Game game){
        inVideo=true;
        this.field=field;
        this.game=game;
//        scheduler=Executors.newScheduledThreadPool(200);
    }

    public abstract String livingName();

//    /**
//     * 计划移动事件。
//     * @param newPosition 要移动到的位置
//     * @param oldPosition 移动前所在位置
//     * @param time 移动发生的时间戳，整型数
//     */
//    public void scheduleMoveEvent(final Position newPosition,
//                                  final Position oldPosition,
//                                  final int time
//                                  ){
//        scheduler.schedule(new Runnable() {
//            @Override
//            public void run() {
//                assert oldPosition.equals(position);
//                boolean flag=move(newPosition.getX()-oldPosition.getX(),
//                        newPosition.getY()-oldPosition.getY());
//                assert flag;
//            }
//        },time*Game.INTERVAL,TimeUnit.MILLISECONDS);
//    }
}
