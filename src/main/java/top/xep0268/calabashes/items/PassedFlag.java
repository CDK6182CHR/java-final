package top.xep0268.calabashes.items;
import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.*;
public class PassedFlag extends Living{
    private static int number;
    static {
        number=0;
    }
    private int order;
    public PassedFlag(Position pos, Field field_, Game game_) {
        super(pos, field_, game_);
        order=number++;
    }

    @Override
    protected String getResourceName() {
        throw new UnsupportedOperationException(
                "PassedFlag does not support graphics operations."
        );
//        return "/1.jpg";
    }

    @Override
    public boolean isAttackable(Living living) {
        return false;
    }

    @Override
    public String toString(){
        return "F"+order;
    }

    @Override
    public String livingName(){
        return "Flag";
    }

}
