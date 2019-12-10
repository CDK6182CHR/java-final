package top.xep0268.calabashes.log;

import javafx.scene.control.Alert;

import java.io.*;

/**
 * 给定文件名（绝对文件名），读取数据。封装<code>ObjectInputStream</code>
 */
public class EventReader implements AutoCloseable{
    private ObjectInputStream objectInputStream;
    public EventReader(File file)throws IOException{
        objectInputStream=new ObjectInputStream(new FileInputStream(file));
    }

    /**
     * @return 记录中的下一条记录。如果已经结束了，返回null.
     */
    public AbstractEvent read(){
        try {
            AbstractEvent event = (AbstractEvent) objectInputStream.readObject();
            return event;
        }catch(EOFException e){
            return null;
        } catch (InvalidClassException e){
            //旧版数据模型，无法读取
            Alert alert=new Alert(Alert.AlertType.ERROR,
                    "对不起，不支持读取旧版数据模型！");
//            alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
//                @Override
//                public void handle(DialogEvent event) {
//                    System.exit(0);
//                }
//            });
            alert.showAndWait();
            System.exit(0);
//            try{
//                TimeUnit.SECONDS.sleep(20);
//                System.exit(0);
//            }catch (InterruptedException e0){
//                //
//            }
//            System.exit(0);
        } catch (IOException e){
            System.out.println("IOException caught");
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            System.out.println("ClassNotFoundException caught");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close(){
        try{
            objectInputStream.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
