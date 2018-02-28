package com.kinghorn.inksplat.inksplat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class InkSplatActivity extends AppCompatActivity {

    private Paint color;
    private Uri chosenImage,outputImage;
    private Intent intent;
    private InkCanvas canvas;
    private float size = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ink_splat);

        //Grab the intent information from activity this was started from.
        intent = getIntent();

        //Initialize the color object.
        color = new Paint();
        color.setColor(Color.BLACK);
        color.setStyle(Paint.Style.STROKE);
        color.setAntiAlias(true);
        color.setStrokeCap(Paint.Cap.ROUND);
        color.setStrokeWidth(size);
        color.setStrokeJoin(Paint.Join.ROUND);

        canvas = new InkCanvas(getApplicationContext());

        RelativeLayout l = findViewById(R.id.paint_layout);
        l.addView(canvas);

        ImplementColorSwatches();
    }

    //Loops through the color swatch list and adds click events for each color swatch.
    private void ImplementColorSwatches(){
        final ViewGroup swatchList = findViewById(R.id.swatch_list);

        for(int i = 0;i < swatchList.getChildCount();i++){
            final View v = swatchList.getChildAt(i);
            v.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    //Here we want to grab the color of the swatch, make all the swatches transparent and make this particular one not.
                    for(int k = 0;k < swatchList.getChildCount();k++){
                        swatchList.getChildAt(k).setAlpha(.3f);
                    }

                    color.setColor(v.getBackgroundTintList().getDefaultColor());
                    canvas.invalidate();
                    v.setAlpha(1);
                }
            });
        }
    }

    public class InkPath extends Path{
        private Paint p;

        public InkPath(int color){
            p = new Paint();
            p.setColor(color);
            p.setStyle(Paint.Style.STROKE);
            p.setAntiAlias(true);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(size);
            p.setStrokeJoin(Paint.Join.ROUND);
        }

        public Paint getPaint(){
            return this.p;
        }
    }

    //Canvas that we will use to draw the image onto.
    public class InkCanvas extends View{

        private InkPath p = new InkPath(Color.BLACK);
        private ArrayList<InkPath> paths;

        public InkCanvas(Context c){
            super(c);

            paths = new ArrayList<InkPath>();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for(InkPath path:paths){
                canvas.drawPath(path,path.getPaint());
            }

            canvas.drawPath(p,color);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    p.moveTo(event.getX(),event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    p.moveTo(event.getX(),event.getY());

                    InkPath newPath = new InkPath(color.getColor());
                    newPath.set(p);
                    paths.add(newPath);

                    p.reset();
                    break;
                case MotionEvent.ACTION_MOVE:
                    p.lineTo(event.getX(),event.getY());
                    break;
            }
            invalidate();
            return true;
        }
    }
}
