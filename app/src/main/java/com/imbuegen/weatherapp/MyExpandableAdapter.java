package com.imbuegen.weatherapp;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> parent_DaysList;  //parent
    private HashMap<String, ArrayList<WeatherDataModel>> child_TimeWiseList;    //child
    private TimeWiseChildListAdapter timeWiseChildListAdapter;

    MyExpandableAdapter(Context _context, List<String> _parent_DaysList, HashMap<String, ArrayList<WeatherDataModel>> _child_TimeWiseList) {
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
        return this.parent_DaysList.get(i);
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
            assert parentLayoutInflater != null;
            view = parentLayoutInflater.inflate(R.layout.days_view, null);
        }
        TextView listTitleTextView = view
                .findViewById(R.id.txt_day);
        String s;
        if (new SimpleDateFormat("HH").format(child_TimeWiseList.get(parent_DaysList.get(0)).get(0).getDateTime()).equals("21")) {
            if (i == 0)
                s = "Yesterday";
            else if (i == 1)
                s = "Today";
            else if (i == 2)
                s = "Tomorrow";
            else
                s = parent_DaysList.get(i);
            listTitleTextView.setText(s);
            return view;
        } else {
            if (i == 0)
                s = "Today";
            else if (i == 1)
                s = "Tomorrow";
            else
                s = parent_DaysList.get(i);
            listTitleTextView.setText(s);
            return view;
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder childHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.time_wise_weather, viewGroup, false);
            childHolder = new ChildHolder();
            view.setTag(childHolder);
        } else
            childHolder = (ChildHolder) view.getTag();

        childHolder.horizontalRecView = view.findViewById(R.id.recyclerView_timeWiseWeather);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        childHolder.horizontalRecView.setLayoutManager(layoutManager);
            timeWiseChildListAdapter = new TimeWiseChildListAdapter(context, child_TimeWiseList.get(parent_DaysList.get(i)));
            childHolder.horizontalRecView.setAdapter(timeWiseChildListAdapter);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private static class ChildHolder {
        private RecyclerView horizontalRecView;
    }
}
