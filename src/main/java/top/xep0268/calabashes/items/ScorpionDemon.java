package top.xep0268.calabashes.items;
/*
 * 蝎子精，领队。负责指挥喽啰排队。
 */

import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.exceptions.NoSpaceForFormationException;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.formations.Formation;
import top.xep0268.calabashes.formations.FormationHandler;

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
    public boolean isAttackable(Item living) {
        Class<? extends Item> cls=living.getClass();
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
