package top.xep0268.calabashes.items;/*
 * 蛇精。总指挥。
 */
import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.exceptions.NoSpaceForFormationException;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.formations.*;

@WithDemon
public class SnakeDemon extends Living implements Leader {
    private ScorpionDemon scorpionDemon;

    public SnakeDemon(Position pos, Field field_, Game game_, int followCount_) {
        super(pos, field_,game_);
        field_.addLiving(this);
        scorpionDemon=new ScorpionDemon(new Position(Field.N-5,Field.M-4),
                field_,game,followCount_);
        scorpionDemon.setMovable(false);
        //        scorpionDemon=new top.xep0268.calabashes.ScorpionDemonorpionDemon(new top.xep0268.calabashes.Positionld.Position(9,0),field_,followCount_);
    }

    @Override
    public String toString(){
        return active?"Sna":"SnX";
    }

    @Override
    public <T extends Formation> void embattleFormation(Class<T> formType) {
        try{
            scorpionDemon.embattleFormation(formType);
        }
        catch (NoSpaceForFormationException e){
            System.out.println(e.toString());
        }
    }

    @Override
    protected String getResourceName() {
        return active?"/snake.png":"/snaked.png";
    }

    @Override
    public boolean isAttackable(Item living) {
        Class<? extends Item> cls=living.getClass();
        return cls==Calabash.class || cls==Elder.class;
    }

    public ScorpionDemon getScorpionDemon(){
        return scorpionDemon;
    }

    @Override
    public String livingName(){
        return "蛇精";
    }
}
