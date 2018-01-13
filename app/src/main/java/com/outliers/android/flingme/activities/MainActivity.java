package com.outliers.android.flingme.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.outliers.android.flingme.R;
import com.outliers.android.flingme.constants.AppConstants;
import com.outliers.android.flingme.utils.ProjectUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends com.outliers.android.flingme.activities.FlingMeParentActivity implements View.OnLayoutChangeListener, SurfaceHolder.Callback, View.OnTouchListener, PopupMenu.OnMenuItemClickListener {

    //Constants
    private final int EASY_SPEED_LOWER_LIMIT = 10;
    private final int EASY_SPEED_HIGH_LIMIT = 20;
    private final int EASY_RADIUS = 50;

    private final int MODERATE_SPEED_LOWER_LIMIT = 15;
    private final int MODERATE_SPEED_HIGH_LIMIT = 30;
    private final int MODERATE_RADIUS = 40;

    private final int PRO_SPEED_LOWER_LIMIT = 25;
    private final int PRO_SPEED_HIGH_LIMIT = 40;
    private final int PRO_RADIUS = 30;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    int screenH,screenW;
    int leftPos = 0;
    int topPos = 0;
    Bitmap circleBitmap;
    Paint paint,textPaint,lightPaint;
    boolean forward = true;
    String TAG = "";
    View view;
    int radius,halfRadius,radius1pt5;
    //CircleView circleView;
    boolean isActionOnCircle;
    int yOffset;
    int RConversionFactor, GConversionFactor, BConversionFactor;
    int canvasHeight;
    ArrayList<String> movePath, predictedPath;
    String text = "Fling me!";
    double degree;
    int catchAttempts;
    double speed;
    long startTime,finishTime;
    boolean viewMoving,gameOn;
    SharedPreferences appPref;
    TextView tvStart,tvCountDown;
    ImageView ivSettings;
    RelativeLayout rlCountdown;
    int difficultyLevel;
    int lowerSpeedLimit;
    int higherSpeedLimit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);

        setContentView(R.layout.activity_main);
        appPref = getSharedPreferences(AppConstants.APP_PREF_NAME,MODE_PRIVATE);

        tvStart = (TextView) findViewById(R.id.tv_start);
        ivSettings = (ImageView) findViewById(R.id.iv_settings);
        rlCountdown = (RelativeLayout) findViewById(R.id.rl_count_down);

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planMovement();
            }
        });
        tvCountDown = (TextView) findViewById(R.id.tv_countdown);

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,ivSettings);
                popupMenu.inflate(R.menu.pop_up_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(MainActivity.this);
            }
        });

        movePath = new ArrayList<>();
        predictedPath = new ArrayList<>();

        DisplayMetrics outMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        }
        else{
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        }
        screenH = outMetrics.heightPixels;
        screenW = outMetrics.widthPixels;

        RConversionFactor = Math.round(screenW/255f);
        GConversionFactor = Math.round(screenH/255f);
        BConversionFactor = Math.round((screenW+screenH)/255f);

        radius = ProjectUtil.dpToPx(50,this);
        halfRadius = radius/2;
        radius1pt5 = (int) (radius/1.5f);
        yOffset = 0;//getActionBarHeight()+getStatusBarHeight();
        canvasHeight = screenH - yOffset;
        //Log.e("onCreate","H:"+screenH+",W:"+screenW+",yOffset:"+yOffset+",mid:"+((screenH/2) - (radius/2) + yOffset)+",radius:"+radius);
        //circleView = new CircleView(this);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.circle_color_dark));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        lightPaint = new Paint(paint);
        lightPaint.setColor(getResources().getColor(R.color.circle_color_light));

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(radius/3);
        //textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(1);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        /*view = new View(this);
        Shape shape = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawCircle(0,0,radius,paint);
            }
        };
        ShapeDrawable circleDrawable = new ShapeDrawable(shape);
        view.setBackground(circleDrawable);*/

        circleBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shape_circle);
        //circleDrawable.draw(canvas);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.setKeepScreenOn(true);
        surfaceView.addOnLayoutChangeListener(this);
        surfaceView.setOnTouchListener(this);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        difficultyLevel = appPref.getInt(AppConstants.PREF_KEY_DIFFICULTY_LEVEL,0);
        switch (difficultyLevel){
            case 0:
                radius = ProjectUtil.dpToPx(EASY_RADIUS,this);
                lowerSpeedLimit = EASY_SPEED_LOWER_LIMIT;
                higherSpeedLimit = EASY_SPEED_HIGH_LIMIT;
                break;

            case 1:
                radius = ProjectUtil.dpToPx(MODERATE_RADIUS,this);
                lowerSpeedLimit = MODERATE_SPEED_LOWER_LIMIT;
                higherSpeedLimit = MODERATE_SPEED_HIGH_LIMIT;
                break;

            case 2:
                radius = ProjectUtil.dpToPx(PRO_RADIUS,this);
                lowerSpeedLimit = PRO_SPEED_LOWER_LIMIT;
                higherSpeedLimit = PRO_SPEED_HIGH_LIMIT;
                break;
        }
        textPaint.setTextSize(radius/3);
    }

    @Override
    public void onStop(){
        super.onStop();
        isActionOnCircle = true;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //Log.e("onLayoutChange",left+","+top+","+right+","+bottom);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.e("surfaceCreate","surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.e("surfaceChanged","w:"+width+",h:"+height);
        //startAnimation();
        leftPos = width/2;
        topPos = height/2;
        drawView(leftPos,topPos);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void planMovement(){
        try {
            final int x1 = ProjectUtil.getRandomInt(0 + radius, screenW - radius);
            final int y1 = ProjectUtil.getRandomInt(0 + radius, canvasHeight - radius);

            int x2 = 0;
            int y2 = 0;

            //TODO we can implement code to calculate slope based on difficulty level and then pixel speed
            int deltaX = ProjectUtil.getRandomInt(lowerSpeedLimit, higherSpeedLimit);
            int deltaY = ProjectUtil.getRandomInt(lowerSpeedLimit, higherSpeedLimit);

            if (Math.random() > 0.5f) {
                //deltaX -ve
                deltaX = -1 * deltaX;
            }

            if (Math.random() > 0.5f) {
                //deltaY -ve
                deltaY = -1 * deltaY;
            }

            int tempX = x1 + deltaX, tempY = y1 + deltaY;
            if (tempX >= 0 + radius && tempX <= screenW - radius) {
                x2 = tempX;
            } else {
                x2 = x1 - deltaX;
            }

            if (tempY >= 0 + radius && tempY <= canvasHeight - radius) {
                y2 = tempY;
            } else {
                y2 = y1 - deltaY;
            }

            final int x2Final = x2;
            final int y2Final = y2;

            isActionOnCircle = true;
            drawView(x1, y1);
            Log.e("plan", "x1:" + x1 + ",y1:" + y1 + ",x2:" + x2 + ",y2:" + y2);
            rlCountdown.setVisibility(View.VISIBLE);
            //scaleUpAndFadeOutAnimation(tvCountDown);
            hideViews();
            new CountDownTimer(4000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    //tvCountDown.clearAnimation();
                    int left = ((int) Math.round(millisUntilFinished / 1000));
                    if(left > 1)
                        tvCountDown.setText(left+"");
                    else
                        tvCountDown.setText("Go!!");
                    scaleUpAndFadeOutAnimation(tvCountDown);
                    Log.e("onTick",millisUntilFinished+"");
                }

                @Override
                public void onFinish() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlCountdown.setVisibility(View.GONE);
                        }
                    },500);

                    movePath.add(10+","+10);
                    movePath.add(10+","+10);
                    movePath.add(x1 + "," + y1);
                    movePath.add(x2Final + "," + y2Final);
                    gameOn = true;
                    predictPath();
                }
            }.start();
        }catch (Exception ex){
            Log.e("plan",Log.getStackTraceString(ex));
        }
    }

    private void animateFromTo(int x1, int y1, int x2, int y2, long duration){

    }

    private void scaleUpAndFadeOutAnimation(TextView tv){
        //tv.animate().scaleXBy(2f).scaleY(2f).alpha(0f).setDuration(1000).start();
        AlphaAnimation fadeOut = new AlphaAnimation(1,0);
        fadeOut.setFillAfter(false);
        //fadeOut.setDuration(1100);
        ScaleAnimation scaleUp = new ScaleAnimation(1,2,1,2,AnimationSet.RELATIVE_TO_SELF,0.5f,AnimationSet.RELATIVE_TO_SELF,0.5f);
        scaleUp.setFillAfter(false);
        //scaleUp.setDuration(1100);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(scaleUp);
        animationSet.setDuration(1300);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(tvCountDown.getText().toString().toLowerCase().contains("go")){
                    tvCountDown.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tv.startAnimation(animationSet);
    }

    private void startAnimation(){
        final Handler handler = new Handler();
        final int midHeight = screenH/2;
        final int increment = 10;//radius/4;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //if (leftPos+radius <= screenW && leftPos >= 0) {
                if(forward) {
                    leftPos += increment;
                    if(leftPos+radius >= screenW){
                        leftPos = screenW-radius;// - radius - increment;
                        forward = false;
                    }
                    ////Log.e("leftPostInc", "leftPos=" + leftPos);
                }else {
                    leftPos -= increment;
                    if(leftPos-radius <= 0){
                        leftPos = 0 + radius;
                        forward = true;
                    }
                    ////Log.e("leftPostDec", "leftPos=" + leftPos);
                }
                /*}else {
                    if(leftPos+radius >= screenW) {
                        forward = false;
                        leftPos -= increment;
                        //Log.e("forwardGrt", "forward:"+forward);
                    }else if(leftPos <= 0) {
                        forward = true;
                        leftPos += increment;
                        //Log.e("forwardLess", "forward:"+forward);
                    }
                }*/
                ////Log.e("startAnimation", "leftPos=" + leftPos+",forward:"+forward);
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                topPos = canvas.getHeight()/2;
                ////Log.e("topPos:",topPos+"");
                topPos = canvas.getHeight()/2;
                leftPos = canvas.getWidth()/2;
                canvas.drawCircle(leftPos, topPos, radius, paint);
                holder.unlockCanvasAndPost(canvas);
                //handler.postDelayed(this,20);
            }
        };
        handler.postDelayed(runnable,20);
    }

    public int getStatusBarHeight() {
        TAG = "getStatusBarHeight";
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d(TAG,"statusbar height="+result);
        return result;
    }

    public int getActionBarHeight() {
        TAG = "getActionBarHeight";
        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        Log.d(TAG,"ActionBar height="+actionBarHeight);
        return actionBarHeight;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float relativeX = event.getX();
        float relativeY = event.getY();
        //int realY = rawY - yOffset;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                //Log.e("ActionDown","x:"+relativeX+",y:"+relativeY+"leftPos:"+leftPos+",topPos:"+topPos);
                if(isViewTouched(Math.round(relativeX), Math.round(relativeY), leftPos, topPos)){
                    isActionOnCircle = true;
                    text = "Fling Me!";
                    movePath.add(relativeX+","+relativeY);
                    if(viewMoving && gameOn){
                        calculateScore();
                    }else{
                        showViews();
                    }
                }else{
                    isActionOnCircle = false;
                    if(viewMoving && gameOn){
                        catchAttempts++;
                    }
                }
                //Log.e("ActionDown","isCircleTouch:"+isActionOnCircle);
                break;

            case MotionEvent.ACTION_MOVE :
                if(isActionOnCircle){
                    drawView(Math.round(relativeX), Math.round(relativeY));
                    movePath.add(relativeX+","+relativeY);
                    //Log.e("ActionMove","x:"+relativeX+",y:"+relativeY);
                }
                break;

            case MotionEvent.ACTION_UP :
                if(isActionOnCircle) {
                    isActionOnCircle = false;
                    //Log.e("ActionUp", "x:" + relativeX + ",y:" + relativeY);
                    text = "Catch Me!";
                    predictPath();
                }
                break;
        }
        return true;
    }

    private void calculateScore(){
        double degScore = 0;
        int speed = (int)Math.round(this.speed);

        if(this.degree == 0 || this.degree == 90){
            degScore = 0;
        }else{
            degScore = Math.round(degree > 45 ? 90 - degree : degree);
        }
        float degreeFactor = (int)Math.round(degScore/10f);
        degreeFactor = (difficultyLevel+1) * degreeFactor;
        int score = 0;
        if(degreeFactor != 0)
            score =  (int) ((speed * degreeFactor) - catchAttempts);
        else
            score = speed - catchAttempts;
        int time = (int) (System.currentTimeMillis()-startTime)/1000;
        score = score - time;
        score = score < 0 ? 0 : score;
        //score = (difficultyLevel+1) * score;
        if(System.currentTimeMillis() - finishTime > 2000) {
            Toast.makeText(this, "Score is " + Math.round(score), Toast.LENGTH_LONG).show();
            String scoreSummary = "Movement Difficulty Bonus : "+degreeFactor+"\n"+
                    "Speed Bonus : "+speed+"\n"+
                    "Time Taken : "+ time+"\n"+
                    "Catch Attempts : "+ catchAttempts+"\n";
            scoreSummary += "Your Score is "+score;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Score Card");
            builder.setMessage(scoreSummary);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearScoreVariables();
                    showViews();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.show();
            dialog.getWindow().getDecorView().setSystemUiVisibility(this.getWindow().getDecorView().getSystemUiVisibility());
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            Log.e("Score", score + ",deg:" + degree + ",degS:" + degScore + ",speed:" + speed + ",ca:" + catchAttempts + ",time:" + time);
            finishTime = System.currentTimeMillis();
            
            try {
                JSONObject gameObject = new JSONObject();
                gameObject.put("id",finishTime);
                gameObject.put("score",score);
                gameObject.put("movementDiff",degreeFactor);
                gameObject.put("speedBonus",speed);
                gameObject.put("timeTaken",time);
                gameObject.put("catchAttempts",catchAttempts);
                
                JSONArray gamesHistory = new JSONArray(appPref.getString(AppConstants.PREF_KEY_GAME_HISTORY,"[]"));
                gamesHistory.put(gameObject);
                appPref.edit().putString(AppConstants.PREF_KEY_GAME_HISTORY,gamesHistory.toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void clearScoreVariables(){
        startTime = 0;
        catchAttempts = 0;
        gameOn = false;
    }

    private void drawView(int x, int y){
        try {
            //set argb with alpha with 128 for tail effect
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.rgb(x/RConversionFactor,y/GConversionFactor,(x+y)/BConversionFactor));//, PorterDuff.Mode.CLEAR);
            //TODO with mod screen flashes because is suddenly jumps from 244 to 0. possible fix can be to reverse the values once reached max.
            canvas.drawCircle(x, y, radius, paint);
            //canvas.drawCircle(x-radius+(radius/3),y+radius-(radius/3),radius/3,lightPaint);
            //float diff = ((radius*2) - textPaint.measureText(text))/2;
            //canvas.drawText(text,x-radius1pt5,y+10,textPaint);
            //Rect rect = new Rect();
            //textPaint.getTextBounds(text,0,text.length()+1,rect);rect.
            canvas.drawText(text,x-(textPaint.measureText(text)/2),y+(textPaint.descent()),textPaint);
            holder.unlockCanvasAndPost(canvas);
            leftPos = x;
            topPos = y;
        }catch (Exception ex){

        }
    }

    private boolean isViewTouched(int touchX, int touchY, int viewX, int viewY){
        boolean result = false;
        if(touchX >= viewX-radius && touchX <= viewX+radius && touchY <= viewY+radius && touchY >= viewY-radius){
            result = true;
        }

        return result;
    }

    private void predictPath(){
        if(movePath.size() > 3){
            String coord = movePath.get(movePath.size()-1);
            float x2 = Float.parseFloat(coord.split(",")[0]);
            float y2 = Float.parseFloat(coord.split(",")[1]);
            coord = movePath.get(movePath.size()-2);
            float x1 = Float.parseFloat(coord.split(",")[0]);
            float y1 = Float.parseFloat(coord.split(",")[1]);
            viewMoving = true;
            isActionOnCircle = false;
            hideViews();
            startTime = System.currentTimeMillis();
            solveLineEquation(x1,y1,x2,y2);
        }else{
            viewMoving = false;
            Toast.makeText(this,"Drag more",Toast.LENGTH_SHORT).show();
        }
    }

    private void hideViews(){
        /*tvStart.animate().setDuration(600).translationY(screenH+tvStart.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivSettings.animate().setDuration(600).translationX(screenW+50).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivSettings.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tvStart.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();*/
        if(tvStart.getVisibility() == View.VISIBLE) {
            TranslateAnimation tvTranslate = new TranslateAnimation(0, 0, 0, tvStart.getHeight() + 50);
            tvTranslate.setDuration(600);
            tvTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tvStart.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            tvStart.startAnimation(tvTranslate);
        }

        if(ivSettings.getVisibility() == View.VISIBLE) {
            TranslateAnimation ivTranslate = new TranslateAnimation(0, 50, 0, 0);
            ivTranslate.setDuration(600);
            ivTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivSettings.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ivSettings.startAnimation(ivTranslate);
        }

    }

    private void showViews(){
        tvStart.setVisibility(View.VISIBLE);
        ivSettings.setVisibility(View.VISIBLE);
        /*ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tvStart,"translationY",screenH+50,tvStart.getY());
        objectAnimator.setDuration(600);
        objectAnimator.start();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(ivSettings,"translationX",screenW+50,ivSettings.getX());
        objectAnimator1.setDuration(600);
        objectAnimator1.start();*/
        float tvY = tvStart.getY();
        float ivX = ivSettings.getX();
        TranslateAnimation tvTranslate = new TranslateAnimation(0,0,tvStart.getHeight(),0);
        tvTranslate.setDuration(600);
        tvTranslate.setFillAfter(true);
        tvStart.startAnimation(tvTranslate);

        TranslateAnimation ivTranslate = new TranslateAnimation(50,0,0,0);
        ivTranslate.setDuration(600);
        ivTranslate.setFillAfter(true);
        ivSettings.startAnimation(ivTranslate);
    }

    private void solveLineEquation(final float x1, final float y1, final float x2, final float y2){
        Thread calculationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //try {
                float deltaX = x2 - x1;
                float deltaY = -1*(y2-y1); //since in devices y increases as you go down
                speed = Math.sqrt((Math.pow((x2-x1),2)+Math.pow((y2-y1),2)));
                float tanTheta = deltaY / deltaX;
                float C = y2 - (tanTheta * x2);
                degree = Math.abs(Math.toDegrees(Math.atan(tanTheta)));
                //Log.e("degree",degree+" deg");
                //Log.e("solveEq", x1 + "," + y1 + "," + x2 + "," + y2 + "," + deltaX + "," + deltaY + "," + degree + ",C=" + C + ",m=" + tanTheta);
                float xIncrement = 0;
                if (deltaX > 0 && deltaY > 0) {
                    //towards 1st quad

                } else if (deltaX < 0 && deltaY > 0) {
                    //towards 2nd quad
                    //degree = 180 - degree;
                } else if (deltaX < 0 && deltaY < 0) {
                    //towards 3rd quad
                    //degree = 180 + degree;
                } else if (deltaX > 0 && deltaY < 0) {
                    //towards 4th quad
                    //degree = 360 - degree;
                }
                xIncrement = deltaX;
                //Log.e("solveEq", "Deg:" + degree + ",XInc:" + xIncrement);
                //y = tanThetaX - tanThetaX1 + y1
                //OR
                //y = mx + b;
                predictedPath.clear();
                predictedPath.add(x1 + "," + y1);
                predictedPath.add(x2 + "," + y2);
                float x = x2;
                float y = 0;
                float pX, pY;
                float yChange = 0f;
                int i = 2;
                //for(int i = 1; i < 100; i++){
                while (!isActionOnCircle) {
                    //Log.e("whileStart", "predSize:" + predictedPath.size() + ",i:" + i);
                    pX = Float.parseFloat(predictedPath.get(predictedPath.size() - 1).split(",")[0]);
                    pY = Float.parseFloat(predictedPath.get(predictedPath.size() - 1).split(",")[1]);
                    x += xIncrement;
                    //y = (int) (tanTheta * (x - pX) + pY) * -1;
                    if (deltaX != 0) {
                        y = (tanTheta * x) + C;
                    } else {
                        y = pY + deltaY;
                    }
                    float adjustedY = (pY + (-1 * deltaY));
                    //Log.e("predicted", "x=" + x + ",y=" + y + ",pY:" + pY + ",newY:" + adjustedY);
                    if (isWithinBounds(x, adjustedY)) {
                        predictedPath.add(x + "," + adjustedY);
                        drawView(Math.round(x), Math.round(adjustedY));
                    } else {
                        //calculate rebound and add to predictedPath
                        //float y1 = pY - (-1 * adjustedY);
                        float newSlope = -1 * tanTheta;
                        float xR,yR=0;
                        if ((adjustedY-radius) <= 0 || (adjustedY+radius) >= canvasHeight) { //Y is limiting factor
                            //Log.e("limitingFactor", "y");
                            xR = x + xIncrement;//(1.5f*xIncrement);
                            yR = adjustedY - (-1*deltaY);//float adjustedY = (pY + (-1 * deltaY));
                        } else {
                            //Log.e("limitingFactor", "x");
                            //xR = x > screenW/2 ? (screenW-radius-xIncrement) : (radius+xIncrement); //x - xIncrement;
                            if(x > screenW/2){
                                xR = screenW-radius-xIncrement;
                                x = screenW-radius;
                            }else{
                                xR = radius+(-1*xIncrement);
                                x = radius;
                            }
                            yR = adjustedY + (-1*deltaY);//y + deltaY;
                        }
                        //yR = newSlope * xR + C;
                        //Log.e("ReboundPoints", "x1=" + x + ",y1=" + y + ",xR:" + xR + ",yR:" + yR);
                        solveLineEquation(x, adjustedY, xR, yR);
                        return;
                    }
                    if(predictedPath.size()!= 0)
                        predictedPath.remove(0);
                    i++;
                    //}
                }
                movePath.clear();
                predictedPath.clear();
                /*}catch (Exception ex){

                }*/
            }
        });
        calculationThread.start();
        //startPredictedAnimation();
    }

    private void startPredictedAnimation(){

    }

    private boolean isWithinBounds(float x, float y){
        boolean result = false;
        if((x-radius) >= 0 && (x+radius) <= screenW && (y-radius) >= 0 && (y+radius) <= canvasHeight){
            result = true;
        }
        //Log.e("isWithinBounds",result+"");
        return result;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.score_history :
                //scores history here
                Intent scoreIntent = new Intent(this,ScoreHistoryDialogActivity.class);
                startActivity(scoreIntent);
                break;

            case R.id.settings :
                Intent settingIntent = new Intent(this,SettingsActivity.class);
                startActivity(settingIntent);
                break;
        }
        return false;
    }
}
