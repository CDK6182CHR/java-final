package top.xep0268.calabashes.formations;

import top.xep0268.calabashes.field.Position;

public class SnakeFormation extends Formation {
    public SnakeFormation(int n) {
        super(n);
    }

    @Override
    protected void form() {
        Position center=new Position(0,0);
        Position p=center.copy();
        Position.Direction d=p.new Direction(Position.Direction.S);
        for(int i=0;i<N;i++){
            coordinates[i]=d.adjacentPosition();
            d.aStep();
        }
    }
}
