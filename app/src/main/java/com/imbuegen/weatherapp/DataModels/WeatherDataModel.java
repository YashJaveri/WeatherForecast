package com.imbuegen.weatherapp.DataModels;

import com.imbuegen.weatherapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private int getImageSource(int weatherId) {
        //Check for night:
        boolean isNight = false;

        Calendar time = Calendar.getInstance();
        time.setTime(getDateTime());

        String s = new SimpleDateFormat("HH").format(time.getTime());
        int t = Integer.parseInt(s) - 20;

        if (t < -13 || t >= 0)    //for time between 20:00 to 06:00
            isNight = true;

        if (weatherId >= 0 && weatherId < 300)
            return R.drawable.ic_bolt;    //thunder storm
        else if (weatherId >= 300 && weatherId < 500)
            return R.drawable.ic_drop;    //light rains
        else if (weatherId >= 500 && weatherId < 600)
            return R.drawable.ic_rain_2;  //shower
        else if (weatherId >= 600 && weatherId <= 700)
            return R.drawable.ic_ice_crystal; //snow
        else if (weatherId >= 701 && weatherId <= 771)
            return R.drawable.ic_haze;   //foggy
        else if (weatherId >= 772 && weatherId < 800)
            return R.drawable.ic_bolt;   //thunder storm
        else if (weatherId == 800)
            return (isNight) ? R.drawable.ic_night : R.drawable.ic_sun;  //sunny || night
        else if (weatherId >= 801 && weatherId <= 804)
            return R.drawable.ic_cloud;   //cloudy
        else if (weatherId >= 900 && weatherId <= 902)
            return R.drawable.ic_cloud;    //cloudy
        else if (weatherId == 903)
            return R.drawable.ic_ice_crystal;    //snow
        else if (weatherId == 904)
            return (isNight) ? R.drawable.ic_night : R.drawable.ic_sun;  //sunny || night
        else if (weatherId >= 905 && weatherId <= 1000)
            return R.drawable.ic_bolt;    //thunder storm
        else
            return (isNight) ? R.drawable.ic_night : R.drawable.ic_sun;  //sunny || night
    }

    public void setDateTime(String _dateTimeString) {
        try {
            dateTime = formatter.parse(_dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getDateTime() {
        return dateTime;
    }
}
