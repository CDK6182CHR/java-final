/*
 * 重新设计的阵型抽象基类。
 * 只包括阵型排布方式的数据，不包括如何排布的处理逻辑。
 */
package calabashes.formations;

import calabashes.field.Position;

public abstract class Formation {
    /*
     * 指定从者的坐标序列，且不包括中心。
     */
    protected Position[] coordinates;
    protected final int N; // blank final
    public Formation(int n){
        N=n;
        coordinates=new Position[N];
        form();//动态绑定到子类的方法
    }

    /*
     * 抽象的阵型实现。
     */
    protected abstract void form();

    /*
     * 由coordinates中的相对坐标转换出绝对坐标返回。
     */
    public Position[] positions(Position center){
        Position[] positions=new Position[N];
        for(int i=0;i<N;i++){
            positions[i]=coordinates[i].add(center);
        }
        return positions;
    }
}
