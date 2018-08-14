package com.imbuegen.weatherapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private final String APP_ID = "df78dcfa9580e72b15fdf62d406d34ec";
    private String cityName = "Mumbai";
    private String weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        setContentView(R.layout.activity_main);

        new MyJSONTask().execute(weatherUrl);
    }

     class MyJSONTask extends AsyncTask<String, Integer, ArrayList<WeatherDataModel>> {
        @Override
        protected ArrayList<WeatherDataModel> doInBackground(String... strings) {
            ArrayList<WeatherDataModel> listOfWeather = new ArrayList<>();

            try {
                URL url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();//Initializing the connection
                connection.setRequestMethod("GET");
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb = null;
                sb = new StringBuilder();

                String line;    //Temporarily to read lines
                while ((line = reader.readLine()) != null)
                    sb.append(line);                      //appending the JSON String to StringBuilder

                JSONObject mainJSONObj = new JSONObject(sb.toString());    //Convert JSON String to JSON object
                JSONArray JSONArray = mainJSONObj.getJSONArray("list");    //Get JSON array of weather of 5 days which has keyword "list"

                for (int i = 0; i < JSONArray.length(); i++) {
                    JSONObject object = JSONArray.getJSONObject(i);

                    WeatherDataModel dataModel = new WeatherDataModel();

                    dataModel.set_temperature(object.getJSONObject("main").getDouble("temp"));   //set avg temperature
                    dataModel.set_ResourceOfImage(object.getJSONArray("weather").getJSONObject(0).getInt("id"));    //set imageIcon
                    dataModel.setDateTime(object.getString("dt_txt"));

                    listOfWeather.add(dataModel);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if ((connection != null))
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return listOfWeather;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherDataModel> wdm) {
            if (wdm == null)
                Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_LONG).show();
            else {
                Log.d("WeatherApp", "Size = " + wdm.size());
                for (int i = 0; i < wdm.size(); i++)
                    Log.d("WeatherApp", Integer.toString(i) + ": Date Time: " + wdm.get(i).getDateTime() + " ,temperature: " + Integer.toString(wdm.get(i).get_temperature()) + " ,img source: " + Integer.toString(wdm.get(i).get_ResourceOfImage()));
            }
        }
    }
}