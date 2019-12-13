package top.xep0268.calabashes.log;

import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.items.Item;
import top.xep0268.calabashes.items.Living;

/**
 * 生物死亡事件。
 * 注意：主体是杀人的人，客体<code>object</code>是被杀的人。
 */
public class KillEvent extends AbstractEvent {
    private Living object;
    public KillEvent(Living subject, Position position, long timeStamp,
                     Living object) {
        super(subject, position, timeStamp);
        this.object=object;
    }

    public Living getObject(){
        return object;
    }

    @Override
    public String toString(){
        return "KillEvent "+subject+" kills "+object+" @ "+position+
                " on "+getTimeStamp();
    }

    @Override
    protected int getTypeCode() {
        return 1;
    }
}
