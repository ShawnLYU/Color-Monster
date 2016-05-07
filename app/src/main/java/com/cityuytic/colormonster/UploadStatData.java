package com.cityuytic.colormonster;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class UploadStatData extends AsyncTask<String, Void, Integer> {
    private Context mContext;

    public UploadStatData(Context context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String strUrl = "http://188.166.230.233/colormonster/upload.php";
        final String[] PARANAME={"name","mac","mode","time","trial_color","trial_word","trial_both","correct_color","correct_word","correct_both","age","gender"};
        int responseCode=404;
        try {
            // Build POST string
            StringBuilder buf = new StringBuilder();
            for (int i=0;i<PARANAME.length;i++)
                buf.append(PARANAME[i]+"=" + URLEncoder.encode(params[i], "UTF-8") + "&");
            byte[] POSTdata = buf.toString().getBytes("UTF-8");
            // Set up connection
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", String.format("%s/%s (Linux; Android %s; %s Build/%s)", "ColorMonster", "1.0", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
            conn.setDoOutput(true);    //如果要输出，则必须加上此句
            // POST
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(POSTdata);
            responseCode=conn.getResponseCode();
            // Get response (for debug only)
            InputStream is=new BufferedInputStream(conn.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            StringBuffer sb=new StringBuffer();
            String inputLine="";
            while ((inputLine=br.readLine())!=null)
                sb.append(inputLine);
            Log.i("UploadStatData",sb.toString());
            if (sb.toString().contains("refresh"))
                responseCode=-3;
        } catch (ConnectException e) {
            responseCode=-1;
        } catch (MalformedURLException e) {
            responseCode=-2;
        } catch (ProtocolException e) {
            responseCode=-2;
        } catch (UnsupportedEncodingException e) {
            responseCode=-2;
        } catch (IOException e) {
            responseCode=-2;
        }
        return responseCode;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        int responseCode=result.intValue();
        if (responseCode == 200)
            Toast.makeText(mContext, "統計數據上傳成功！", Toast.LENGTH_LONG).show();
        else {
            String errorMsg="統計數據上傳失敗。";
            if (responseCode==-1)
                errorMsg+="請檢查您的網絡連接。";
            else if (responseCode==-2)
                errorMsg+="程序內部錯誤。";
            else if (responseCode==302 || responseCode==-3)
                errorMsg+="您需要登錄您的網絡。";
            else
                errorMsg+="伺服器錯誤。";
            Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
        }
    }
}
