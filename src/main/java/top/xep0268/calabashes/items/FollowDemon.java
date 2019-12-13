package top.xep0268.calabashes.items;

/*
 * 小喽啰。比其他的多一个编号。
 */
import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.field.Field;

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
    public boolean isAttackable(Item living) {
        Class<? extends Item> cls=living.getClass();
        return cls==Calabash.class || cls==Elder.class;
    }

    @Override
    public String livingName(){
        return "小妖精("+order+")";
    }
}
