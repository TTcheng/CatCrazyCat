package com.example.wangchuncheng.catcrazycat;

/**
 * Created by WangChunCheng on 2017/12/12.
 */

public class Dot {
    private int x,y;    //coordinator
    private int status; //status

    public static final int STATUS_ON = 1;  //blocked
    public static final int STATUS_OFF = 0; //allow cat in
    public static final int STATUS_IN = 9;  //cat in

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
        status = STATUS_OFF;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
