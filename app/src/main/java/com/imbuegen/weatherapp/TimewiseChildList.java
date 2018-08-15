package com.imbuegen.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

class TimeWiseChildList extends BaseAdapter{

    private Context context;
    private ArrayList<WeatherDataModel> wdm;

    TimeWiseChildList(Context _context, ArrayList<WeatherDataModel> _wdm){
        this.context = _context;
        this.wdm = _wdm;
    }

    @Override
    public int getCount() {
        return this.wdm.size();
    }

    @Override
    public Object getItem(int i) {
        return this.wdm.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.weather_obj_layout, viewGroup);
        }
        TextView timeView = view.findViewById(R.id.txt_time);
        ImageView icon =  view.findViewById((R.id.img_weather)) ;
        TextView temperatureView = view.findViewById(R.id.txt_avgTemp);

        Date timeObj = this.wdm.get(i).getDateTime();
        int temperature = this.wdm.get(i).get_temperature();
        int imgSource = this.wdm.get(i).get_ResourceOfImage();

        timeView.setText(timeObj.toString());
        temperatureView.setText(temperature);
        icon.setImageResource(imgSource);
        return view;
    }
}
