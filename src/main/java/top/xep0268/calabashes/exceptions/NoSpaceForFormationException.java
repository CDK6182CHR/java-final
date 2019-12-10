package top.xep0268.calabashes.exceptions;

import top.xep0268.calabashes.formations.Formation;

public class NoSpaceForFormationException extends Exception {
    private Formation form;
    public NoSpaceForFormationException(Formation f){
        form=f;
    }
    public String toString(){
        return super.toString()+"Cannot find place for leader to format as "+form;
    }
}
