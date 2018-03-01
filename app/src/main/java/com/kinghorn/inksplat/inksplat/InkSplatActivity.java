package com.kinghorn.inksplat.inksplat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InkSplatActivity extends AppCompatActivity {

    private Paint color;
    private Bitmap chosenImage,outputImage;
    private ImageButton brush_up,brush_down;
    private Intent intent;
    private InkCanvas canvas;
    private float size = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ink_splat);

        //Grab the intent information from activity this was started from.
        intent = getIntent();

        if(intent.getExtras().get("InkImgChoice") != null){
            try {
                this.chosenImage = MediaStore.Images.Media.getBitmap(getContentResolver(),(Uri) intent.getExtras().get("InkImgChoice"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        ImplementBrushSizing();
    }

    //Sets the click events for sizing the brush up and down.
    private void ImplementBrushSizing(){
        brush_up = (ImageButton) findViewById(R.id.brush_size_up);
        brush_down = (ImageButton) findViewById(R.id.brush_size_down);


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

            //Draw the chosen image onto the canvas.
            if(chosenImage != null){
                //Center and scale up the chosen image.
                float scaledWidth = (float) getWidth() / (float) chosenImage.getWidth();
                Matrix m = new Matrix();
                m.setScale(scaledWidth,scaledWidth);
                Bitmap scaled = Bitmap.createBitmap(chosenImage,0,0,chosenImage.getWidth(),chosenImage.getHeight(),m,true);

                canvas.drawBitmap(scaled,(getWidth() - scaled.getWidth()) / 2,(getHeight() - scaled.getHeight()) / 2,null);
            }

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

    //Builder class that we will use to set up our instance of inksplat
    public static class InksplatBuilder extends Intent{
        private final Uri InkStartImg,InkTargetImg;
        private Context ctx;
        private Class<?> ac_class;

        public InksplatBuilder(Context ctx,Uri InkStartImg,Uri InkTargetImg,Class<?> clas){
            super(ctx,clas);
            this.InkStartImg = InkStartImg;
            this.InkTargetImg = InkTargetImg;
            this.ctx = ctx;
        }

        //Starts the new intent and launches the activity.
        //Requires the activity we want to go back to.
        public void start(){
            this.putExtra("InkImgChoice",this.InkStartImg);
            this.ctx.startActivity(this);
        }
    }
}
