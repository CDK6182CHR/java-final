package top.xep0268.calabashes.items;

/*
 * 葫芦娃类。
 * 在Living的基础上新增排行。
 */
import top.xep0268.calabashes.field.*;
import top.xep0268.calabashes.Game;

@WithCalabash
public class Calabash extends Living{
    private int order;
    private String color;
    public Calabash(Position pos, Field field_, Game game, int order_, String color_) {
        super(pos, field_,game);
        order=order_;
        color=color_;
    }

    @Override
    public String toString(){
        if(active)
            return "["+order+"]";
        else
            return "["+order+"x";
    }

    @Override
    protected String getResourceName() {
        return active?"/"+order+".png":"/"+order+"d.png";
    }

    @Override
    public boolean isAttackable(Item living) {
        Class<? extends Item> cls=living.getClass();
        return cls==ScorpionDemon.class||cls==SnakeDemon.class||cls==FollowDemon.class;
    }

    @Override
    public String livingName(){
        return "葫芦娃["+color+"]";
    }
}
