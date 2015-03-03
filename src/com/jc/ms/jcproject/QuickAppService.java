package com.jc.ms.jcproject;

import java.util.ArrayList;
import java.util.List;

import com.jc.ms.jcproject.Adapters.GridViewAdapter;
import com.jc.ms.jcproject.widgets.SemiCircleDrawable;
import com.jc.ms.jcproject.widgets.SemiCircleDrawable.Direction;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint("NewApi")
public class QuickAppService extends Service implements View.OnClickListener {

	private WindowManager.LayoutParams params;
	private Context mContext;
	private DisplayManager displayManager;
	private Display display;
	private PopupWindow pw;
	private ImageButton recents;
	private Button games;
	private Button favourites;
	private Button social;
	private ActivityManager activityManager;
	private List<RecentTaskInfo> x;
	private List<ApplicationInfo> recentApps = new ArrayList<ApplicationInfo>();
	private GridViewAdapter mRecentAdapter;
	private GridView mGridView;
	private LayoutInflater inflater;
	private PackageManager pm;
	private List<ApplicationInfo> appInfo;
	private List<ApplicationInfo> socialApps;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		mContext = getBaseContext();

		displayManager = (DisplayManager) mContext
				.getSystemService(Context.DISPLAY_SERVICE);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);

		params = new WindowManager.LayoutParams();
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.format = PixelFormat.RGBA_8888;

		View view = inflater.inflate(R.layout.main_layout, null, false);
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.gravity = Gravity.BOTTOM | Gravity.LEFT;
		params.width = 100;
		params.height = 400;

		mGridView = (GridView) view.findViewById(R.id.grid_view);
		recents = (ImageButton) view.findViewById(R.id.folder_recents);
		
		recents.setOnClickListener(this);
		
		final WindowManager wmgr = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		
		activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		wmgr.addView(view, params);
		super.onCreate();
	}

	
	private void constructPopupWindow() {
		pw = new PopupWindow(QuickAppService.this);
		pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.main_layout, null, false);
		pw.setContentView(view);
		pw.setWidth(display.getWidth());
		pw.setHeight(LayoutParams.WRAP_CONTENT);
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);

		initializeButtons(view);
	}

	private void initializeButtons(View v) {}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.folder_recents:
			if(mRecentAdapter == null) {
				mRecentAdapter = new GridViewAdapter(mContext, socialApps);
				mGridView.setAdapter(mRecentAdapter);
			}
			mRecentAdapter.notifyDataSetChanged();
			break;
		}
	}

	public void getSocailPackages(){
		pm  = getPackageManager();
		appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		socialApps = new ArrayList<ApplicationInfo>();
		for(ApplicationInfo app : appInfo){
			if(app.packageName.contains("whatsapp")
					||app.packageName.contains("facebook")
					||app.packageName.contains("twitter")
					||app.packageName.contains("linkedin")
					||app.packageName.contains("plus")){
				socialApps.add(app);
			}
		}
		
	}
}
