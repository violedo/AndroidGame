package com.example.myapplication;

import android.app.ActivityManager;
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
import android.media.MediaPlayer;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.math.*;

public class mylayout extends View {
    private Paint mPaint=new Paint();
    private Paint cPaint=new Paint();
    private Paint tPaint=new Paint();
    public static Bitmap newTreasureIsland;
    public enum Gamemode{NOT_START,RUNNING,PAUSE,END,SELECTING};
    private Gamemode gamemode=Gamemode.NOT_START;
    static public int width,height;
    private int timeCount=0;
    private int score=0;
    private int state=-1;
    private int comboTime=0;
    private int keyCount;
    private int perfectCount=0;
    private int goodCount=0;
    private int badCount=0;
    private int missCount=0;
    private enum Difficulty{easy,normal,hard};
    Difficulty difficulty=Difficulty.easy;
    final private int endTime=4000;
    public Queue<Key> keyQueue = new PriorityQueue<>();
    public Queue<Key> runningKeys = new PriorityQueue<>();
    public Queue<Key> unclickedKeys = new PriorityQueue<>();

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
        try {
            newTreasureIsland=BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.newisland);
        }catch (Exception e){
            e.printStackTrace();
        }

        width=this.getMeasuredWidth();
        height=this.getMeasuredHeight();


    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (gamemode){
            case NOT_START:
                if (event.getX()>1000&&event.getX()<1150&&event.getY()>500&&event.getY()<600)
                {
                    gamemode=Gamemode.SELECTING;
                    invalidate();
                }

                break;
            case SELECTING:
                if (event.getX()>200&&event.getX()<800&&event.getY()>300&&event.getY()<600
                        || event.getX()>200&&event.getX()<800&&event.getY()>0&&event.getY()<260
                ||event.getX()>200&&event.getX()<800&&event.getY()>640&&event.getY()<940)
                {

                    prepareKeys();
                    gamemode=Gamemode.RUNNING;
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            while (true&&gamemode==Gamemode.RUNNING){
                                switch (difficulty){
                                    case easy:
                                        Key.lastTime=400;break;
                                    case normal:
                                        Key.lastTime=200;break;
                                    case hard:
                                        Key.lastTime=100;break;
                                }
                                calculateAction();
                                invalidate();
                                try {
                                    Thread.sleep(10);
                                }catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MainActivity.start=true;
                                ++timeCount;
                                if (timeCount>=endTime)
                                {
                                    gamemode=Gamemode.END;
                                    MainActivity.start=false;
                                    MainActivity.stop=true;
                                }
                            }
                        }
                    };
                    new Thread(runnable).start();

                }
                else if (event.getX()>1100&&event.getX()<1900&&event.getY()>700&&event.getY()<850&&event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    switch (difficulty){
                    case easy:difficulty=Difficulty.normal;break;
                    case normal:difficulty=Difficulty.hard;break;
                    case hard:difficulty=Difficulty.easy;break;
                }
                    invalidate();
                }
                break;
            case RUNNING:
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    if (unclickedKeys.isEmpty())
                        break;
                    int pos=(int)(event.getX()*8/width);
                    if (pos<=0)
                        pos=1;
                    if (pos>=7)
                        pos=6;
                    Set<Key> temp=new HashSet<>();
                    while (!unclickedKeys.isEmpty()&&unclickedKeys.peek().getStarttime()<=timeCount-0.75&&unclickedKeys.peek().getPosition()!=pos)
                    {
                            temp.add(unclickedKeys.poll());
                    }
                    if (unclickedKeys.isEmpty()||unclickedKeys.peek().getStarttime()>timeCount-0.75)
                    {
                        for (Key k:temp)
                            unclickedKeys.add(k);
                        temp.clear();
                        break;
                    }

                    int tmp=unclickedKeys.poll().getStarttime()+Key.lastTime*(height-150)/height-timeCount;
                    if (tmp<0)
                        tmp*=-1;
                    if (tmp<20){
                        score+=100;
                        ++perfectCount;
                        state=0;
                    }
                    else if (tmp<40){
                        score+=60;
                        ++goodCount;
                        state=1;
                    }
                    else if (tmp<60){
                        score+=20;
                        ++badCount;
                        state=2;
                    }
                    else {
                        ++missCount;
                        state=3;
                    }
                    comboTime=timeCount;
                    for (Key k:temp)
                        unclickedKeys.add(k);
                    temp.clear();
                }
                break;
        }
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawBitmap(background,new Rect(0,0,background.getWidth(),background.getHeight()),new Rect(0,0,this.getMeasuredWidth(),this.getMeasuredHeight()),mPaint);
        switch (gamemode){
            case NOT_START:
                cPaint.setColor(Color.WHITE);
                canvas.drawRect(1000,500,1150,600,cPaint);
                tPaint.setColor(Color.BLUE);
                tPaint.setTextSize(60);
                canvas.drawText("START",1000,580,tPaint);

                break;

            case SELECTING: tPaint.setColor(Color.BLACK);
                tPaint.setTextSize(100);
                canvas.drawRect(200,300,800,600,cPaint);
                canvas.drawRect(1100,700,1900,850,cPaint);
                canvas.drawText("新 寶 島",300,500,tPaint);
                String string;
                switch (difficulty){
                    case normal:string=new String("normal");break;
                    case hard:string=new String("hard");break;
                    default:string=new String("easy");break;
                }
                canvas.drawText("Difficulty:"+string,1130,820,tPaint);
                cPaint.setAlpha(150);
                canvas.drawRect(200,0,800,260,cPaint);
                canvas.drawRect(200,640,800,940,cPaint);
                cPaint.setAlpha(255);
                tPaint.setAlpha(150);
                canvas.drawText("新 寶 島",300,180,tPaint);
                canvas.drawText("新 寶 島",300,800,tPaint);
                tPaint.setAlpha(255);
                tPaint.setColor(Color.WHITE );
                canvas.drawText("Choose a Song",1600,100,tPaint);
                canvas.drawBitmap(newTreasureIsland,null,new Rect(1100,150,1900,650),cPaint);

                break;
            case RUNNING:
                drawAction(canvas);
                break;
            case END:
                tPaint.setTextSize(200);
                tPaint.setColor(Color.WHITE);
                canvas.drawText(String.format("完成度：%.2f", (float)score/keyCount),(float)(width*0.2),(float)(height*0.3),tPaint);
                canvas.drawText("Perfect*"+perfectCount,(float)(width*0.4),(float)(height*0.5),tPaint);
                tPaint.setTextSize(100);
                canvas.drawText("Good*"+goodCount,(float)(width*0.4),(float)(height*0.65),tPaint);
                canvas.drawText("Bad*"+badCount,(float)(width*0.4),(float)(height*0.8),tPaint);
                canvas.drawText("Miss*"+missCount,(float)(width*0.4),(float)(height*0.95),tPaint);
                break;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
         width=w;
         height=h;
    }


    private void prepareKeys(){
        keyQueue.add(new Key(500-Key.lastTime*(height-150)/height,2,null));
        keyQueue.add(new Key(640-Key.lastTime*(height-150)/height,4,null));
        keyQueue.add(new Key(730-Key.lastTime*(height-150)/height,6,null));
        keyQueue.add(new Key(765-Key.lastTime*(height-150)/height,1,null));
        keyQueue.add(new Key(845-Key.lastTime*(height-150)/height,5,null));
        keyQueue.add(new Key(887-Key.lastTime*(height-150)/height,3,null));
        keyQueue.add(new Key(983-Key.lastTime*(height-150)/height,4,null));
        keyQueue.add(new Key(1049-Key.lastTime*(height-150)/height,2,null));
        keyQueue.add(new Key(1077-Key.lastTime*(height-150)/height,4,null));
        keyQueue.add(new Key(1124-Key.lastTime*(height-150)/height,6,null));
        keyQueue.add(new Key(1208-Key.lastTime*(height-150)/height,1,null));
        keyQueue.add(new Key(1292-Key.lastTime*(height-150)/height,5,null));
        keyQueue.add(new Key(1373-Key.lastTime*(height-150)/height,3,null));
        keyQueue.add(new Key(1405-Key.lastTime*(height-150)/height,4,null));
        keyQueue.add(new Key(1485-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1597-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1644-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1688-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1741-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1791-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1819-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1844-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1881-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1922-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1956-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(1993-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2031-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2071-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2109-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2149-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2190-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2224-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2265-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2302-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2349-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2389-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2423-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2461-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2492-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2514-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2548-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2589-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2626-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2683-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2723-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2767-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2798-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2820-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2857-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2895-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2935-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(2985-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(3029-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));
        keyQueue.add(new Key(3066-Key.lastTime*(height-150)/height,(int)(Math.random()*6+1),null));


        keyCount=54;
    }
    private void calculateAction(){
        for (;!runningKeys.isEmpty()&&timeCount>=runningKeys.peek().getStarttime()+Key.lastTime;){
            runningKeys.poll();
            }
        for (;!unclickedKeys.isEmpty()&&timeCount>=unclickedKeys.peek().getStarttime()+Key.lastTime;){
            unclickedKeys.poll();
            comboTime=timeCount;
            ++missCount;
            state=3;
        }
        for (;!keyQueue.isEmpty()&&timeCount>=keyQueue.peek().getStarttime();)
        {
            unclickedKeys.add(keyQueue.peek());
            runningKeys.add(keyQueue.poll());
        }
    }

    private void drawAction(Canvas canvas){
        drawKeys(canvas);
        drawHUD(canvas);
    }

    private void drawHUD(Canvas canvas){
        cPaint.setColor(Color.WHITE);
        cPaint.setStrokeWidth(10);
        canvas.drawLine(0,height-150,width,height-150,cPaint);
        tPaint.setTextSize(120);
        tPaint.setColor(Color.rgb(255, 218, 185));
        canvas.drawText("Score:"+score,(float)(width*0.8),(float)(height*0.2),tPaint);
        String stat;
        switch (state){
            case -1:
                stat=new String("");
                break;
            case 0:
                stat=new String("PERFECT");
                tPaint.setColor(Color.rgb(238, 201, 0));
                break;
            case 1:
                stat=new String("GOOD");
                tPaint.setColor(Color.rgb(0, 191, 255));

                break;
            case 2:
                stat=new String("BAD");
                tPaint.setColor(Color.rgb(147, 112, 219));

                break;
            default:
                tPaint.setColor(Color.rgb(205, 201, 201));
                stat=new String("MISS");
                break;
        }
        tPaint.setTextSize(200);
        int alpha=255-(timeCount-comboTime)*255/150;
        if (alpha<0)
            alpha=0;
        tPaint.setAlpha(alpha);
        canvas.drawText(stat,(float)(width*0.4),(float)(height*0.4),tPaint);
        tPaint.setAlpha(255);
    }

    private void drawKeys(Canvas canvas){
        cPaint.setColor(Color.GRAY);
        for (Key k:runningKeys){
            float left=width*k.getPosition()/8;
            float top=(timeCount-k.getStarttime())*height/Key.lastTime;
            float right=width*(k.getPosition()+1)/8;
            float ruler=(float)(0.2+top/height*0.8);
            canvas.drawRect(width/2-(width/2-left)*ruler,top-20*ruler,width/2-(width/2-right)*ruler,top+20*ruler,cPaint);
        }
    }
}
