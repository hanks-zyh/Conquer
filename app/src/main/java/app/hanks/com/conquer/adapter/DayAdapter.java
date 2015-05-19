package app.hanks.com.conquer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.bean.Day;


public class DayAdapter extends MyBaseAdpter<Day> {

	public DayAdapter(Context context, ArrayList<Day> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_day, null);
		}
		TextView tv_month = ViewHolder.get(convertView, R.id.tv_month);
		TextView tv_day = ViewHolder.get(convertView, R.id.tv_day);
		Day d = list.get(position);
		tv_month.setText(d.getMonth() + 1 + "月");
		tv_day.setText(d.getDay() + "");
		tv_day.setTextColor(d.isToday() ? Color.RED : Color.BLACK);
		return convertView;
	}

}
