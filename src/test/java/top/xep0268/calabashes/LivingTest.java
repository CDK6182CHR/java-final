package top.xep0268.calabashes;

import org.junit.*;
import static org.junit.Assert.*;

import top.xep0268.calabashes.exceptions.PathNotFoundException;
import top.xep0268.calabashes.items.*;
import top.xep0268.calabashes.field.*;

@SuppressWarnings("unchecked")
public class LivingTest{
    private LogicalItem item1,item2;
    private Field<Block<LogicalItem>> field
            =new Field(Block.class);

    @Before
    public void initFormation(){
        item1=new LogicalItem(new Position(1,1),field,"1");
        item2=new LogicalItem(new Position(1,2),field,"2");
        field.addLiving(item1);
        field.addLiving(item2);
    }

    /**
     * 测试能够移动到空位以及不能移动到已有生物的位置
     */
    @Test
    public void testMove(){
        field.draw();
        assertTrue(item1.move(1,0));
        assertEquals(item1.getPosition(), new Position(2, 1));
        assertFalse(item2.move(1,-1));
        field.draw();
    }

    /**
     * 测试寻路算法
     * @throws PathNotFoundException this would not be thrown
     */
    @Test
    public void testWalk()throws PathNotFoundException {
        Position p=new Position(5,6);
        item1.walkTowards(p);
        assertEquals(item1.getPosition(), p);
        Position p1=new Position(6,3);
        item2.walkTowards(p1);
        assertEquals(item2.getPosition(),p1);
    }
}
