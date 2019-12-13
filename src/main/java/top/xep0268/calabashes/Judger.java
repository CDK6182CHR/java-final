package top.xep0268.calabashes;

import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.items.Item;
import top.xep0268.calabashes.items.Living;
import top.xep0268.calabashes.log.KillEvent;

import static java.lang.Math.*;

public class Judger{
    private Living living1,living2;
    private Game game=Game.getInstance();
    public Judger(Living l1, Living l2){
        living1=l1;
        living2=l2;
    }

    public void judge(){
        decide(living1,living2);
    }

    /**
     * 遇事不决，量子力学
     * 决定两生物冲突中，生物死亡相对概率的波函数
     * @param position 生物所处的位置
     * @return 生物死亡概率
     */
    private double schrodinger(Position position){
        int xr=position.getX(),yr=position.getY();
        double r = abs(sin(yr*yr)*sin(yr*yr)*(sin(xr)*sin(xr)+sin(2*xr)*sin(2*xr)+
                2*sin(xr)*sin(yr)*cos((double)game.currentTimeStamp()/(Field.N*Field.N))));
        System.out.println("Schrodinger("+position+") = "+r);
        return r;
    }

    /**
     * 两生物发生冲突，决定谁死谁活
     * @param living1 主调者
     * @param living2 被调者
     */
    private void decide(Living living1, Living living2){
        if(!living1.isActive()||!living2.isActive())
            return;
        System.out.println("decide called by " + living1 + " with " + living2);
        if (schrodinger(living1.getPosition())<schrodinger(living2.getPosition())) {
            commitKill(living2,living1);
        } else {
            commitKill(living1,living2);
        }
    }

    private void commitKill(Living killer, Living victim){
        game.showKillEvent(killer, victim);
        game.getEventWriter().write(new KillEvent(
                killer,killer.getPosition(),game.currentTimeStamp(),victim
        ));
        game.livingDies(victim);
    }

    public String toString(){
        return "Judger<"+living1+","+living2+">";
    }
}
