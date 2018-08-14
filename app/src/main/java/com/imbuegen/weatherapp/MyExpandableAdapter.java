package com.imbuegen.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyExpandableAdapter extends BaseExpandableListAdapter{

    private Context context;
    private List<String> parent_DaysList;  //parent
    private HashMap<String,ArrayList<WeatherDataModel>> child_TimeList;    //child

    public MyExpandableAdapter(Context _context, List<String> _parent_DaysList){
        this.context=_context;
        this.parent_DaysList = new ArrayList<>();
        this.parent_DaysList = _parent_DaysList;
    }
    
    @Override
    public int getGroupCount() {
        return this.parent_DaysList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return this.parent_DaysList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {    //doubt
        return this.child_TimeList.get(parent_DaysList.get(i));  //returns array of children, since I want horizontal display of weather per 3 hour
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater parentLayoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = parentLayoutInflater.inflate(R.layout.days_view, viewGroup);
        }
        TextView listTitleTextView = (TextView) view
                .findViewById(R.id.txt_day);
        listTitleTextView.setText(parent_DaysList.get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater childLayoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = childLayoutInflater.inflate(R.layout.time_wise_weather, viewGroup);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}

class MyChildAdapter extends ArrayAdapter<WeatherDataModel>{

    private Context context;
    private ArrayList<WeatherDataModel> wdm;

    public MyChildAdapter(Context _context, ArrayList<WeatherDataModel> data){

        super(_context,R.layout.weather_obj_layout,data);
        this.context = _context;
        this.wdm = data;
    }
}
