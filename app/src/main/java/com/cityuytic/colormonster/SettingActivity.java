package com.cityuytic.colormonster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.SettingInjectorService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private String name="";
    private String gender="M";
    private int age=0;
    private boolean allowUpload=false;

    private EditText txtName, txtAge;
    private ImageView selectedMale,selectedFemale,selectedUpload,btnFinish;
    private Button btnMale,btnFemale,btnUpload,btnReset, btnAbout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // Restore preferences
        sharedPreferences = getSharedPreferences("com.cityuytic.colormonster", Activity.MODE_PRIVATE);
        if (sharedPreferences!=null) {
            name=sharedPreferences.getString("name","");
            gender=sharedPreferences.getString("gender", "M");
            age=sharedPreferences.getInt("age", 0);
            allowUpload=sharedPreferences.getBoolean("allowUpload", false);
        }
        // Set controls
        txtName=(EditText)findViewById(R.id.setting_txt_name);
        txtName.setText(name);
        txtAge=(EditText)findViewById(R.id.setting_txt_age);
        txtAge.setText(""+age);
        selectedMale=(ImageView)findViewById(R.id.setting_view_male);
        selectedFemale=(ImageView)findViewById(R.id.setting_view_female);
        if (gender.equals("M")) {
            selectedMale.setVisibility(View.VISIBLE);
            selectedFemale.setVisibility(View.GONE);
        } else {
            selectedMale.setVisibility(View.GONE);
            selectedFemale.setVisibility(View.VISIBLE);
        }
        selectedUpload=(ImageView)findViewById(R.id.setting_view_upload);
        if (allowUpload)
            selectedUpload.setVisibility(View.VISIBLE);
        else
            selectedUpload.setVisibility(View.GONE);
        btnMale=(Button)findViewById(R.id.setting_btn_male);
        btnFemale=(Button)findViewById(R.id.setting_btn_female);
        btnUpload=(Button)findViewById(R.id.setting_btn_upload);
        btnFinish=(ImageView)findViewById(R.id.setting_btn_finish);
        btnReset=(Button)findViewById(R.id.setting_btn_reset);
        btnAbout=(Button)findViewById(R.id.setting_btn_about_us);
        btnMale.setOnClickListener(mButtonClickListener);
        btnFemale.setOnClickListener(mButtonClickListener);
        btnUpload.setOnClickListener(mButtonClickListener);
        btnFinish.setOnClickListener(mButtonClickListener);
        btnReset.setOnClickListener(mButtonClickListener);
        btnAbout.setOnClickListener(mButtonClickListener);
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
                case R.id.setting_btn_male:
                    selectedMale.setVisibility(View.VISIBLE);
                    selectedFemale.setVisibility(View.GONE);
                    break;
                case R.id.setting_btn_female:
                    selectedMale.setVisibility(View.GONE);
                    selectedFemale.setVisibility(View.VISIBLE);
                    break;
                case R.id.setting_btn_upload:
                    if (selectedUpload.getVisibility()==View.VISIBLE)
                        selectedUpload.setVisibility(View.GONE);
                    else
                        selectedUpload.setVisibility(View.VISIBLE);
                    break;
                case R.id.setting_btn_finish:
                    if (txtName.getText()==null || txtName.getText().toString().equals("")) {
                        Toast.makeText(SettingActivity.this, "請輸入您的名字。", Toast.LENGTH_LONG).show();
                        txtName.requestFocus();
                    }
                    else if (txtAge.getText()==null || txtAge.getText().toString().equals("")) {
                        Toast.makeText(SettingActivity.this, "請輸入您的年齡。", Toast.LENGTH_LONG).show();
                        txtAge.requestFocus();
                    }
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
                        finish();
                    }
                    break;
                case R.id.setting_btn_reset:
                    /*
                    // Generate custom confirmation dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this,R.style.CustomDialog); // Create new alert dialog
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_resetdata, null); // Inflate the custom layout in to a View object
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final AlertDialog resetDataDialog = dialog.create(); // Create the dialog in a final context
                    // Find the Button object within the inflated view
                    ImageButton btnDialogResetdataY=(ImageButton)dialogView.findViewById(R.id.dialog_resetdata_yes);
                    ImageButton btnDialogResetdataN=(ImageButton)dialogView.findViewById(R.id.dialog_resetdata_no);
                    // Set the onClickListener
                    btnDialogResetdataY.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // Restart activity
                            editor=sharedPreferences.edit();
                            editor.clear().commit();
                            txtName.setText("");
                            txtAge.setText("");
                            selectedMale.setVisibility(View.VISIBLE);
                            selectedFemale.setVisibility(View.GONE);
                            selectedUpload.setVisibility(View.GONE);
                            Toast.makeText(SettingActivity.this, "數據重置成功！", Toast.LENGTH_LONG).show();
                            resetDataDialog.dismiss();
                        }
                    });
                    btnDialogResetdataN.setOnClickListener(new View.OnClickListener() { // Finish activity
                        @Override
                        public void onClick(View v) {
                            resetDataDialog.dismiss();
                        }
                    });
                    resetDataDialog.show(); // Show the dialog
                    */
                    final Dialog resetDataDialog = new Dialog(SettingActivity.this);
                    resetDataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    resetDataDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    resetDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    resetDataDialog.setContentView(R.layout.dialog_resetdata);
                    resetDataDialog.findViewById(R.id.dialog_resetdata_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor = sharedPreferences.edit();
                            editor.clear().commit();
                            txtName.setText("");
                            txtAge.setText("");
                            selectedMale.setVisibility(View.VISIBLE);
                            selectedFemale.setVisibility(View.GONE);
                            selectedUpload.setVisibility(View.GONE);
                            Toast.makeText(SettingActivity.this, "數據重置成功！", Toast.LENGTH_LONG).show();
                            resetDataDialog.dismiss();
                        }
                    });
                    resetDataDialog.findViewById(R.id.dialog_resetdata_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resetDataDialog.dismiss();
                        }
                    });
                    resetDataDialog.findViewById(R.id.dialog_resetdata_yes).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
                            }
                            return false;
                        }
                    });
                    resetDataDialog.findViewById(R.id.dialog_resetdata_no).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
                            }
                            return false;
                        }
                    });
                    resetDataDialog.show();
                    break;
                case R.id.setting_btn_about_us:
                    startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                    break;
            }
        }
    };
}
