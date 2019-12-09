package calabashes.log;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将记录的输出封装在这个类里。
 * 程序启动时自动初始化，打开由日期和时间限定的文件。
 * 默认放在CalabashLogs目录下。如果目录不存在，创建目录。
 *
 * 暂定单例模式
 */
public class EventWriter{
    private static final String LOG_DIR="CalabashLogs/";
    private ObjectOutputStream objectOutputStream;

    public EventWriter()throws IOException{
        verifyDir();
        //保证路径存在，可以创建文件
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String filename=LOG_DIR+"Log-"+format.format(new Date())+".dat";
        objectOutputStream=new ObjectOutputStream(new FileOutputStream(filename));
    }

    private void verifyDir()throws IOException{
        File file=new File(LOG_DIR);
        if(!file.exists()&&!file.mkdir()){
            throw new IOException("Fetal: Cannot create log dir");
        }
    }

    public synchronized void write(AbstractEvent event){
        try {
            objectOutputStream.writeObject(event);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void cleanup(){
        try {
            if (objectOutputStream != null)
                objectOutputStream.close();
        }catch(IOException e){
            System.out.println("Close log file failed "+e.toString());
        }
    }
}
