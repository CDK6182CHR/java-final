package top.xep0268.calabashes.field;

import top.xep0268.calabashes.items.Item;

/**
 * Field上的一个地块，可以承载至多一个Living。
 */
public class Block<T extends Item> {
    protected T living;
    private final int x,y;  //blank final

    //package-access
    Block(int x,int y){
        this.x=x;
        this.y=y;
    }

    public boolean hasLiving(){
        return living!=null;
    }

    public synchronized boolean setLiving(T l){
//        System.out.println("Enter Block::setLiving ("+x+", "+y+")");
        if(living!=null) {
            System.out.println("Exit Block::setLiving false ("+x+", "+y+")");
            return false;
        }
        living=l;
        return true;
    }

    public T getLiving(){
        return living;
    }

    public synchronized void removeLiving(){
//        System.out.println("Enter Block::removeLiving ("+x+", "+y+")");
        living=null;
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
