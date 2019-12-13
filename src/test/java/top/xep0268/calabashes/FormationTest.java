package top.xep0268.calabashes;

import org.junit.Test;
import top.xep0268.calabashes.exceptions.NoSpaceForFormationException;
import top.xep0268.calabashes.field.Block;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.formations.ArrowFormation;
import top.xep0268.calabashes.formations.Formation;
import top.xep0268.calabashes.formations.FormationHandler;
import top.xep0268.calabashes.formations.SnakeFormation;
import top.xep0268.calabashes.items.Item;
import top.xep0268.calabashes.items.Leader;

/**
 * 测试阵型排布
 */
@SuppressWarnings("unchecked")
public class FormationTest {
    private static class LogicalLeader extends LogicalItem implements Leader {
        private static final int N=6;
        private LogicalItem[] items=new LogicalItem[N];
        public LogicalLeader(Position pos, Field field_, String mark) {
            super(pos, field_, mark);
            for(int i=0;i<N;i++){
                items[i]=new LogicalItem(field.randomPosition(),field_,""+i);
                field_.addLiving(items[i]);
            }
            field_.addLiving(this);
        }

        @Override
        public <T extends Formation> void embattleFormation(Class<T> formType)
                throws NoSpaceForFormationException {
            FormationHandler<T> handler=new FormationHandler<>(
                    field,this,items,formType);
            handler.embattle();
        }
    }

    private Field<Block<Item>> field=new Field(Block.class);
    private LogicalLeader leader=new LogicalLeader(new Position(1,1),field,"L");

    @Test
    public void testSnake() throws NoSpaceForFormationException{
        leader.embattleFormation(SnakeFormation.class);
        field.draw();
    }

    @Test
    public void testArrow()throws Exception{
        leader.walkTowards(new Position(4,5));
        leader.embattleFormation(ArrowFormation.class);
        field.draw();
    }
}
