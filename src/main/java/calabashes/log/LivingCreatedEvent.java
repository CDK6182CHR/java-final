package calabashes.log;

import calabashes.field.Position;
import calabashes.items.Living;

/**
 * 生物创建事件。
 * 暂定只记录布阵结束后生物的位置
 * （也即回放时候忽略布阵过程。当然要重现布阵过程也很简单。）
 */
public class LivingCreatedEvent extends AbstractEvent {
    public LivingCreatedEvent(Living subject, Position position, long timeStamp) {
        super(subject, position, timeStamp);
    }

    @Override
    protected int getTypeCode() {
        return 0;
    }

    @Override
    public String toString(){
        return "LivingCreatedEvent: "+subject+"@"+position+" on "+getTimeStamp();
    }
}
