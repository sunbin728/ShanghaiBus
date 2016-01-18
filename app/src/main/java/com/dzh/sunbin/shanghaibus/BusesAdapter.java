package com.dzh.sunbin.shanghaibus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sunbin on 15-5-28.
 */
public class BusesAdapter extends ArrayAdapter<Bus> {
    private int resourceId;
    public BusesAdapter(Context context, int textViewResourceId,
                        List<Bus> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bus bus = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.BusName = (TextView) view.findViewById
                    (R.id.bus_name);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.BusName.setText(bus.GetName());
        return view;
    }

    class ViewHolder {
        TextView BusName;
    }

}
