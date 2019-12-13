package top.xep0268.calabashes;

import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.items.Item;


/**
 * 测试用的，逻辑上的生物。
 */
public class LogicalItem extends Item {
    private String mark;
    public LogicalItem(Position pos, Field field_, String mark) {
        super(pos, field_);
        this.mark=mark;
    }

    @Override
    public String toString(){
        return "["+mark+"]";
    }

    @Override
    protected String getResourceName() {
        return null;
    }

    @Override
    protected void findEnemy() {

    }

    @Override
    protected void checkEnemyToAttack() {

    }

    @Override
    public boolean isAttackable(Item living) {
        return false;
    }

    @Override
    public String livingName() {
        return toString();
    }
}
