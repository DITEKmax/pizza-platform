package edu.rutmiit.exception;

public class DublicateItemException extends RuntimeException{
    public DublicateItemException (Long Id){
        super(String.format("Menu Item  id=%d cannot be add to menu or order (dublicate)", Id));
    }
}
