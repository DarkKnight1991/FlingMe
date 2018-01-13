package com.outliers.android.flingme.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.outliers.android.flingme.R;
import com.outliers.android.flingme.utils.ProjectUtil;

/**
 * Created by outliersasu on 11/14/17.
 */

public class CircleView extends View {

    private static int RADIUS;
    Context context;
    Drawable drawable;
    Bitmap bitmap;
    private int x;
    private int y;
    Paint paint;

    public CircleView(Context context) {
        super(context);
        this.context = context;
        drawable = context.getResources().getDrawable(R.drawable.shape_circle);
        //bitmap = drawableToBitmap(drawable);//BitmapFactory.decodeResource(context.getResources(),R.drawable.shape_circle);
        /*if(bitmap == null)
            Log.e("circleView","null");
        else
            Log.e("circleView","not null");*/
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private void init(){
        RADIUS = ProjectUtil.dpToPx(50,context);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw the View
        //drawable.draw(canvas);
        //canvas.drawBitmap(bitmap,x,y,null);
        canvas.drawCircle(x, y, RADIUS, paint);
        Log.e("onDraw","called");
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

