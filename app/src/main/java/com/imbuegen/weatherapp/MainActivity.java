package com.imbuegen.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.w3c.dom.Text;

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

    final private String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
    final private String APP_ID = "df78dcfa9580e72b15fdf62d406d34ec";
    final private List<String> mDAYS_OF_WEEK = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

    private Button searchButton;
    private EditText editCityName;
    private TextView cityNameView;

    private String cityName = "Mumbai";
    private String weatherURL = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
    private String mainWeatherText = "Sunny";
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;
    private HashMap<String, ArrayList<WeatherDataModel>> hashMapWeatherData;
    private ArrayList<WeatherDataModel> listOfWeatherObjs;
    private ArrayList<WeatherDataModel> tempWdm;
    private ArrayList<String> days_ExpList;
    private ExpandableListView days_ExpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_main);

        init();
        //spinnerInit();

        searchButton = findViewById(R.id.btn_searchBtn);
        editCityName = findViewById(R.id.edit_txt_cityName);
        cityNameView = findViewById(R.id.txt_cityName);
        editCityName.setVisibility(View.INVISIBLE);

        editCityName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    search();
                    Log.d("WeatherApp","setOnDitorCalled");
                    return true;
                }
                return false;
            }
        });
        new MyJSONTask().execute(weatherURL);

        editCityName.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        cityNameView.setVisibility(View.VISIBLE);
    }

    public void search() {
        cityName = editCityName.getText().toString();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        weatherURL =  BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
        new MyJSONTask().execute(weatherURL);

    }

    public void searchOnClick(View view) {
        editCityName.setVisibility(View.VISIBLE);
        editCityName.setClickable(true);
        editCityName.setText(cityName);
        searchButton.setVisibility(View.INVISIBLE);
        cityNameView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

/*  private void spinnerInit() {
        Spinner spinner = findViewById(R.id.spinner_cityChange);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.CITIES, R.layout.mspinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.mspinner_item_dropdown);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cityName = getResources().getStringArray(R.array.CITIES)[position];    //get city name from array of string in resources
        String weatherUrl = BASE_URL + cityName + "&mode=json&appid=" + APP_ID;
        init();
        new MyJSONTask().execute(weatherUrl);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Restart application", Toast.LENGTH_LONG).show();
    }*/

    //Initialisation:
    void init() {
        hashMapWeatherData = new HashMap<>();
        days_ExpList = new ArrayList<>();
        listOfWeatherObjs = new ArrayList<>();
        tempWdm = new ArrayList<>();
    }

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
                Toast.makeText(getApplicationContext(), "Unable to fetch data", Toast.LENGTH_SHORT).show();
            else {
                days_ExpListView = findViewById(R.id.expListView_Days);
                //To collapse all others except selected:-
                days_ExpListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    int previousItem = 1;
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousItem) {
                            days_ExpListView.collapseGroup(previousItem);
                            previousItem = groupPosition;
                        }
                    }
                });

                MyExpandableAdapter days_ExpListViewAdapter = new MyExpandableAdapter(MainActivity.this, days_ExpList, hashMapWeatherData);
                days_ExpListView.setAdapter(days_ExpListViewAdapter);
                //default expand group 1:
                days_ExpListView.expandGroup(1);
                //Change main part:
                changeMain();

                editCityName.setText(cityName);

//                Log.d("WeatherApp", String.valueOf(hashMapWeatherData.size()));
//                for (Map.Entry<String, ArrayList<WeatherDataModel>> entry : hashMapWeatherData.entrySet()) {
//                    for (int j = 0; j < entry.getValue().size(); j++)
//                        Log.d("WeatherApp", String.valueOf(j) + ":" + " Temp = " + entry.getValue().get(j).get_temperature() + " ,Day = " + entry.getKey() + " ,Time = " + entry.getValue().get(j).getDateTime() + "\n");
//                }

            }
        }

        private void changeMain() {
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