package com.imbuegen.weatherapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;

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

public class MainActivity extends AppCompatActivity {

    //Server ==>
    private String cityName = "Mumbai";
    private String mainWeatherText = "Sunny";
    private HttpURLConnection connection = null;
    final private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    final private String APP_ID = "df78dcfa9580e72b15fdf62d406d34ec";
    final private List<String> mDAYS_OF_WEEK = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
    private String weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;


    private BufferedReader reader = null;
    private HashMap<String, ArrayList<WeatherDataModel>> hashMapWeatherData;
    private ArrayList<WeatherDataModel> listOfWeatherObjs;
    private ArrayList<WeatherDataModel> tempWdm;
    private ArrayList<String> days_ExpList;

    //Views ==>
    private ExpandableListView days_ExpListView;
    private TextView cityNameView;
    private EditText editCityName;
    private Button searchBtn;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_main);
        init();
        new MyJSONTask().execute(weatherUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        editCityName.setVisibility(View.INVISIBLE);
    }

    /*  @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            cityName = getResources().getStringArray(R.array.CITIES)[position];    //get city name from array of string in resources
            String weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
            new MyJSONTask().execute(weatherUrl);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(getApplicationContext(), "Restart application", Toast.LENGTH_LONG).show();
        }

        void spinnerInit() {
            Spinner spinner = findViewById(R.id.spinner_cityChange);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.CITIES, R.layout.mspinner_item);
            spinnerAdapter.setDropDownViewResource(R.layout.mspinner_item_dropdown);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
        }*/

    //Initialisation:
    void init() {
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        hashMapWeatherData = new HashMap<>();
        days_ExpList = new ArrayList<>();
        listOfWeatherObjs = new ArrayList<>();
        tempWdm = new ArrayList<>();
        searchBtn = findViewById(R.id.btn_searchBtn);
        cityNameView = findViewById(R.id.txt_cityName);
        editCityName = findViewById(R.id.edit_txt_cityName);
    }

    public void searchOnClick(View view) {
        cityNameView.setVisibility(View.INVISIBLE);
        searchBtn.setVisibility(View.INVISIBLE);
        editCityName.setVisibility(View.VISIBLE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        editCityName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (editCityName.getText() != null && !cityName.equals(editCityName.getText().toString())) {
                    cityName = editCityName.getText().toString();
                    weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
                    new MyJSONTask().execute(weatherUrl);
                }
                cityNameView.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(editCityName.getWindowToken(), 0);
                cityNameView.setText(cityName);
                editCityName.setVisibility(View.INVISIBLE);
                return false;
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class MyJSONTask extends AsyncTask<String, Integer, HashMap<String, ArrayList<WeatherDataModel>>> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(MainActivity.this, "Fetching Data", "Please wait!", false);
        }

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

                String line;    //temp to read lines
                while ((line = reader.readLine()) != null)
                    sb.append(line);                      //appending the JSON String to StringBuilder

                JSONObject mainJSONObj = new JSONObject(sb.toString());    //Convert JSON String to JSON object
                JSONArray mJSONArray = mainJSONObj.getJSONArray("list");    //Get JSON array of weather of 5 days which has keyword "list"

                days_ExpList.clear();
                hashMapWeatherData.clear();
                listOfWeatherObjs.clear();
                //1 --> get JSON data and convert to Java Object and built up an ArrayList of it:-
                for (int i = 0; i < mJSONArray.length(); i++) {
                    JSONObject object = mJSONArray.getJSONObject(i);
                    WeatherDataModel dataModel = new WeatherDataModel();
                    //get weather text for main layout:
                    if (i == 1)
                        mainWeatherText = object.getJSONArray("weather").getJSONObject(0).getString("main");

                    dataModel.set_temperature(object.getJSONObject("main").getDouble("temp"));   //set avg temperature
                    dataModel.setDateTime(object.getString("dt_txt"));    //set date
                    dataModel.set_ResourceOfImage(object.getJSONArray("weather").getJSONObject(0).getInt("id"));    //set imageIcon
                    listOfWeatherObjs.add(dataModel);
                }
                //2 --> Build list of next 5 days (Main (parent) ArrayList<String>), 3 --> Build HasMap of String & ArrayList<WeatherDataModel> for 3 hourly basis:-
                for (int i = 0; i < listOfWeatherObjs.size(); i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(listOfWeatherObjs.get(i).getDateTime());
                    String s = mDAYS_OF_WEEK.get(cal.get(Calendar.DAY_OF_WEEK) - 1);

                    if (days_ExpList.size() == 0 || !days_ExpList.contains(s)) {  //check presence
                        tempWdm.add(listOfWeatherObjs.get(i));
                        hashMapWeatherData.put(s, tempWdm);    //add data model to list
                        tempWdm = new ArrayList<>();
                        days_ExpList.add(s);    //converting int to resp Day
                    } else
                        hashMapWeatherData.get(s).add(listOfWeatherObjs.get(i));    //modify the list of hashMap of ongoing day-key
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
            //stop loading
            if (pd.isShowing())
                pd.dismiss();

            if (wdm.size() == 0)
                Toast.makeText(MainActivity.this, "Unable to fetch data!", Toast.LENGTH_LONG).show();
            else {
                days_ExpListView = findViewById(R.id.expListView_Days);
                //To collapse all others except selected:-
                days_ExpListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousItem = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousItem)
                            days_ExpListView.collapseGroup(previousItem);
                        previousItem = groupPosition;
                    }
                });
                MyExpandableAdapter days_ExpListViewAdapter = new MyExpandableAdapter(MainActivity.this, days_ExpList, hashMapWeatherData);
                days_ExpListView.setAdapter(days_ExpListViewAdapter);
                //default expand group 1;
                days_ExpListView.expandGroup(1);
                //Change main part:
                changeMain();
            }
//                Log.d("WeatherApp", String.valueOf(hashMapWeatherData.size()));
//                for (Map.Entry<String, ArrayList<WeatherDataModel>> entry : hashMapWeatherData.entrySet()) {
//                    for (int j = 0; j < entry.getValue().size(); j++)
//                        Log.d("WeatherApp", String.valueOf(j) + ":" + " Temp = " + entry.getValue().get(j).get_temperature() + " ,Day = " + entry.getKey() + " ,Time = " + entry.getValue().get(j).getDateTime() + "\n");
//                }
        }

        void changeMain() {
            TextView mainTempView = findViewById(R.id.txt_avgTemperature);
            ImageView mainIcon = findViewById(R.id.img_weatherImage);
            TextView mainWeatherTextView = findViewById(R.id.txt_weatherText);

            String s = String.valueOf(listOfWeatherObjs.get(1).get_temperature()) + "°";
            mainTempView.setTypeface(Typer.set(MainActivity.this).getFont(Font.ROBOTO_THIN));
            mainTempView.setText(String.valueOf(s));
            mainIcon.setImageResource(listOfWeatherObjs.get(1).get_ResourceOfImage());
            mainWeatherTextView.setText(mainWeatherText);
        }
    }
}