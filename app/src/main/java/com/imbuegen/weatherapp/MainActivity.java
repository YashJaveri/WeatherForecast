package com.imbuegen.weatherapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private final String APP_ID = "df78dcfa9580e72b15fdf62d406d34ec";
    private String cityName = "Mumbai,in";
    private String weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private HashMap<String, ArrayList<WeatherDataModel>> hashMapWeatherData;
    private ArrayList<WeatherDataModel> listOfWeatherObjs;
    private ArrayList<WeatherDataModel> tempWdm;
    private ArrayList<String> days_ExpList;
    final private List<String> mDAYS_OF_WEEK = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_main);
        init();
    }

    void init() {
        hashMapWeatherData = new HashMap<>();
        days_ExpList = new ArrayList<>();
        listOfWeatherObjs = new ArrayList<>();
        tempWdm = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyJSONTask().execute(weatherUrl);
    }

    class MyJSONTask extends AsyncTask<String, Integer, HashMap<String, ArrayList<WeatherDataModel>>> {

        @Override
        protected HashMap<String, ArrayList<WeatherDataModel>> doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();//Initializing the connection
                connection.setRequestMethod("GET");
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder sb;
                sb = new StringBuilder();

                String line;    //days_ExpListorarily to read lines
                while ((line = reader.readLine()) != null)
                    sb.append(line);                      //appending the JSON String to StringBuilder

                JSONObject mainJSONObj = new JSONObject(sb.toString());    //Convert JSON String to JSON object
                JSONArray JSONArray = mainJSONObj.getJSONArray("list");    //Get JSON array of weather of 5 days which has keyword "list"

                //1 --> get JSON data and convert to Java Object and built up an ArrayList of it:-
                for (int i = 0; i < JSONArray.length(); i++) {
                    JSONObject object = JSONArray.getJSONObject(i);
                    WeatherDataModel dataModel = new WeatherDataModel();

                    dataModel.set_temperature(object.getJSONObject("main").getDouble("temp"));   //set avg temperature
                    dataModel.set_ResourceOfImage(object.getJSONArray("weather").getJSONObject(0).getInt("id"));    //set imageIcon
                    dataModel.setDateTime(object.getString("dt_txt"));    //set date
                    listOfWeatherObjs.add(dataModel);
                }
                //2 --> Build list of next 5 days (Main (parent) ArrayList<String>), 3 --> Build HasMap of String & ArrayList<WeatherDataModel> for 3 hourly basis:-
                for (int i = 0; i < listOfWeatherObjs.size(); i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(listOfWeatherObjs.get(i).getDateTime());

                    if (days_ExpList.size() == 0 || !days_ExpList.contains(mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1))) {   //converting int to resp day and checking presence
                        days_ExpList.add(mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1));    //converting int to resp Day

                        tempWdm.clear();    //reinitialise temp list to make empty list for every new day
                        hashMapWeatherData.put(mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1),tempWdm);   //put the new day as the key with empty list
                    }
                    tempWdm = hashMapWeatherData.get(mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1));
                    tempWdm.add(listOfWeatherObjs.get(i));     //add data models to list
                    hashMapWeatherData.put(mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1), tempWdm);    //modify the list of hashMap of ongoing day-key
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
                return hashMapWeatherData;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<WeatherDataModel>> wdm) {
            if (wdm == null)
                Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_LONG).show();
            else {
              /*Log.d("WeatherApp", "Size = " + wdm.size());
                for (int i = 0; i < wdm.size(); i++)
                    Log.d("WeatherApp", Integer.toString(i) + ": Date Time: " + wdm.get(i).getDateTime() + " ,get_temperature: " + Integer.toString(wdm.get(i).get_temperature()) + " ,img source: " + Integer.toString(wdm.get(i).get_ResourceOfImage()));*/
                ExpandableListView days_ExpListView = findViewById(R.id.expListView_Days);
                MyExpandableAdapter days_ExpListViewAdapter = new MyExpandableAdapter(MainActivity.this, days_ExpList, hashMapWeatherData);
                days_ExpListView.setAdapter(days_ExpListViewAdapter);
                Log.d("WeatherApp", String.valueOf(hashMapWeatherData.size()));
                for (Map.Entry<String, ArrayList<WeatherDataModel>> entry : hashMapWeatherData.entrySet()){
                    for (int j=0; j<entry.getValue().size(); j++)
                        Log.d("WeatherApp", String.valueOf(j) + ":" + " Temp = " + entry.getValue().get(j).get_temperature() + " ,Day = " + entry.getKey() + " ,Time = " + entry.getValue().get(j).getDateTime() + "\n");
                }
            }
        }
    }
}