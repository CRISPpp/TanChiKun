package cn.cirsp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import cn.cirsp.entity.Block;

public class MainActivity extends AppCompatActivity {

    private ImageView[] imageViews = null;
    private TextView score;
    private TextView maxScore;
    private SharedPreferences preferences = null;
    private Block[] blocks = new Block[16 * 16];
    private Deque<Block> kunkun = new ArrayDeque<>();
    private MediaPlayer mediaPlayer;
    private boolean kk = false;

    //flag 1表示左，2表示上，3表示下，4表示右
    private int flag = 1;

    int curScore = 0;

    //消息处理者，负责渲染
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                //1表示坤坤生成的位置
                case 1:
                    imageViews[msg.arg1].setImageResource(R.drawable.ji);
                    break;
                //2表示篮球生成的位置
                case 2:
                    imageViews[msg.arg1].setBackgroundResource(R.drawable.lq);
                    break;
                //3表示图形渲染
                case 3:
                    for (int i = 0; i < blocks.length; i ++) {
                        if (blocks[i].isBasketball()) {
                            if (!kk) {
                                imageViews[i].setBackgroundResource(R.drawable.lq);
                            }
                            else {
                                imageViews[i].setBackgroundResource(R.drawable.kk9);
                            }
                            if (!blocks[i].isBody() && !blocks[i].isHead()) {
                                imageViews[i].setImageResource(R.mipmap.ic_launcher_round);
                            }
                        }
                        if (blocks[i].isBody()) {
                            imageViews[i].setBackgroundResource(R.drawable.white);
                            imageViews[i].setImageResource(R.drawable.bdk);
                        }else if (blocks[i].isHead()) {
                            imageViews[i].setBackgroundResource(R.drawable.white);
                            imageViews[i].setImageResource(R.drawable.ji);
                        }else if (!blocks[i].isBasketball()){
                            imageViews[i].setImageResource(R.mipmap.ic_launcher_round);
                        }
                        score.setText("Score: " + String.valueOf(curScore));
                    }
                    break;
            }
        }
    };

    private long time = 500;

    //开启新线程跑动
    class MyThread extends Thread {
        @SuppressLint("ResourceAsColor")
        @Override
        public void run() {
            //坤坤随机生成
            Message message = new Message();
            message.what = 1;
            int kun = new Random().nextInt(imageViews.length);
            blocks[kun].setHead(true);
            message.arg1 = kun;
            kunkun.addFirst(blocks[kun]);

            //随机生成篮球
            Message message1 = new Message();
            message1.what = 2;
            int basketball = new Random().nextInt(imageViews.length);
            message1.arg1 = basketball;
            blocks[basketball].setBasketball(true);

            while(true) {
                //更新坤坤以及篮球的状态，如果出现碰墙或者碰到坤身则结束
                boolean mark = false;
                if (kunkun.size() > 1) {
                    kunkun.getFirst().setBody(false);
                    kunkun.removeFirst();
                    if (kunkun.size() > 1) {
                        kunkun.getFirst().setBody(true);
                    }
                    kunkun.getLast().setHead(false);
                    kunkun.getLast().setBody(true);
                    int curId = kunkun.getLast().getId();
                    if (flag == 1) {
                        if (curId % 16 == 0) {
                            mark = true;
                        }else {
                            curId -= 1;
                        }
                    }else if (flag == 2) {
                        if (curId < 16) {
                            mark = true;
                        } else {
                            curId -= 16;
                        }
                    }else if (flag == 3) {
                        if (curId + 16 >= 16 * 16){
                            mark = true;
                        }else {
                            curId += 16;
                        }
                    }else if (flag == 4) {
                        if ((curId + 1) % 16 == 0) {
                            mark = true;
                        }else {
                            curId += 1;
                        }
                    }
                    blocks[curId].setHead(true);
                    kunkun.addLast(blocks[curId]);
                } else {
                    int curId = kunkun.getLast().getId();
                    if (flag == 1) {
                        if (curId % 16 == 0) {
                            mark = true;
                        }else {
                            curId -= 1;
                        }
                    }else if (flag == 2) {
                        if (curId < 16) {
                            mark = true;
                        } else {
                            curId -= 16;
                        }
                    }else if (flag == 3) {
                        if (curId + 16 >= 16 * 16){
                            mark = true;
                        }else {
                            curId += 16;
                        }
                    }else if (flag == 4) {
                        if ((curId + 1) % 16 == 0) {
                            mark = true;
                        }else {
                            curId += 1;
                        }
                    }
                    kunkun.getLast().setHead(false);
                    kunkun.removeLast();
                    blocks[curId].setHead(true);
                    kunkun.addLast(blocks[curId]);
                }

                //判断是否碰撞身体
                if (kunkun.getLast().isBody() && kunkun.getLast().isHead()) {
                    mark = true;
                }
                //判断是否有篮球
                if (kunkun.getLast().isBasketball()) {
                    mediaPlayer.start();
                    curScore += 1;
                    if (kk) {
                        curScore += 1;
                        kk = false;
                    }
                    if (new Random().nextInt(10) == 5) {
                        kk = true;
                    }
                    //更改篮球
                    kunkun.getLast().setBasketball(false);
                    int newBasketball = new Random().nextInt(16 * 16);
                    Message message2 = new Message();
                    message2.what = 2;
                    message2.arg1 = newBasketball;
                    blocks[newBasketball].setBasketball(true);
                    //添加身体
                    kunkun.addFirst(kunkun.getFirst());
                }


                if (mark) {
                    //对比是否新记录
                    Bundle bundle = new Bundle();
                    bundle.putInt("score", curScore);
                    if (curScore > preferences.getInt("maxScore", 0)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("maxScore", curScore);
                        editor.apply();
                        bundle.putString("res", "win");
                    }else {
                        bundle.putString("res", "loss");
                    }
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, EndActivity.class);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }

                Message message3 = new Message();
                message3.what = 3;
                handler.sendMessage(message3);

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //对组件进行赋值
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.jimp3);
        score = findViewById(R.id.score);
        maxScore = findViewById(R.id.maxScore);
        imageViews = new ImageView[]{findViewById(R.id.image0), findViewById(R.id.image1), findViewById(R.id.image2), findViewById(R.id.image3), findViewById(R.id.image4), findViewById(R.id.image5), findViewById(R.id.image6), findViewById(R.id.image7), findViewById(R.id.image8), findViewById(R.id.image9), findViewById(R.id.image10), findViewById(R.id.image11), findViewById(R.id.image12), findViewById(R.id.image13), findViewById(R.id.image14), findViewById(R.id.image15), findViewById(R.id.image16), findViewById(R.id.image17), findViewById(R.id.image18), findViewById(R.id.image19), findViewById(R.id.image20), findViewById(R.id.image21), findViewById(R.id.image22), findViewById(R.id.image23), findViewById(R.id.image24), findViewById(R.id.image25), findViewById(R.id.image26), findViewById(R.id.image27), findViewById(R.id.image28), findViewById(R.id.image29), findViewById(R.id.image30), findViewById(R.id.image31), findViewById(R.id.image32), findViewById(R.id.image33), findViewById(R.id.image34), findViewById(R.id.image35), findViewById(R.id.image36), findViewById(R.id.image37), findViewById(R.id.image38), findViewById(R.id.image39), findViewById(R.id.image40), findViewById(R.id.image41), findViewById(R.id.image42), findViewById(R.id.image43), findViewById(R.id.image44), findViewById(R.id.image45), findViewById(R.id.image46), findViewById(R.id.image47), findViewById(R.id.image48), findViewById(R.id.image49), findViewById(R.id.image50), findViewById(R.id.image51), findViewById(R.id.image52), findViewById(R.id.image53), findViewById(R.id.image54), findViewById(R.id.image55), findViewById(R.id.image56), findViewById(R.id.image57), findViewById(R.id.image58), findViewById(R.id.image59), findViewById(R.id.image60), findViewById(R.id.image61), findViewById(R.id.image62), findViewById(R.id.image63), findViewById(R.id.image64), findViewById(R.id.image65), findViewById(R.id.image66), findViewById(R.id.image67), findViewById(R.id.image68), findViewById(R.id.image69), findViewById(R.id.image70), findViewById(R.id.image71), findViewById(R.id.image72), findViewById(R.id.image73), findViewById(R.id.image74), findViewById(R.id.image75), findViewById(R.id.image76), findViewById(R.id.image77), findViewById(R.id.image78), findViewById(R.id.image79), findViewById(R.id.image80), findViewById(R.id.image81), findViewById(R.id.image82), findViewById(R.id.image83), findViewById(R.id.image84), findViewById(R.id.image85), findViewById(R.id.image86), findViewById(R.id.image87), findViewById(R.id.image88), findViewById(R.id.image89), findViewById(R.id.image90), findViewById(R.id.image91), findViewById(R.id.image92), findViewById(R.id.image93), findViewById(R.id.image94), findViewById(R.id.image95), findViewById(R.id.image96), findViewById(R.id.image97), findViewById(R.id.image98), findViewById(R.id.image99), findViewById(R.id.image100), findViewById(R.id.image101), findViewById(R.id.image102), findViewById(R.id.image103), findViewById(R.id.image104), findViewById(R.id.image105), findViewById(R.id.image106), findViewById(R.id.image107), findViewById(R.id.image108), findViewById(R.id.image109), findViewById(R.id.image110), findViewById(R.id.image111), findViewById(R.id.image112), findViewById(R.id.image113), findViewById(R.id.image114), findViewById(R.id.image115), findViewById(R.id.image116), findViewById(R.id.image117), findViewById(R.id.image118), findViewById(R.id.image119), findViewById(R.id.image120), findViewById(R.id.image121), findViewById(R.id.image122), findViewById(R.id.image123), findViewById(R.id.image124), findViewById(R.id.image125), findViewById(R.id.image126), findViewById(R.id.image127), findViewById(R.id.image128), findViewById(R.id.image129), findViewById(R.id.image130), findViewById(R.id.image131), findViewById(R.id.image132), findViewById(R.id.image133), findViewById(R.id.image134), findViewById(R.id.image135), findViewById(R.id.image136), findViewById(R.id.image137), findViewById(R.id.image138), findViewById(R.id.image139), findViewById(R.id.image140), findViewById(R.id.image141), findViewById(R.id.image142), findViewById(R.id.image143), findViewById(R.id.image144), findViewById(R.id.image145), findViewById(R.id.image146), findViewById(R.id.image147), findViewById(R.id.image148), findViewById(R.id.image149), findViewById(R.id.image150), findViewById(R.id.image151), findViewById(R.id.image152), findViewById(R.id.image153), findViewById(R.id.image154), findViewById(R.id.image155), findViewById(R.id.image156), findViewById(R.id.image157), findViewById(R.id.image158), findViewById(R.id.image159), findViewById(R.id.image160), findViewById(R.id.image161), findViewById(R.id.image162), findViewById(R.id.image163), findViewById(R.id.image164), findViewById(R.id.image165), findViewById(R.id.image166), findViewById(R.id.image167), findViewById(R.id.image168), findViewById(R.id.image169), findViewById(R.id.image170), findViewById(R.id.image171), findViewById(R.id.image172), findViewById(R.id.image173), findViewById(R.id.image174), findViewById(R.id.image175), findViewById(R.id.image176), findViewById(R.id.image177), findViewById(R.id.image178), findViewById(R.id.image179), findViewById(R.id.image180), findViewById(R.id.image181), findViewById(R.id.image182), findViewById(R.id.image183), findViewById(R.id.image184), findViewById(R.id.image185), findViewById(R.id.image186), findViewById(R.id.image187), findViewById(R.id.image188), findViewById(R.id.image189), findViewById(R.id.image190), findViewById(R.id.image191), findViewById(R.id.image192), findViewById(R.id.image193), findViewById(R.id.image194), findViewById(R.id.image195), findViewById(R.id.image196), findViewById(R.id.image197), findViewById(R.id.image198), findViewById(R.id.image199), findViewById(R.id.image200), findViewById(R.id.image201), findViewById(R.id.image202), findViewById(R.id.image203), findViewById(R.id.image204), findViewById(R.id.image205), findViewById(R.id.image206), findViewById(R.id.image207), findViewById(R.id.image208), findViewById(R.id.image209), findViewById(R.id.image210), findViewById(R.id.image211), findViewById(R.id.image212), findViewById(R.id.image213), findViewById(R.id.image214), findViewById(R.id.image215), findViewById(R.id.image216), findViewById(R.id.image217), findViewById(R.id.image218), findViewById(R.id.image219), findViewById(R.id.image220), findViewById(R.id.image221), findViewById(R.id.image222), findViewById(R.id.image223), findViewById(R.id.image224), findViewById(R.id.image225), findViewById(R.id.image226), findViewById(R.id.image227), findViewById(R.id.image228), findViewById(R.id.image229), findViewById(R.id.image230), findViewById(R.id.image231), findViewById(R.id.image232), findViewById(R.id.image233), findViewById(R.id.image234), findViewById(R.id.image235), findViewById(R.id.image236), findViewById(R.id.image237), findViewById(R.id.image238), findViewById(R.id.image239), findViewById(R.id.image240), findViewById(R.id.image241), findViewById(R.id.image242), findViewById(R.id.image243), findViewById(R.id.image244), findViewById(R.id.image245), findViewById(R.id.image246), findViewById(R.id.image247), findViewById(R.id.image248), findViewById(R.id.image249), findViewById(R.id.image250), findViewById(R.id.image251), findViewById(R.id.image252), findViewById(R.id.image253), findViewById(R.id.image254), findViewById(R.id.image255)};

        //按钮功能初始化
        findViewById(R.id.left).setOnClickListener(v -> {
            flag = 1;
        });
        findViewById(R.id.up).setOnClickListener(v -> {
            flag = 2;
        });
        findViewById(R.id.down).setOnClickListener(v -> {
            flag = 3;
        });
        findViewById(R.id.right).setOnClickListener(v -> {
            flag = 4;
        });

        //对数组进行初始化
        for (int i = 0; i < 16 * 16; i ++) {
            blocks[i] = new Block();
            blocks[i].setId(i);
        }

        preferences = getSharedPreferences("ikun", Context.MODE_PRIVATE);
        //获取最大得分
        maxScore.setText("MaxScore: " + String.valueOf(preferences.getInt("maxScore", 0)));

        findViewById(R.id.slow).setOnClickListener(v -> {
            time = 1000;
        });

        findViewById(R.id.normal).setOnClickListener(v -> {
            time = 500;
        });

        findViewById(R.id.fast).setOnClickListener(v -> {
            time = 250;
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Thread myThread = new MyThread();
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}