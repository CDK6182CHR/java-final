package calabashes.field;
import calabashes.Game;
import calabashes.items.Living;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * N*N的场地。
 * 先行后列。x为横坐标。访问格式为map[x][y].
 *
 * 死循环问题：
 * 1. 每个线程无法结束的情况
 * 2. 每个线程都能结束，但是循环。
 * ！！必须保证每个线程在一个周期内结束
 * !! 已经死亡的生物为什么会又冒出来？
 * synchronized锁是对象只能进一个，还是只能进一个方法？  --对象只能进一个。
 * synchronized(lock)这样的代码块只允许一个线程进入；进入不会导致lock.lock()发生。
 * 所以线程死在等待synchronized block的地方，这个过程恰好是cannot be interrupted的。
 * 现有问题是：对synchronized块的设计考虑不足。
 * 注意：Condition的await必须获得了锁才可以。相当于还是单线程的。不能去等锁。
 *
 * 2019.12.06：改为泛型, 泛型参数为Block的类型。
 * 使用反射方式做Block数组和实例化。
 *
 * 移动失败问题：
 * 考虑将CreatedEvent也处理成scheduled。
 * 2019.12.06最后笔记：考虑重新规范一下写入的情况，还是由调用的地方计算时间戳。
 *
 * todo: 循环问题
 * 1. 结束后重新开始；
 * 2. 禁止中间重新开始或者读取。
 */
public class Field<T extends Block> {
    public static final int M=10,N=12;  //M行, N列
    protected List<List<T>> map;
    Class<T> meta;

    public Field(Class<T> meta){
        this.meta=meta;
//        map=(T[][])(new Block[M][N]);//java.lang.ClassCastException: [[Ljava.lang.Object; cannot be cast to [[Lcalabashes.field.Block;
        map=new ArrayList<>(M);
        for(int i=0;i<M;i++) {
            List<T> row=new ArrayList<>(N);
            map.add(row);
            for (int j = 0; j < N; j++) {
                row.add(instanceBlock(i,j));
            }
        }
    }

