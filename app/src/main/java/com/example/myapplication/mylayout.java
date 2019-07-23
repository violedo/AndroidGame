package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class mylayout extends View {
    private Paint mPaint=new Paint();
    private Paint cPaint=new Paint();
    private Paint tPaint=new Paint();
    public static Bitmap background;
    public enum Gamemode{NOT_START,RUNNING,PAUSE,END};
    private Gamemode gamemode=Gamemode.NOT_START;
    static public int width,height;

    long startTime;
    static public Set<Key> keysToDraw=new HashSet<>();
    public Queue<Key> keyQueue = new PriorityQueue<>();
    public Queue<Key> runningKeys = new PriorityQueue<>();

    public mylayout(Context context) {
        super(context);
        init();
    }
    public mylayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public mylayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public mylayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        width=this.getRight();
        height=this.getBottom();
        try {
            background=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.background);
        }catch (Exception e){
            e.printStackTrace();
        }
        run();
    }


    private void run(){
        startTime=System.currentTimeMillis();
        while (true){
            produceAction();
            dyingAction();
        }
    }

    private void produceAction(){
        for (;timeCount==keyQueue.peek().getStarttime();)
        {
            mylayout.drawKey(keyQueue.peek());
            runningKeys.add(keyQueue.poll());
        }
    }

    private void dyingAction(){

    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (gamemode){
            case NOT_START:
                if (event.getX()>1000&&event.getX()<1150&&event.getY()>500&&event.getY()<600)
                {
                    gamemode=Gamemode.RUNNING;
                }
        }
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(background,null,new Rect(0,0,this.getRight(),this.getBottom()),mPaint);
        switch (gamemode){
            case NOT_START:
                cPaint.setColor(Color.WHITE);
                canvas.drawRect(1000,500,1150,600,cPaint);
                tPaint.setColor(Color.BLUE);
                tPaint.setTextSize(60);
                canvas.drawText("START",1000,580,tPaint);
                break;
            case RUNNING:
                for (Key k:keysToDraw)
                break;

        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
}
