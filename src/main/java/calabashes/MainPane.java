//package calabashes;
//
//import calabashes.field.BattleField;
//import javafx.scene.control.ListView;
//import javafx.scene.layout.*;
//
//public class MainPane extends HBox {
//    private GridPane gridPane;
//    private ListView<String> listView;
//    public MainPane(){
//        gridPane= BattleField.getInstance().getGridPane();
//        listView=Game.getInstance().getListView();
//        getChildren().add(gridPane);
//        getChildren().add(listView);
//        prefHeightProperty().bind(gridPane.prefHeightProperty());
//        HBox.setHgrow(gridPane, Priority.ALWAYS);
//    }
//}
