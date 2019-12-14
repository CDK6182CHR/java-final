package top.xep0268.calabashes.exceptions;

import top.xep0268.calabashes.field.Block;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.formations.Formation;

public class NoSpaceForFormationException extends Exception {
    private Formation form;
    private Field<Block<?>> passed;
    public NoSpaceForFormationException(Formation f){
        form=f;
    }
    public NoSpaceForFormationException(Formation f, Field<Block<?>> passed){
        form=f;
        this.passed =passed;
    }
    public String toString(){
        return super.toString()+"Cannot find place for leader to format as "+form;
    }
    public  Field<Block<?>> getPassed(){
        return passed;
    }
}
