package top.xep0268.calabashes;

import org.junit.*;
import static org.junit.Assert.*;
import top.xep0268.calabashes.items.*;
import top.xep0268.calabashes.field.*;

public class LivingTest{
    private Game game;
    private BattleField field;
    private Item c1,c2;
    @Before
    public void initFormation(){
        game=Game.getInstance();
        field=BattleField.getInstance();
        c1=new Calabash(
                new Position(1,1),field,game,1,"红"
        );
        field.addLiving(c1);
        c2=new Calabash(
                new Position(1,2),field,game,2,"橙"
        );
        field.addLiving(c2);
    }

    /**
     * 测试能够移动到空位以及不能移动到已有生物的位置
     */
    @Test
    public void testMove(){
        assertTrue(c1.move(1,0));
        assertTrue(c1.getPosition().equals(new Position(2,1)));
        assertFalse(c2.move(1,-1));
    }
}
