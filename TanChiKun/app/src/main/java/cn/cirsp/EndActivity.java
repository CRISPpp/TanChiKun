package cn.cirsp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        mediaPlayer = MediaPlayer.create(EndActivity.this, R.raw.kgmp3);
        mediaPlayer1 = MediaPlayer.create(EndActivity.this, R.raw.ngmmp3);
        TextView textView = findViewById(R.id.end_score);
        ImageView img_gif = findViewById (R.id.img_dlq);
        Bundle bundle = getIntent().getExtras();
        textView.setText("最终得分: " + String.valueOf(bundle.getInt("score")));
        //如果系统版本为Android9.0以上,则利用新增的AnimatedImageDrawable显示GIF动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                ImageDecoder.Source source;
                //利用Android9.0新增的ImageDecoder读取gif动画
                if (bundle.getString("res").equals("win")) {
                    source = ImageDecoder.createSource(getResources(), R.drawable.dlq);
                    mediaPlayer.start();
                }
                else {
                    source = ImageDecoder.createSource(getResources(), R.drawable.ngm);
                    mediaPlayer1.start();
                }
                //从数据源中解码得到gif图形数据
                @SuppressLint("WrongThread") Drawable drawable = ImageDecoder.decodeDrawable (source);
                //设置图像视图的图形为gif图片
                img_gif.setImageDrawable (drawable);
                //如果是动画图形，则开始播放动画
                if (drawable instanceof Animatable) {
                    Animatable animatable = (Animatable) img_gif.getDrawable ();
                    animatable.start ();
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        findViewById(R.id.btn_restart).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(EndActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}