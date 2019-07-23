package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;



public class Key implements Comparable<Key>{
    private int starttime;
    private int position;
    private Bitmap bitmap;
    final static public int level=1;
    final static public int lastTime=400;

    public Key(int starttime, int position,  Bitmap bitmap) {
        this.starttime = starttime;
        this.position = position;
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas, int time, Paint paint){
        canvas.drawRect(mylayout.width/6*position,mylayout.height*(time-starttime)/lastTime,mylayout.width/6*position+100,mylayout.height*(time-starttime)/lastTime+100,paint
        );
    }



    @Override
    public int compareTo(Key key) {
        return starttime - key.starttime;
    }

    public int getStarttime() {
        return starttime;
    }

    public int getPosition() {
        return position;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
