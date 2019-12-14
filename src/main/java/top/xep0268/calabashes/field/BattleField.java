package top.xep0268.calabashes.field;

/**
 * 实现图形化的战场。继承Field，单例模式。
 */
public class BattleField extends Field<GraphicBlock> {
//    private BattleGridPane gridPane;

    private static final BattleField instance=new BattleField();
    public static BattleField getInstance(){
        return instance;
    }

    private BattleField(){
        super(GraphicBlock.class);
    }

//    private void initPane(){
//        gridPane=new GridPane();
//        for(int i=0;i<M;i++){
//            for(int j=0;j<N;j++){
////                Pane pane=map[i][j].getPane();
//                Pane pane=map.get(i).get(j).getPane();
//                gridPane.add(pane,j,i);
//                GridPane.setVgrow(pane, Priority.ALWAYS);
//                GridPane.setHgrow(pane,Priority.ALWAYS);
//            }
//        }
//        int percent_col = 100/N;
//        int percent_row = 100/M;
//        for(int i=0;i<N;i++){
//            ColumnConstraints constraints=new ColumnConstraints();
//            constraints.setPercentWidth(percent_col);
//            gridPane.getColumnConstraints().add(constraints);
//        }
//        for(int i=0;i<M;i++) {
//            RowConstraints constraints=new RowConstraints();
//            constraints.setPercentHeight(percent_row);
//            gridPane.getRowConstraints().add(constraints);
//        }
//    }

//    public synchronized GridPane getGridPane(){
//        if(gridPane==null)
//            initPane();
//        return gridPane;
//    }
}
