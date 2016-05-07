package com.cityuytic.colormonster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.HashMap;

public class SingleTutorialActivity extends AppCompatActivity {

    private int clickCnt = 0 ;
    private ImageView view_button ;
    private TextView st_text;
    private HashMap<Integer, String> st_alltext= new HashMap<>();

    private ImageView view_score_board ;
    private TextView view_time_remaining;
    private TextView view_task_type;
    private TextView view_task_content;

    private TableLayout st_canvas;

    private ImageView view_xiaozhi;
    private ImageView view_monster;
    private ImageView view_route;

    private TextView view_total_score;
    private TextView view_single_mode_score;
    private TextView st_total_score;
    private ImageView st_star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_tutorial);

        register();

        RelativeLayout layout=(RelativeLayout)findViewById(R.id.st_myRelativeLayout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // Restart activity

                clickCnt += 1;
                if (clickCnt > 5) {
                    startActivity(new Intent(SingleTutorialActivity.this, SingleModeActivity.class));
                    finish();
                } else {
                    setInvisibleView(clickCnt);
                    setVisibleView(clickCnt);
                    showText(clickCnt);

                }
            }



        });
    }

    public void showText (int clickCnt)
    {
        st_text.setText(st_alltext.get(clickCnt));
    }

    public void setInvisibleView (int clickCnt)
    {
        switch (clickCnt){
            case 1:
                view_button.setVisibility(View.INVISIBLE);
                break;
            case 2:
                st_canvas.setVisibility(View.INVISIBLE);
                view_score_board.setVisibility(View.INVISIBLE);
                view_time_remaining.setVisibility(View.INVISIBLE);
                view_task_type.setVisibility(View.INVISIBLE);
                view_task_content.setVisibility(View.INVISIBLE);
                break;
            case 3:
                view_route.setVisibility(View.INVISIBLE);
                view_xiaozhi.setVisibility(View.INVISIBLE);
                view_monster.setVisibility(View.INVISIBLE);
                break;
            case 4:
                view_total_score.setVisibility(View.INVISIBLE);
                view_single_mode_score.setVisibility(View.INVISIBLE);
                st_star.setVisibility(View.INVISIBLE);
                st_total_score.setVisibility(View.INVISIBLE);
                break;
            case 5:
                view_button.setVisibility(View.INVISIBLE);
            default:
                break;

        }
    }

    public void setVisibleView (int clickCnt)
    {
        switch (clickCnt){
            /*case 1:
                view_score_board.setVisibility(View.VISIBLE);
                view_time_remaining.setVisibility(View.VISIBLE);
                view_task_type.setVisibility(View.VISIBLE);
                view_task_content.setVisibility(View.VISIBLE);
                break;*/
            case 1:
                view_score_board.setVisibility(View.VISIBLE);
                view_time_remaining.setVisibility(View.VISIBLE);
                view_task_type.setVisibility(View.VISIBLE);
                view_task_content.setVisibility(View.VISIBLE);
                st_canvas.setVisibility(View.VISIBLE);
                view_xiaozhi.setVisibility(View.VISIBLE);
                break;
            case 2:
                view_route.setVisibility(View.VISIBLE);
                view_monster.setVisibility(View.VISIBLE);
                break;
            case 3:
                view_total_score.setVisibility(View.VISIBLE);
                view_single_mode_score.setVisibility(View.VISIBLE);
                st_star.setVisibility(View.VISIBLE);
                st_total_score.setVisibility(View.VISIBLE);

                break;
            case 4:
                view_button.setImageResource(R.drawable.btn_pause);
                view_button.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }

    public void register()
    {
        view_button = (ImageView) findViewById(R.id.st_single_mode_btn_start);
        st_text = (TextView) findViewById(R.id.st_text);

        st_alltext.put(1, "遊戲開始后，你要在這片區域里根據右邊的任務提示來找到相應的卡片，拍到正確卡片左側的小智就會向前跑");
        st_alltext.put(2, "遊戲過程中，如果小智率先到達終點或小智被怪物追上，則遊戲結束");
        st_alltext.put(3, "右上方顯示的分別是小朋友的總積分和本輪積分");
        st_alltext.put(4, "遊戲過程中小朋友想暫停的話可以點擊這個按鈕哦");
        st_alltext.put(5, "好啦，那就讓我們一起開始遊戲吧！");


        view_score_board = (ImageView)findViewById(R.id.st_imageView2);
        view_time_remaining = (TextView)findViewById(R.id.st_single_mode_time_remaining);
        view_task_type = (TextView)findViewById(R.id.st_single_mode_task_type);
        view_task_content = (TextView)findViewById(R.id.st_single_mode_task_content);

        st_canvas = (TableLayout)findViewById(R.id.st_canvas);

        view_route = (ImageView)findViewById(R.id.st_imageView4);
        view_xiaozhi = (ImageView) findViewById(R.id.st_single_mode_xiaozhi);
        view_monster = (ImageView)findViewById(R.id.st_single_mode_monster);

        view_total_score=(TextView)findViewById(R.id.st_single_mode_total_score);
        view_single_mode_score = (TextView)findViewById(R.id.st_single_mode_score);
        st_total_score = (TextView) findViewById(R.id.st_totalscore);
        st_star = (ImageView)findViewById( R.id.st_imageView3);
    }
}