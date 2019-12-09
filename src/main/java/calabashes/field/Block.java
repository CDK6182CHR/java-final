package calabashes.field;

import calabashes.items.Living;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import org.omg.CORBA.TIMEOUT;

import java.util.concurrent.TimeUnit;

/**
 * Field上的一个地块，可以承载至多一个Living。
 */
public class Block {
    protected Living living;
    private final int x,y;  //blank final

    //package-access
    Block(int x,int y){
        this.x=x;
        this.y=y;
    }

    public boolean hasLiving(){
        return living!=null;
    }

    public synchronized boolean setLiving(Living l){
//        System.out.println("Enter Block::setLiving ("+x+", "+y+")");
        if(living!=null) {
            System.out.println("Exit Block::setLiving false ("+x+", "+y+")");
            return false;
        }
        living=l;
        extendInterval();
        return true;
    }

    private synchronized void extendInterval(){
//        try{
//            TimeUnit.MILLISECONDS.sleep(3);
//        }catch (InterruptedException e){
//            //
//        }
    }

    public synchronized Living getLiving(){
        return living;
    }

    public synchronized void removeLiving(){
//        System.out.println("Enter Block::removeLiving ("+x+", "+y+")");
        living=null;
        extendInterval();
//        System.out.println("Exit Block::removeLiving ("+x+", "+y+")");
    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean isReachable(){
        return living==null || living.isMovable();
    }

    public boolean isVacant(){
        return living==null;
    }

}
