package top.xep0268.calabashes.exceptions;
import com.sun.istack.internal.Nullable;
import top.xep0268.calabashes.field.Block;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.items.Item;
import top.xep0268.calabashes.field.Position;

public class PathNotFoundException extends Exception {
    private Item obj;
    private Position to;
    private Field<Block<?>> passed;

    public PathNotFoundException(Item item,Position to,Field<Block<?>> passed){
        obj=item;
        this.to=to;
        this.passed=passed;
    }

    @Override
    public String toString(){
        return super.toString()+"No path for item "+obj+" to "+to+".";
    }

    public Field<Block<?>> getPassed(){
        return passed;
    }
}
