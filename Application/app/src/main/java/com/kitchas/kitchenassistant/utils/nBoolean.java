package com.kitchas.kitchenassistant.utils;

public class nBoolean {
    private boolean flag;
    private Long time;
    private static final Long millisSecondFactor = 1000l;

    private nBoolean(boolean flag){
        this.flag = flag;
        this.time = System.currentTimeMillis();
    }

    public static nBoolean initWithTrue(){
        return new nBoolean(true);
    }

    public static nBoolean initWithFalse(){
        return new nBoolean(false);
    }

    public void changeValue(){
        this.flag = !this.flag;
    }

    public boolean compare(boolean flag){
        long currentTime = System.currentTimeMillis();
        this.time = currentTime - this.time > millisSecondFactor ? currentTime : this.time;
        return this.flag == flag && currentTime == this.time;
    }
}
