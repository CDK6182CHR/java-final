/*
 * 2019.10.22重构。
 * 取消对偶数个从者的限制。如果是奇数个从者，则在顶部多加一个从者。
 */
package calabashes.formations;

import calabashes.field.Position;

public class SwingFormation extends calabashes.formations.Formation {
    public SwingFormation(int n) {
        super(n);
    }

    @Override
    protected void form() {
        Position center=new Position(0,0);
        Position.Direction d=center.new Direction(Position.Direction.N);
        int t=0;
        if(N%2==1){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
        int swingCount=N/2;
        Position p=center.copy();
        d=p.new Direction(Position.Direction.NW);
        for(int i=0;i<swingCount;i++){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
        p=center.copy();
        d=p.new Direction(Position.Direction.NE);
        for(int i=0;i<swingCount;i++){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
    }
}
