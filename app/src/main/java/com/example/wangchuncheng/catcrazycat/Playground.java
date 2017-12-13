package com.example.wangchuncheng.catcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by WangChunCheng on 2017/12/13.
 */

public class Playground extends SurfaceView implements View.OnTouchListener {

    private static int WIDTH = 40;
    private static int HEIGHT = 40;
    private static final int COL = 10;
    private static final int ROW = 10;
    private static final int BLOCKS = 10;   //路障
    Dot matrix[][];
    private Dot cat;


    public Playground(Context context) {
        super(context);
        getHolder().addCallback(mCallback);
        matrix = new Dot[ROW][COL];
        setOnTouchListener(this);
        initGame();
    }

    /**
     * draw game map
     */
    private void redraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        for (int i = 0; i < ROW; i++) {
            int offset = 0;
            if (i % 2 != 0) {
                offset = WIDTH / 2;
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);
                switch (one.getStatus()) {
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    default:
                        break;
                }
                //绘制圆圈
                canvas.drawOval(new RectF((one.getX()) * WIDTH + offset, (one.getY() * HEIGHT), (one.getX() + 1) * WIDTH + offset, (one.getY() + 1) * HEIGHT), paint);

            }
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * cat move to dot
     * @param dot
     */
    private void moveTo(Dot dot) {
        dot.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(dot.getX(), dot.getY());
    }

    /**
     * 移动的策略
     */
    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }
        Vector<Dot> available = new Vector<Dot>();
        Vector<Dot> positive = new Vector<Dot>();                   //available and edge Accessible
        HashMap<Dot, Integer> availableLength = new HashMap<>();    //available dots and its direction
        for (int i = 1; i < 7; i++) {
            Dot n = getNeighbor(cat, i);
            if (n.getStatus() == Dot.STATUS_OFF) {
                available.add(n);
                availableLength.put(n, i);
                if (getDistance(n, i) > 0) {
                    positive.add(n);
                }
            }
        }
        if (available.size() == 0) {
            win();
        } else if (available.size() == 1) {
            moveTo(available.get(0));
        } else {
            Dot best = null;
            if (positive.size() != 0) {    //存在可以到达屏幕边缘的走向
                System.out.println("向前进------");
                int min = 999;
                for (int i = 0; i < positive.size(); i++) {   //找到到达边届距离最小的方向
                    int dis1 = getDistance(positive.get(i), availableLength.get(positive.get(i)));
                    if (dis1 < min) {
                        min = dis1;
                        best = positive.get(i);
                    }
                }
            } else {                     //所有方向都存在路障
                System.out.println("躲路障------");
                int max = 0;
                for (int i=0;i<available.size();i++){
                    int dis2 = getDistance(available.get(i),availableLength.get(available.get(i)));
                    if (dis2<=max){
                        max = dis2;
                        best = available.get(i);
                    }
                }
            }
            moveTo(best);
        }
    }

    private void lose() {
        Toast.makeText(getContext(), "LOSE!", Toast.LENGTH_SHORT).show();
    }

    private void win() {
        Toast.makeText(getContext(), "WIN!", Toast.LENGTH_SHORT).show();
    }

    private void initGame() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);
                matrix[i][j].setStatus(Dot.STATUS_OFF);
            }
        }
        cat = new Dot(4, 5);
        getDot(4, 5).setStatus(Dot.STATUS_IN);

        for (int i = 0; i < BLOCKS; ) {
            int x = (int) ((Math.random() * 1000) % COL);
            int y = (int) ((Math.random() * 1000) % ROW);
            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x, y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    /**
     * matrix上的点阵和图上点阵的x,y相反
     * @param x
     * @param y
     * @return
     */
    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    private boolean isAtEdge(Dot dot) {
        if (dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL || dot.getY() + 1 == ROW) {
            return true;
        }
        return false;
    }

    /**
     * 获取dot 在direction方向上的相邻的dot
     * @param dot
     * @param direction
     * @return
     */
    private Dot getNeighbor(Dot dot, int direction) {
        switch (direction) {
            case 1:
                return getDot(dot.getX() - 1, dot.getY());
            case 2:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX() - 1, dot.getY() - 1);
                } else {
                    return getDot(dot.getX(), dot.getY() - 1);
                }
            case 3:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX(), dot.getY() - 1);
                } else {
                    return getDot(dot.getX() + 1, dot.getY() - 1);
                }
            case 4:
                return getDot(dot.getX() + 1, dot.getY());
            case 5:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX(), dot.getY() + 1);
                } else {
                    return getDot(dot.getX() + 1, dot.getY() + 1);
                }
            case 6:
                if (dot.getY() % 2 == 0) {
                    return getDot(dot.getX() - 1, dot.getY() + 1);
                } else {
                    return getDot(dot.getX(), dot.getY() + 1);
                }
        }
        return null;
    }

    /**
     * 获取dot 在某个方向上的距离
     * 如果该方向可以到达边界，返回距离的正值
     * 如果该方向上存在路障，返回距离的负数
     * @param dot
     * @param diretion
     * @return
     */
    private int getDistance(Dot dot, int diretion) {
        int distance = 0;
        if (isAtEdge(dot))
            return 1;
        Dot ori = dot, next;
        while (true) {
            next = getNeighbor(ori, diretion);
            if (next.getStatus() == Dot.STATUS_ON) {
                return distance * -1;
            }
            if (isAtEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width / (COL + 1);
            HEIGHT = WIDTH;
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x, y;
            y = (int) (event.getY() / WIDTH);

            /*确定点击目标*/
            if (y % 2 == 0) {
                x = (int) (event.getX() / WIDTH);
            } else {
                x = (int) ((event.getX() - WIDTH / 2) / WIDTH);
            }

            if (x + 1 > COL || y + 1 > ROW) {
                initGame();                             /*边界外点击,restart game when somewhere clicked outside the map */
            } else if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x, y).setStatus(Dot.STATUS_ON);   //改变状态
                move();
            }
            redraw();                                   //重绘
        }
        return true;
    }
}
