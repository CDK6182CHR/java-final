package calabashes.items;
/*
 * 蝎子精，领队。负责指挥喽啰排队。
 */

import calabashes.Game;
import calabashes.exceptions.NoSpaceForFormationException;
import calabashes.field.Field;
import calabashes.field.Position;
import calabashes.formations.Formation;
import calabashes.formations.FormationHandler;

public class ScorpionDemon extends Living implements Leader,WithDemon {
    private int followCount;
    private FollowDemon[] followDemons;

    public ScorpionDemon(Position pos, Field field_, Game game, int followCount_) {
        super(pos, field_, game);
        field_.addLiving(this);
        followCount = followCount_;
        followDemons=new FollowDemon[followCount];
        for(int i=0;i<followCount_;i++){
            Position position=field_.randomPosition();
            FollowDemon followDemon=new FollowDemon(position,field_,game,i+1);
            field.addLiving(followDemon);
            followDemons[i]=followDemon;
        }
    }

    @Override
    public String toString(){
        return active?"Sco":"ScX";
    }


    @Override
    public <T extends Formation> void embattleFormation(Class<T> formType)
            throws NoSpaceForFormationException {
        FormationHandler<T> fh=new FormationHandler<T>(
                field,this,followDemons,formType);
        fh.embattle();
    }

    @Override
    protected String getResourceName() {
        return active?"/scorpion.png":"/scorpiond.png";
    }

    @Override
    public boolean isAttackable(Living living) {
        Class<? extends Living> cls=living.getClass();
        return cls==Calabash.class || cls==Elder.class;
    }

    public FollowDemon[] getFollowDemons(){
        return followDemons;
    }

    @Override
    public String livingName(){
        return "蝎子精";
    }
}
