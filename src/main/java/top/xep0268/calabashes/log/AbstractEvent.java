package top.xep0268.calabashes.log;

import top.xep0268.calabashes.field.Position;
import top.xep0268.calabashes.items.Item;

import java.io.Serializable;

/**
 * 程序中所有事件的记录。
 * 常见事件：
 * <ul>
 *     <li>生物创建</li>
 *     <li>生物移动</li>
 *     <li>生物被杀。注意，以杀人的人为主体，被杀的人为客体。</li>
 * </ul>
 * 暂定所有的记录都通过WriteObject记录。
 *
 * 假定所有记录中的M, N总是个常数，不考虑改变的情况。
 * 这样就只需要保存Position。
 * 共有的field:
 * <ul>
 *     <li>事件主体</li>
 *     <li>事件地点 <code>Position</code>对象</li>
 *     <li>事件发生的时间。接受<code>Game.getInstance().currentTimeStamp()</code>参数</li>
 * </ul>
 */
public abstract class AbstractEvent implements Serializable,Comparable<AbstractEvent> {
    Item subject; //主体生物
    Position position;
    private volatile long timeStamp;
    AbstractEvent(Item subject, Position position, long timeStamp){
        this.subject =subject;
        this.position =position.copy();
        this.timeStamp=timeStamp;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public Item getSubject(){
        return subject;
    }

    public Position getPosition(){
        return position;
    }

    protected abstract int getTypeCode();

    /**
     * 2019.12.06添加逻辑：
     * 如果是同类型的，则按时间排序。
     */
    @Override
    public int compareTo(AbstractEvent abstractEvent){
        if(getClass()==abstractEvent.getClass())
            return (int)(getTimeStamp()-abstractEvent.getTimeStamp());
        else
            return getTypeCode()-abstractEvent.getTypeCode();
    }
}
