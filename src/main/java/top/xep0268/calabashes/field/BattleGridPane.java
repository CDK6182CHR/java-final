package top.xep0268.calabashes.field;

import javafx.scene.layout.*;

public class BattleGridPane extends GridPane {
    private BattleField field=BattleField.getInstance();
    private static final BattleGridPane instance=new BattleGridPane();

    private BattleGridPane(){
        for(int i=0;i<BattleField.M;i++){
            for(int j=0;j<BattleField.N;j++){
//                Pane pane=map[i][j].getPane();
                Pane pane=field.map.get(i).get(j).getPane();
                final int finalJ = j;
                final int finalI = i;
//                Platform.runLater(()->add(pane, finalJ, finalI));
                add(pane, finalJ, finalI);

                GridPane.setVgrow(pane, Priority.ALWAYS);
                GridPane.setHgrow(pane,Priority.ALWAYS);
            }
        }
        int percent_col = 100/BattleField.N;
        int percent_row = 100/BattleField.M;
        for(int i=0;i<BattleField.N;i++){
            ColumnConstraints constraints=new ColumnConstraints();
            constraints.setPercentWidth(percent_col);
            getColumnConstraints().add(constraints);
        }
        for(int i=0;i<BattleField.M;i++) {
            RowConstraints constraints=new RowConstraints();
            constraints.setPercentHeight(percent_row);
            getRowConstraints().add(constraints);
        }
    }

    public static BattleGridPane getInstance(){
        return instance;
    }
}