    /**
     * 使用泛型之后没法直接new，于是得绕一圈，这个相当于在new。
     */
    private T instanceBlock(int i,int j){
        try{
            return meta.getDeclaredConstructor(int.class,int.class)
                    .newInstance(i,j);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
     * 按Living指定的位置添加。用于初始化。
     */
    public boolean addLiving(Living living){
        if(livingAt(living.getPosition())!=null)
            return false;
        int x=living.getPosition().getX(),y=living.getPosition().getY();
//        map[y][x].setLiving(living);
        map.get(y).get(x).setLiving(living);
        return true;
    }

    /*
     * 将living添加到x,y指定的位置，返回是否成功。
     * 如果成功，修改living中的数据为x,y指定的位置。
     * 否则不做任何操作并返回false。
     */
    private boolean addLiving(Living living, int x, int y){
        if(livingAt(x,y)!=null)
            return false;
//        map[y][x].setLiving(living);
        map.get(y).get(x).setLiving(living);
        living.getPosition().setPos(x,y);
        return true;
    }

    public boolean addLiving(Living living, Position pos){
        return addLiving(living,pos.getX(),pos.getY());
    }

    /**
     * 这里有异常等待
     * @param position
     */
    private void removeLiving(Position position){
        System.out.println("Field::removeLiving "+position
                +", living="+livingAt(position));
//        map[position.getY()][position.getX()].removeLiving();
        map.get(position.getY()).get(position.getX()).removeLiving();
    }

    public Living livingAt(Position pos){
        if(inside(pos))
//            return map[pos.getY()][pos.getX()].getLiving();
            return map.get(pos.getY()).get(pos.getX()).getLiving();
        return null;
    }

    private Living livingAt(int x, int y){
//        return map[y][x].getLiving();
        return map.get(y).get(x).getLiving();
    }

    /*
     * 移动指定生物到指定位置。此函数由Living调用。
     * （移动应该是生物体主动的动作，故由Living主动，此处只是维护数据，
     * 顺便充当检测是否冲突。如果目标位置有生物体了，返回false，不做任何操作。）
     * 后续维护工作由Living自行完成。
     *
     * 2019.11.27补充--此方法只能由living主动调用，Living.move()是同步方法，所以是线程安全的。
     */
    public synchronized boolean moveLiving(Living living, int dx, int dy){
        //保证两边的数据一致
        assert livingAt(living.getPosition())==living;
        int nx=living.getPosition().getX()+dx;
        int ny=living.getPosition().getY()+dy;
        Position oldPosition=living.getPosition().copy();
        if(!inside(living.getPosition().getX()+dx,living.getPosition().getY()+dy))
            return false;
        if(!addLiving(living,nx,ny)) //疑似：移动成功但返回false
            return false;
        removeLiving(oldPosition);
        assert livingAt(living.getPosition())==living;
//        System.out.println("moveLiving: "+living+" "+oldPosition+" -> "
//        +"("+nx+","+ny+")");
        if(!Game.getInstance().isInVideo())
            try {
                TimeUnit.MILLISECONDS.sleep(2);
            }catch (InterruptedException e){
                //
            }
        return true;
    }

    /*
     * 交换两指定生物的位置，由Living调用，且已经保证位置相邻。
     */
    public boolean swapLiving(Living living1, Living living2){
        assert livingAt(living1.getPosition())==living1;
        assert livingAt(living2.getPosition())==living2;
        if(!living1.getPosition().adjacentWith(living2.getPosition()))
            return false;
        removeLiving(living1.getPosition());
        removeLiving(living2.getPosition());
        calabashes.field.Position pos1=living1.getPosition().copy(),
                pos2=living2.getPosition().copy();
        addLiving(living1,pos2);
        addLiving(living2,pos1);
        assert livingAt(living1.getPosition())==living1;
        assert livingAt(living2.getPosition())==living2;
        return true;
    }

    /*
     * 画出当前的面板情况。每行表示一行，每列宽度一个制表符。
     * 各个生物调用toString接口显示。
     */
    public synchronized void draw(){
        System.out.println("------------------------------------------------------");
        System.out.print('\t');
        for(int i=0;i<N;i++){
            System.out.print(""+i+'\t');
        }
        System.out.print('\n');
        for(int i=0;i<M;i++){
            System.out.print(""+i+'\t');
            for(int j=0;j<N;j++) {
                Living l = livingAt(j,i);
                if(l!=null)
                    System.out.print(l.toString());
                System.out.print('\t');
            }
            System.out.print('\n');
        }
        System.out.println("------------------------------------------------------");
    }

    public boolean inside(calabashes.field.Position pos){
        return 0 <= pos.getX() && pos.getX() < N && 0 <= pos.getY() && pos.getY() < M;
    }

    public boolean inside(int x,int y){
        return x>=0 && x<N && y>=0 && y<M;
    }

    public Position randomPosition(){
        Random random=new Random();
        Position position=new Position(random.nextInt(Field.N),
                random.nextInt(Field.M));
        while (livingAt(position)!=null) {
            position.setPos(random.nextInt(Field.N),
                    random.nextInt(Field.M));
        }
        return position;
    }

    public calabashes.field.Position leftRandomPosition(){
        Random random=new Random();
        calabashes.field.Position position=new calabashes.field.Position(random.nextInt(Field.N/2),
                random.nextInt(Field.M));
        while (livingAt(position)!=null) {
            position.setPos(random.nextInt(Field.N/2),
                    random.nextInt(Field.M));
        }
        return position;
    }

    public calabashes.field.Position rightRandomPosition(){
        Random random=new Random();
        calabashes.field.Position position=new calabashes.field.Position(random.nextInt(Field.N/2)+N/2,
                random.nextInt(Field.M));
        while (livingAt(position)!=null) {
            position.setPos(random.nextInt(Field.N/2)+N/2,
                    random.nextInt(Field.M));
        }
        return position;
    }

    private T blockAt(calabashes.field.Position p){
//        return map[p.getY()][p.getX()];
        return map.get(p.getY()).get(p.getX());
    }

    /*
     * 返回p指定的位置能否到达。
     * 不能到达是当且仅当该位置不在范围内，或者存在不可移动的对象。
     */
    public boolean unreachable(calabashes.field.Position p){
        return !inside(p) || !blockAt(p).isReachable();
    }

    public boolean reachable(calabashes.field.Position p){
        return inside(p) && blockAt(p).isReachable();
    }

    /**
     * 在初始化结束后，我们规定生物只能到达空白的格子（不允许交换发生）。
     * @param p 目标单元格位置
     * @return 返回true当且仅当可以安全到达该位置。
     */
    public boolean vacant(calabashes.field.Position p){
        return inside(p) && blockAt(p).isVacant();
    }

    public void clearLivings(){
        for(int y=0;y<M;y++){
            for(int x=0;x<N;x++){
//                map[y][x].removeLiving();
                map.get(y).get(x).removeLiving();
            }
        }
    }


}
