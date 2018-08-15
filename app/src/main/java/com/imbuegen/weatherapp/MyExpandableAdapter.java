package com.imbuegen.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> parent_DaysList;  //parent
    private HashMap<String, ArrayList<WeatherDataModel>> child_TimeWiseList;    //child

    public MyExpandableAdapter(Context _context, List<String> _parent_DaysList, HashMap<String, ArrayList<WeatherDataModel>> _child_TimeWiseList) {
        this.context = _context;
        this.parent_DaysList = _parent_DaysList;
        this.child_TimeWiseList = _child_TimeWiseList;
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
        return this.child_TimeWiseList.get(parent_DaysList.get(i));  //returns array of children, since I want horizontal display of weather per 3 hour
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
            view = parentLayoutInflater.inflate(R.layout.days_view, null);
        }
        TextView listTitleTextView = view
                .findViewById(R.id.txt_day);
        listTitleTextView.setText(parent_DaysList.get(i));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ListView child_TimeWiseListView = null;
        if (view == null) {
            child_TimeWiseListView = view.findViewById(R.id.listView_timeWiseWeather);
            TimeWiseChildList timeWiseChildListAdapter = new TimeWiseChildList(context, this.child_TimeWiseList.get(parent_DaysList.get(i)));

            child_TimeWiseListView.setAdapter(timeWiseChildListAdapter);
        }
        return child_TimeWiseListView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
