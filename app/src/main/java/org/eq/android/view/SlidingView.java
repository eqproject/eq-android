package org.eq.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.eq.android.R;

/**
 * Created by liufan on 2019/6/2.
 */

public class SlidingView extends LinearLayout implements View.OnTouchListener {
    private ImageView imageView, image_cav;
    private FrameLayout lin;

    int screenWidth;
    int screenHeight;
    int lastX, Left;
    int lastY;
    int poorWidth;
    int l, r, t, b;
    int viewWidth;
    float x, y;
    private Point verifyPoint, movePoint, startPoint;// 验证的点，拖动中的点，拖动的起点
    private boolean isMoving;//是否在移动中


    //定义一个内存中的图片，该图片将作为缓冲区
    private Bitmap cacheBitmap = null;

    //定义cacheBitmap上的Canvas对象
    private Canvas cacheCanvas = null;
    private Paint cachePaint = null;

    private OnVerifyListener onVerifyListener;

    public void setOnVerifyListener(OnVerifyListener onVerifyListener) {
        this.onVerifyListener = onVerifyListener;
    }

    public SlidingView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slidingview, this);
        lin = (FrameLayout) findViewById(R.id.lin);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;

        imageView = (ImageView) findViewById(R.id.image);
        image_cav = (ImageView) findViewById(R.id.image_cav);
        image_cav.setOnTouchListener(this);


        //设置画笔的颜色
        cachePaint = new Paint();
        //设置画笔风格
        cachePaint.setStyle(Paint.Style.FILL);
        //反锯齿
//      cachePaint.setAntiAlias(true);
        cachePaint.setColor(Color.parseColor("#7ac23c"));
        cachePaint.setStrokeJoin(Paint.Join.ROUND);
        cachePaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        int action = event.getAction();
        System.out.print("------ACTION_DOWNlastX");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                viewWidth = imageView.getWidth();
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                x = lastX;
                cacheBitmap = Bitmap.createBitmap(image_cav.getWidth(),
                        image_cav.getHeight(), Bitmap.Config.ARGB_8888);
                cacheCanvas = new Canvas();
                cacheCanvas.setBitmap(cacheBitmap);

                //获取起点位置
                l = imageView.getLeft();
                r = imageView.getRight();
                t = imageView.getTop();
                b = imageView.getBottom();

                poorWidth = (screenWidth - lin.getWidth()) / 2;

                verifyPoint = new Point(screenWidth, lastY);
                startPoint = new Point(poorWidth, lastY);

                //判断手指是否在滑块上，是则滑动
                if (event.getX() > 0 && event.getX() < imageView.getWidth()) {
                    movePoint = new Point(startPoint);
                    isMoving = true;
                }
                break;
            /**
             * layout(l,t,r,b)
             * l  Left position, relative to parent
             t  Top position, relative to parent
             r  Right position, relative to parent
             b  Bottom position, relative to parent
             * */
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = imageView.getLeft() + dx;
                int top = imageView.getTop();
                int right = imageView.getRight() + dx;
                int bottom = imageView.getBottom();

                //以控件最左边为原点
                if (left < 0) {
                    left = 0;
                    right = left + imageView.getWidth();
                }
                //不划出控件最右边
                if (right + poorWidth > screenWidth - poorWidth) {
                    right = screenWidth - poorWidth - poorWidth;
                    left = right - imageView.getWidth();
                }

                //更新当前点,
                if (isMoving && dx > 0) {
                    //设置滑块位置
                    imageView.layout(left, top, right, bottom);
                    float s = event.getRawX();
                    //设置画盘范围
                    if (s - x < lin.getWidth() - viewWidth) {
                        cacheCanvas.drawRect(0, 0, s - x, screenHeight, cachePaint);   //画线，x，y是上次的
                    } else {
                        cacheCanvas.drawRect(0, 0, lin.getWidth() - viewWidth, screenHeight, cachePaint);
                        //画线，x，y是上次的
                    }

                    image_cav.setImageBitmap(cacheBitmap);
                    invalidate();
                    lastX = (int) event.getRawX();
                    movePoint = new Point(lastX, movePoint.y);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isMoving) {
                    if (onVerifyListener != null) {
                        //验证范围0-30
                        if (Math.abs(movePoint.x - verifyPoint.x + poorWidth) < 100) {
                            onVerifyListener.success();
                        } else {
                            onVerifyListener.fail();
                            backZero();
                        }
                    }
                    isMoving = false;
                    movePoint = null;
                }
                break;
        }
        return false;
    }

    public void backZero() {
        //验证未成功返回原点
        imageView.layout(l, t, r, b);
        cacheBitmap = null;
        image_cav.setImageBitmap(cacheBitmap);
    }

    public interface OnVerifyListener {
        void success();

        void fail();
    }

}
