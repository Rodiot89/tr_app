package t_friends.trichterscoreboard;

/**
 * Created by Basti on 23.07.2017.
 */

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CustomArrayAdapter extends BaseAdapter {
    private final Activity context;
    private ArrayList<TrichterPerson.TrichterEvent> SortList;
    private SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy/MM/dd");


    static class ViewHolder {
        private TextView tv_rank;
        private TextView tv_personName;
        private TextView tv_time;
        private TextView tv_date;
        private TextView tv_eventName;
        private ImageView iv_personImage;
    }

    public CustomArrayAdapter(Activity context, ArrayList<TrichterPerson.TrichterEvent> SortList) {
        super();
        //super(context, R.layout.listview_layout, AdaptedList);
        this.context = context;
        this.SortList = SortList;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return SortList.get(position);
    }

    @Override
    public int getCount() {
        return SortList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_layout, null);
            // configure view holder
            holder = new ViewHolder();
            holder.tv_rank = (TextView) rowView.findViewById(R.id.listView_rank);
            holder.iv_personImage = (ImageView) rowView.findViewById(R.id.listView_image);
            holder.tv_personName = (TextView) rowView.findViewById(R.id.listView_personName);
            holder.tv_time = (TextView) rowView.findViewById(R.id.listView_time);
            holder.tv_date = (TextView) rowView.findViewById(R.id.listView_date);
            holder.tv_eventName = (TextView) rowView.findViewById(R.id.listView_eventName);
            rowView.setTag(holder);
        }
        else{
            holder = (ViewHolder) rowView.getTag();
        }

        // fill data

        TrichterPerson.TrichterEvent item = SortList.get(position);
        holder.tv_rank.setText("XX");
        //holder.tv_rank.setText(String.valueOf(item.getRank()));
        holder.tv_personName.setText(item.getParentPersonName());
        holder.tv_time.setText(String.valueOf(item.getTime()));
        holder.tv_date.setText(simpleDate.format(item.getDate()));
        holder.tv_eventName.setText(item.getEventName());
        return rowView;
    }

}

/*
public class CustomArrayAdapter extends BaseAdapter {
    private final Activity context;
    private final String[] number;
    private final String[] personName;
    private final String[] time;
    private final String[] date;
    private final String[] eventName;

    static class ViewHolder {
        private TextView tv_number;
        private TextView tv_personName;
        private TextView tv_time;
        private TextView tv_date;
        private TextView tv_eventName;
        private ImageView iv_personImage;
    }

    public CustomArrayAdapter(Activity context, ArrayList<HashMap<String, String[]>> AdaptedList) {
        super();
        //super(context, R.layout.listview_layout, AdaptedList);
        this.context = context;
        this.number = AdaptedList.get(1).get("number");
        this.personName = AdaptedList.get(1).get("personName");
        this.time = AdaptedList.get(1).get("time");
        this.date = AdaptedList.get(1).get("date");
        this.eventName = AdaptedList.get(1).get("eventName");;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return number[position];
    }

    @Override
    public int getCount() {
        return number.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_layout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tv_number = (TextView) rowView.findViewById(R.id.listView_number);
            viewHolder.iv_personImage = (ImageView) rowView.findViewById(R.id.listView_image);
            viewHolder.tv_personName = (TextView) rowView.findViewById(R.id.listView_personName);
            viewHolder.tv_time = (TextView) rowView.findViewById(R.id.listView_time);
            viewHolder.tv_date = (TextView) rowView.findViewById(R.id.listView_date);
            viewHolder.tv_eventName = (TextView) rowView.findViewById(R.id.listView_eventName);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.tv_number.setText(number[position]);
        holder.tv_personName.setText(personName[position]);
        holder.tv_time.setText(time[position]);
        holder.tv_date.setText(date[position]);
        holder.tv_eventName.setText(eventName[position]);


        return rowView;
    }

}


 */