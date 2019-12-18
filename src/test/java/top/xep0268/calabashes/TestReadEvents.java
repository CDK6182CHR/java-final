package top.xep0268.calabashes;

import top.xep0268.calabashes.log.*;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestReadEvents {
    public static void main(String[] args) throws IOException {
        EventReader reader=new EventReader(new File(
                "CalabashLogs\\Log-2019-12-18-11-10-11.dat"));
        AbstractEvent event;
        List<AbstractEvent> list=new LinkedList<>();
        do{
            event=reader.read();
            if(event!=null)
                list.add(event);
        }while(event!=null);
        Collections.sort(list);
        for (AbstractEvent event0 : list) {
            System.out.println(event0);
        }
    }
}
