package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button start=null;
    private TextView title=null;
    private ConstraintLayout constraintLayout=null;
    public RunThread runThread=null;
    public mylayout internalMyLayout = null;


    private View.OnClickListener onClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        internalMyLayout = (mylayout) findViewById(R.id.mylayout);
        init();
    }
    public void init(){
        runThread=new RunThread(internalMyLayout);
        runThread.keyQueue.add(new Key(500,2,null));
        runThread.keyQueue.add(new Key(700,1,null));
        new Thread(runThread).start();
    }
}
