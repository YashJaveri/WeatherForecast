package com.imbuegen.weatherapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WeatherDataModel {
    private int temperature;
    private Date dateTime;
    private int resourceOfImage;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int get_temperature() {
        return temperature;
    }

    public void set_temperature(double _temperature) {
        _temperature = _temperature - 273.15;   //Convert to kelvin
        this.temperature = (int) _temperature;   //And Cast it to int
    }

    public int get_ResourceOfImage() {
        return resourceOfImage;
    }

    public void set_ResourceOfImage(int weatherId) {

        this.resourceOfImage = getImageSource(weatherId);
    }

    private int getImageSource(int weatherId){
        if (weatherId >= 0 && weatherId < 300)
            return R.drawable.storm;    //thunder storm
        else if (weatherId >= 300 && weatherId < 500)
            return R.drawable.drop;    //light rains
        else if (weatherId >= 500 && weatherId < 600)
            return R.drawable.rain;  //shower
        else if (weatherId >= 600 && weatherId <= 700)
            return R.drawable.snowflake; //snow
        else if (weatherId >= 701 && weatherId <= 771)
            return R.drawable.haze_2;   //foggy
        else if (weatherId >= 772 && weatherId < 800)
            return R.drawable.storm;   //thunder storm
        else if (weatherId == 800)
            return R.drawable.sun;  //sunny
        else if (weatherId >= 801 && weatherId <= 804)
            return R.drawable.cloudy;   //cloudy
        else if (weatherId >= 900 && weatherId <= 902)
            return R.drawable.cloudy;    //thunder storm
        else if (weatherId == 903)
            return R.drawable.snowflake;    //snow
        else if (weatherId == 904)
            return R.drawable.sun;  //sunny
        else if (weatherId >= 905 && weatherId <= 1000)
            return R.drawable.storm;    //thunder storm
        else
            return R.drawable.sun;  //default
    }

    public void setDateTime(String _dateTimeString){
        try{
            dateTime = formatter.parse(_dateTimeString);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }

    public Date getDateTime() {
        return dateTime;
    }
}
