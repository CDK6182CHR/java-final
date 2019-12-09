package calabashes.items;
/*
 * 老人家类。负责“指挥”葫芦娃，维护葫芦娃列表。
 */

import calabashes.Game;
import calabashes.exceptions.NoSpaceForFormationException;
import calabashes.field.*;
import calabashes.formations.Formation;
import calabashes.formations.FormationHandler;

public class Elder extends Living implements Leader,WithCalabash{
    private Calabash[] calabashes;

    /*
     * 老人家“种”出了葫芦娃
     * 随机产生位置并添加葫芦娃。
     * 2019.12.08改为：自己把自己加进图里，防止领导人自己进不去的情况。
     */
    public Elder(Position pos, Field field_, Game game1) {
        super(pos, field_, game1);
        calabashes=new Calabash[7];
        String[] colors={
                "红","橙","黄","绿","青","蓝","紫"
        };
        field.addLiving(this);
        calabashes[0]=new Calabash(new Position(0,1),field,game,1,
                colors[0]);
        field.addLiving(calabashes[0]);
        for(int i=1;i<7;i++){
//            calabashes.fieldashes.Position position;
//            if(i!=0)
//                position=field_.randomPosition();
//            else
//                position=new calabashes.fieldashes.Position(5,1);
            Position position=field_.leftRandomPosition();
            Calabash cal=new Calabash(position,field_,game,i+1,colors[i]);
            calabashes[i]=cal;
            field_.addLiving(cal);
        }
    }

    @Override
    protected String getResourceName() {
        return active?"/lrj.png":"/lrjd.png";
    }

    /*
     * 按“长蛇阵”排列。指定老大在一个位置（保证他下面足以排够其他的人），
     * 然后依次指挥其他葫芦娃到下面的位置来。
     */
    public void standAsSnake(){
//        Calabash[] followers = new Calabash[6];
//        for(int i=1;i<7;i++)
//            followers[i-1]=calabashes[i];
//        FormationOld form= new SnakeFormationOld(calabashes.field,calabashes[0],
//                followers);
//        try {
//            standAsFormation(form);
//        }catch(NoSpaceForFormationException e){
//            System.out.println(e.toString());
//        }
//        for (Calabash c:calabashes){
//            c.setMovable(true);
//        }
//        while (Field.N-calabashes[0].getPosition().getY()<7) {
//            calabashes[0].moveOrSwap(0,-1);
//        }
//        calabashes[0].setMovable(false);
//        Position pos=calabashes[0].getPosition().copy();
//        for(int i=1;i<7;i++){
//            pos.setPos(pos.getX(),pos.getY()+1);
//            try {
//                calabashes[i].walkTowards(pos);
//            }
//            catch(PathNotFoundException e){
//                System.out.println("Cannot format as snake due to "+e);
//            }
//            calabashes[i].setMovable(false);
//        }
    }

    @Override
    public String toString(){
        return active?"LRJ":"LRx";
    }

    @Override
    public <T extends Formation> void embattleFormation(Class<T> formType) {
        Calabash[] followers=new Calabash[6];
        for(int i=0;i<6;i++){
            followers[i]=calabashes[i+1];
        }
        FormationHandler<T> fh=new FormationHandler<T>(
                field,calabashes[0],followers,formType
        );
        try{
            fh.embattle();
        }
        catch (NoSpaceForFormationException e){
            System.out.println(e.toString());
        }
    }

    @Override
    public boolean isAttackable(Living living) {
        Class<? extends Living> cls=living.getClass();
        return cls==ScorpionDemon.class||cls==SnakeDemon.class||cls==FollowDemon.class;
    }

    public Calabash[] getCalabashes(){
        return calabashes;
    }

    @Override
    public String livingName(){
        return "老人家";
    }
}
