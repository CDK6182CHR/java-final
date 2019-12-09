package calabashes.items;

/*
 * 小喽啰。比其他的多一个编号。
 */
import calabashes.Game;
import calabashes.items.Living;
import calabashes.field.Position;
import calabashes.field.Field;
import calabashes.field.*;
public class FollowDemon extends Living implements WithDemon{
    private int order;

    public FollowDemon(Position pos, Field field_, Game game,int order) {
        super(pos, field_,game);
        this.order=order;
    }

    public String toString(){
        if(active)
            return "("+order+")";
        else
            return "("+order+"x";
    }

    @Override
    protected String getResourceName() {
        return active?"/follower.png":"/followerd.png";
    }

    @Override
    public boolean exchangeable(){
        return true;
    }

    @Override
    public boolean isAttackable(Living living) {
        Class<? extends Living> cls=living.getClass();
        return cls==Calabash.class || cls==Elder.class;
    }

    @Override
    public String livingName(){
        return "小妖精("+order+")";
    }
}
