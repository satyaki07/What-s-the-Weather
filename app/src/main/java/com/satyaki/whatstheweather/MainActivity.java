package com.satyaki.whatstheweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView cityTextView;
    TextView tempTextView;
    TextView conditionTextView;

    /*public boolean haveNetwokConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = (networkInfo != null) && (networkInfo.isConnectedOrConnecting());
        return isConnected;

    }
    */

    public void findWeather(View view) {


        Log.i("cityname", cityName.getText().toString());

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();

            task.execute("http://api.apixu.com/v1/current.json?key=274f5c1a31e14bfa92b54355180203&q=" + encodedCityName);

            Toast.makeText(getApplicationContext(), "NONONO", Toast.LENGTH_SHORT).show();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry,could not find weather!!", Toast.LENGTH_SHORT);

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;


            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sorry,could not find weather!!", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                String msg = "";


                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("location");
                JSONObject cityObj = new JSONObject(weatherInfo);
                String city = cityObj.getString("name");


                String sub = jsonObject.getString("current");

                JSONObject weatherCondition = new JSONObject(sub);

                String currentWeather = weatherCondition.getString("condition");

                JSONObject conditionObj = new JSONObject(currentWeather);

                String temperature = weatherCondition.getString("feelslike_c") + "°";
                String condition = conditionObj.getString("text");

                //System.out.println(city+" "+temperature+" "+condition);

                msg = city + "\n" + temperature + "\t" + condition;

                if (msg != "") {

                    cityTextView.setText(city);
                    tempTextView.setText(temperature);
                    conditionTextView.setText(condition);
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry,could not find weather!!", Toast.LENGTH_SHORT).show();
                }


                //Log.i("Weather Content",currentWEather);


                /*JSONObject sub = arr.getJSONObject(0);
                JSONArray condition = sub.getJSONArray("conditioin");

                for(int i=0;i<arr.length();i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("name",jsonPart.getString("name"));
                    //Log.i("temp_f",jsonPart.getString("temp_f"));
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sorry,could not find weather!!", Toast.LENGTH_SHORT).show();

            }

        }
    }
}

