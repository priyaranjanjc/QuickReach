package com.jc.ms.jcproject.Adapters;

import java.util.List;

import com.jc.ms.jcproject.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	// private List<String> mPackages;
	private PackageManager pm;
	private List<ApplicationInfo> mPackages;

	public GridViewAdapter(Context context, List<ApplicationInfo> recentApps) {
		mContext = context;
		mPackages = recentApps;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pm = context.getPackageManager();

	}

	@Override
	public int getCount() {

		return mPackages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPackages.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.grid_view_layout, null,
					false);
		}

		ImageButton ib = (ImageButton) convertView.findViewById(R.id.icon);
		ib.setImageDrawable(pm.getApplicationIcon(mPackages.get(position)));
		return convertView;
	}

}
