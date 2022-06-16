package com.example.animtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainActivity extends Activity {
    /*
     * 这个类用来当測试的物件，会沿着方形路线持续移动
     */
    class GameObject {
        private float x;
        private float y;
        private Bitmap img;
        private Paint paint;

        public GameObject() {
            this.img = BitmapFactory.decodeResource(getResources(), R.drawable.mtk);
            this.x = 100;
            this.y = 100;
            this.paint = new Paint();
        }

        // 在SurfaceView加锁同步后传给自己的Canvas上绘制自己
        public void drawSelf(Canvas canvas, int opacity) {
            paint.setAlpha(opacity);
            canvas.drawBitmap(img, x, y, paint);
        }

        // 获取物件下一次要绘制的位置(这里是沿着一个边长为400的正方形不断运动的)
        int step = 5;

        public void getNextPos() {
            if (y == 100 && x != 500)
                x += step;
            else if (x == 500 && y != 500)
                y += step;
            else if (y == 500 && x != 100)
                x -= step;
            else if (x == 100 && y != 100)
                y -= step;
        }
    }

    /*
     * 这个类就是加工了SurfaceView之后的类，全部要运动的物件都终于放在这里进行绘制
     */
    class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
        private Thread thread; // SurfaceView通常须要自己单独的线程来播放动画
        private Canvas canvas;
        private SurfaceHolder surfaceHolder;

        private GameObject obj;

        public MySurfaceView(Context c) {
            super(c);
            this.surfaceHolder = this.getHolder();
            this.surfaceHolder.addCallback(this);
            this.obj = new GameObject();
        }

        @Override
        public void run() {
            int opacity = 0;
            while (true) {
                if (opacity >= 255) {
                    opacity = 0;
                }
                opacity++;
                obj.getNextPos();
                canvas = this.surfaceHolder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
                canvas.drawColor(Color.BLACK);
                obj.drawSelf(canvas, opacity); // 把SurfaceView的画布传给物件。物件会用这个画布将自己绘制到上面的某个位置
                this.surfaceHolder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
                try {
                    Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            Toast.makeText(getApplicationContext(), "SurfaceView已经销毁", Toast.LENGTH_LONG).show();
        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0) {
            Toast.makeText(getApplicationContext(), "SurfaceView已经创建", Toast.LENGTH_LONG).show();
            this.thread = new Thread(this);
            this.thread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            // 这里是SurfaceView发生变化的时候触发的部分
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MySurfaceView(getApplicationContext())); // 别忘了開始的时候加载我们加工好的的SurfaceView
    }
}