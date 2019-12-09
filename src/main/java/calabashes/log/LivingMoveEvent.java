package calabashes.log;

import calabashes.field.Position;
import calabashes.items.Living;

public class LivingMoveEvent extends AbstractEvent {
    private Position oldPosition;
    public LivingMoveEvent(Living subject, Position position,long timeStamp,
                    Position oldPosition) {
        super(subject, position, timeStamp);
        this.oldPosition=oldPosition;
    }

    public int dx(){
        return position.getX()-oldPosition.getX();
    }

    public int dy(){
        return position.getY()-oldPosition.getY();
    }

    public Position getOldPosition(){
        return oldPosition;
    }

    @Override
    public String toString(){
        return "LivingMoveEvent: "+subject+" "+oldPosition+"->"+position+
                " on "+getTimeStamp();
    }

    @Override
    protected int getTypeCode() {
        return 1;
    }
}
