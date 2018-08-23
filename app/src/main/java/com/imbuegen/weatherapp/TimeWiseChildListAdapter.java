package com.imbuegen.weatherapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imbuegen.weatherapp.DataModels.WeatherDataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeWiseChildListAdapter extends RecyclerView.Adapter<TimeWiseChildListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherDataModel> awdm;

    TimeWiseChildListAdapter(Context _context, ArrayList<WeatherDataModel> _awdm) {
        this.context = _context;
        this.awdm = _awdm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View linearLayout = inflater.inflate(R.layout.weather_obj_layout, null, false);

        ViewHolder viewHolder = new ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WeatherDataModel weatherDataModel = awdm.get(i);
        Calendar cal = Calendar.getInstance();
        cal.setTime(weatherDataModel.getDateTime());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        viewHolder.timeView.setText(sdf.format(cal.getTime()));
        viewHolder.weatherIcon.setImageResource(weatherDataModel.get_ResourceOfImage());
        String s = String.valueOf(weatherDataModel.get_temperature()) + "°";
        viewHolder.temperatureView.setText(s);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return awdm.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeView;
        private ImageView weatherIcon;
        private TextView temperatureView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timeView = itemView.findViewById(R.id.txt_time);
            this.weatherIcon = itemView.findViewById(R.id.img_weather);
            this.temperatureView = itemView.findViewById(R.id.txt_avgTemp);
        }
    }
}
