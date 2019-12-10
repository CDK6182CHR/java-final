package top.xep0268.calabashes;

import top.xep0268.calabashes.Game;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * 2019.12.07 暂定用B触发启动，后面再处理为什么控制键全被listView抓走的事情。
 */
public class Main extends Application{
    Pane mainPane;
    boolean loading=true;
    Game game;
    public static void main(String[] args){
        Application.launch(args);
    }

    /**
     * 启动逻辑：先显示欢迎界面，同时初始化执行initFormations；
     * init完毕后，切换到主界面。
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(847);
        primaryStage.setHeight(530);
        game=Game.getInstance();
        mainPane=Game.getInstance().getMainPane();
        mainPane.prefHeightProperty().bind(primaryStage.heightProperty());
        mainPane.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane pane=frontCoverPane();
        pane.prefHeightProperty().bind(primaryStage.heightProperty());
        pane.prefWidthProperty().bind(primaryStage.widthProperty());
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("量子葫芦娃大战");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                game.initFormations();
                try{
                    TimeUnit.MILLISECONDS.sleep(1300);
                }catch (InterruptedException e){
                    //
                }
                primaryStage.setScene(new Scene(mainPane));
                primaryStage.setMaximized(true);
                loading=false;
            }
        });

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(loading)
                    return;
                if(Game.getInstance().isExecuting())
                    return;
                System.out.println(event.getCode());
                System.out.println(""+primaryStage.getWidth()+","+primaryStage.getHeight());
//                Platform.runLater(()->{
//                    try{
//                        primaryStage.setScene(new Scene(mainPane));
//                    }catch (Exception e){
//                        //暂时没想到更合适的处理方法
//                    }
//                });
                if(event.getCode()== KeyCode.SPACE) {
                    System.out.println("before begin!");
                    Game.getInstance().begin();
                }
                else if(event.getCode()==KeyCode.L){
                    FileChooser chooser=new FileChooser();
                    chooser.setTitle("选择记录文件");
                    chooser.setInitialDirectory(new File("."));
                    chooser.setSelectedExtensionFilter(
                            new FileChooser.ExtensionFilter("Data file","*.dat"
                                    )
                    );
                    File file=chooser.showOpenDialog(primaryStage);
                    if(file!=null){
                        Game.getInstance().loadLog(file);
                    }
                }
                else
                    System.out.println(event.getCode());
            }
        });
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    Game.getInstance().cleanup();
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
        });

        primaryStage.show();
    }

    private Pane frontCoverPane(){
        Pane pane=new Pane();
        Background background=new Background(new BackgroundImage(
                new Image("/cover.png"), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
                null,new BackgroundSize(-1,-1,false,
                false,true,true)
        ));
//        BackgroundImage backgroundImage=new BackgroundImage(
//                new Image("/cover.png"), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
//                null,null
//        );
        pane.setBackground(background);
        return pane;
    }
}
