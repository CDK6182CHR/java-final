package top.xep0268.calabashes.exceptions;
import top.xep0268.calabashes.items.Item;
import top.xep0268.calabashes.field.Position;

public class PathNotFoundException extends Exception {
    private Item obj;
    private Position to;
    public PathNotFoundException(Item item, Position to){
        obj=item;
        this.to=to;
    }

    @Override
    public String toString(){
        return super.toString()+"No path for item "+obj+" to "+to+".";
    }
}
