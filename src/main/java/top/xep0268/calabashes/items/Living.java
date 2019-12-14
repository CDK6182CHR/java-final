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
     */
    @Override
    protected void checkEnemyToAttack(){
        Position p=position.copy();
        Position.Direction dir=p.new Direction(p);  //随机方向
        for (int i = 0; i < 8; i++, dir.next()) {
            Position pos=dir.adjacentPosition();
            System.out.println("adjacent position is "+pos);
            Living living = (Living)field.livingAt(pos);
            System.out.println("checkEnemyToAttack on "+this+": "+i+", "+living);
            if (living != null && living.isActive()&&isAttackable(living)) {
                game.decide(this, living);
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
    }
}
