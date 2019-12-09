package calabashes.formations;

import calabashes.field.Position;

public class ArrowFormation extends Formation {
    public ArrowFormation(int n) {
        super(n);
    }

    @Override
    protected void form() {
        int swingCount=N/2,t=0;
        Position center=new Position(0,0);
        if(swingCount%2==1)
            swingCount++;
        int mainCount=N-swingCount;
        Position p=center.copy();
        Position.Direction d=p.new Direction(Position.Direction.S);
        for(int i=0;i<mainCount;i++){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
        p=center.copy();
        d=p.new Direction(Position.Direction.SW);
        for(int i=0;i<swingCount/2;i++){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
        p=center.copy();
        d=p.new Direction(Position.Direction.SE);
        for(int i=0;i<swingCount/2;i++){
            coordinates[t++]=d.adjacentPosition();
            d.aStep();
        }
    }
}
