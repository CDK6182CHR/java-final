package top.xep0268.calabashes.field;

import top.xep0268.calabashes.items.Item;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

/**
 * 2019.12.06添加
 * 图形化单元格。
 * 将单元格中与<code>Pane</code>相关的部分抽离出来。
 */
public class GraphicBlock extends Block {
    private Pane grid;
    private static final Color
            EVEN_COLOR=Color.color(0.6,0.6,0.6),
            ODD_COLOR =Color.color(0.9,0.9,0.9);

    GraphicBlock(int x, int y) {
        super(x, y);
        grid=new Pane();
        grid.setBackground(new Background(new BackgroundFill(
                (x+y)%2==0?EVEN_COLOR:ODD_COLOR,null,null
        )));
    }

    @Override
    public synchronized boolean setLiving(Item living){
        boolean flag=super.setLiving(living);
        if(flag)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(living!=null){
                        grid.getChildren().add(living.getImg());
                    }
                }
            });
        return flag;
    }

    @Override
    public synchronized void removeLiving(){
        if(living!=null)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    grid.getChildren().clear();
                }
            });
        super.removeLiving();
    }

    /**
     * 生物死亡后，更新图片。
     */
    public synchronized void updateLivingImage(){

    }

    public Pane getPane(){
        return grid;
    }
}
