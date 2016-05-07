package com.cityuytic.colormonster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class InitSettingActivity extends AppCompatActivity {

    private EditText txtName, txtAge;
    private ImageView selectedMale,selectedFemale,selectedUpload,btnFinish;
    private Button btnMale,btnFemale,btnUpload;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_setting);
        // Set controls
        txtName=(EditText)findViewById(R.id.initSetting_txt_name);
        txtAge=(EditText)findViewById(R.id.initSetting_txt_age);
        selectedMale=(ImageView)findViewById(R.id.initSetting_view_male);
        selectedFemale=(ImageView)findViewById(R.id.initSetting_view_female);
        selectedUpload=(ImageView)findViewById(R.id.initSetting_view_upload);
        btnMale=(Button)findViewById(R.id.initSetting_btn_male);
        btnFemale=(Button)findViewById(R.id.initSetting_btn_female);
        btnUpload=(Button)findViewById(R.id.initSetting_btn_upload);
        btnFinish=(ImageView)findViewById(R.id.initSetting_btn_finish);
        btnMale.setOnClickListener(mButtonClickListener);
        btnFemale.setOnClickListener(mButtonClickListener);
        btnUpload.setOnClickListener(mButtonClickListener);
        btnFinish.setOnClickListener(mButtonClickListener);
        btnFinish.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
                } else if (event.getAction()==MotionEvent.ACTION_UP) {
                    v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
                }
                return false;
            }
        });
    }

    private View.OnClickListener mButtonClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.initSetting_btn_male:
                    selectedMale.setVisibility(View.VISIBLE);
                    selectedFemale.setVisibility(View.GONE);
                    break;
                case R.id.initSetting_btn_female:
                    selectedMale.setVisibility(View.GONE);
                    selectedFemale.setVisibility(View.VISIBLE);
                    break;
                case R.id.initSetting_btn_upload:
                    if (selectedUpload.getVisibility()==View.VISIBLE)
                        selectedUpload.setVisibility(View.GONE);
                    else
                        selectedUpload.setVisibility(View.VISIBLE);
                    break;
                case R.id.initSetting_btn_finish:
                    if (txtName.getText()==null || txtName.getText().toString().equals(""))
                        Toast.makeText(InitSettingActivity.this, "請輸入您的名字。", Toast.LENGTH_LONG).show();
                    else if (txtAge.getText()==null || txtAge.getText().toString().equals(""))
                        Toast.makeText(InitSettingActivity.this, "請輸入您的年齡。", Toast.LENGTH_LONG).show();
                    else {
                        // Save to user preference
                        sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
                        editor=sharedPreferences.edit();
                        editor.putString("name", txtName.getText().toString());
                        editor.putString("gender", selectedMale.getVisibility() == View.VISIBLE ? "M" : "F");
                        editor.putInt("age", (int)Double.parseDouble(txtAge.getText().toString()));
                        editor.putBoolean("allowUpload",  selectedUpload.getVisibility()==View.VISIBLE?true:false);
                        editor.commit();
                        // Upload to server
                        // Goto Main Menu
                        startActivity(new Intent(InitSettingActivity.this,MainMenuActivity.class));
                        finish();
                    }
                    break;
            }
        }
    };
}
