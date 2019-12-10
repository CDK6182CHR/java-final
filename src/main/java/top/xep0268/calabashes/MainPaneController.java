package top.xep0268.calabashes;

import top.xep0268.calabashes.field.BattleGridPane;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MainPaneController {
    @FXML
    public ListView<String> listView,calabashList,demonList;
    @FXML
    public BattleGridPane gridPane;
}
