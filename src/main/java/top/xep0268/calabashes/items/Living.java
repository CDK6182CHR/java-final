package top.xep0268.calabashes.items;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.field.BattleField;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.GraphicBlock;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.log.LivingMoveEvent;

public abstract class Living extends Item {

    private transient Pane img;//采用懒构造模式
    private transient ImageView view;
    protected transient Game game;

    public Living(Position pos, Field field_, Game game_) {
        super(pos, field_);
        this.game=game_;
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

    @Override
    public synchronized boolean move(int dx,int dy){
        Position oldPosition=position.copy();
        boolean flag=super.move(dx,dy);
        if(timeStamp>0&&flag){
            game.getEventWriter().write(new LivingMoveEvent(
                    this,position,game.currentTimeStamp(),oldPosition
            ));
        }
        return flag;
    }

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
    @Override
    protected void checkEnemyToAttack(){
        Position p=position.copy();
        Position.Direction dir=p.new Direction(p);  //随机方向
//        PositionLoop:
        for (int i = 0; i < 8; i++, dir.next()) {
//            System.out.println("checkEnemyToAttack on "+this+": "+i+", "+dir);
            Position pos=dir.adjacentPosition();
            System.out.println("adjacent position is "+pos);
            Living living = (Living)field.livingAt(pos);//确定这个导致线程死掉
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
    }

    @Override
    public void die(){
        super.die();
        Platform.runLater(() -> {
            view.setImage(new Image(String.valueOf(getClass().getResource(getResourceName()))));
        });
    }

    public void setInVideo(Field field,Game game){
        super.setInVideo(field);
        this.game=game;
//        scheduler=Executors.newScheduledThreadPool(200);
    }
}
