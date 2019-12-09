package calabashes.items;/*
 * 蛇精。总指挥。
 */
import calabashes.Game;
import calabashes.exceptions.NoSpaceForFormationException;
import calabashes.field.Field;
import calabashes.field.Position;
import calabashes.formations.*;

public class SnakeDemon extends Living implements Leader, WithDemon{
    private ScorpionDemon scorpionDemon;

    public SnakeDemon(Position pos, Field field_, Game game_, int followCount_) {
        super(pos, field_,game_);
        field_.addLiving(this);
        scorpionDemon=new ScorpionDemon(new Position(Field.N-3,0),
                field_,game,followCount_);
        scorpionDemon.setMovable(false);
        //        scorpionDemon=new calabashes.ScorpionDemonorpionDemon(new calabashes.Positionld.Position(9,0),field_,followCount_);
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
    public boolean isAttackable(Living living) {
        Class<? extends Living> cls=living.getClass();
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
