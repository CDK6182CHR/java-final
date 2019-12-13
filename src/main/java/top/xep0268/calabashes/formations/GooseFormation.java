package top.xep0268.calabashes.formations;

import top.xep0268.calabashes.field.Position;

public class GooseFormation extends Formation {
    public GooseFormation(int n) {
        super(n);
    }

    @Override
    protected void form() {
        Position center=new Position(0,0);
        Position p=center.copy();
        Position.Direction dir=p.new Direction(Position.Direction.SW);
        for(int i=0;i<N;i++){
            coordinates[i]=dir.adjacentPosition();
            dir.aStep();
        }
    }
}
