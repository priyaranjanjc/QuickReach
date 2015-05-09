package com.jc.ms.jcproject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.ms.jcproject.Adapters.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressLint("NewApi")
public class QuickAppService extends Service {

    private WindowManager.LayoutParams params;
    private Context mContext;
    private DisplayManager displayManager;
    private Display display;
    private PopupWindow pw;
    private Button games;
    private Button favourites;
    private Button social;
    private ActivityManager activityManager;
    private List<RecentTaskInfo> x;
    private List<ApplicationInfo> recentApps = new ArrayList<ApplicationInfo>();
    private GridViewAdapter mAdapter;
    private GridView mGridView;
    private LayoutInflater inflater;
    private ImageView mDelete;
    private PackageManager pm;
    private List<ApplicationInfo> appInfo;
    private List<ApplicationInfo> socialApps;
    private LinearLayout mCategories;
    private ArrayList<String> mPackages;
    private ImageView mView;
    private Handler mHandler;
    private WindowManager.LayoutParams deleteParams;
    private Runnable mSetIconTransparent = new Runnable() {
        @Override
        public void run() {
            mView.setAlpha(0.6f);
        }
    };
    private View.OnClickListener mCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String cat = (String) v.getTag();
            if (cat.equalsIgnoreCase("toggle")) {
                mPackages.clear();
                mPackages.add("bluetooth");
                mPackages.add("wifi");
                mPackages.add("mobileData");
                mPackages.add("gps");
            } else {
                SharedPreferences pref = getSharedPreferences(cat.toLowerCase(), MODE_PRIVATE);
                Set<String> appList = pref.getStringSet("apps", null);
                mPackages.clear();
                if (appList != null) {
                    for (String pack : appList)
                        mPackages.add(pack);
                }
            }
            for (int i = 0; i < mCategories.getChildCount(); i++)
                mCategories.getChildAt(i).setSelected(false);
            v.setSelected(true);
            mAdapter.notifyDataSetChanged();
        }
    };
    private WindowManager wmgr;

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
        mHandler = new Handler();

        params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;

        mView = new ImageView(this);
        mView.setImageResource(R.drawable.chat_head);
        mView.setScaleType(ImageView.ScaleType.FIT_XY);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.width = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        params.height = (int) getResources().getDimension(android.R.dimen.app_icon_size);


        wmgr = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        mDelete = new ImageView(mContext);
        mDelete.setImageResource(R.drawable.delete);
        mDelete.setScaleType(ImageView.ScaleType.FIT_XY);
        mDelete.setPadding(10, 10, 10, 10);

        deleteParams = new WindowManager.LayoutParams();
        deleteParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        deleteParams.format = PixelFormat.RGBA_8888;
        deleteParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        deleteParams.width = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        deleteParams.height = (int) getResources().getDimension(android.R.dimen.app_icon_size);
        deleteParams.y = display.getHeight() - (deleteParams.height);

        wmgr.addView(mDelete, deleteParams);
        mDelete.setVisibility(View.GONE);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        wmgr.addView(mView, params);

        //Touch listener to handle the position of floating icon according to user touch.
        mView.setOnTouchListener(new View.OnTouchListener() {
            long start;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeCallbacksAndMessages(null);
                mView.setAlpha(1f);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        mDelete.setVisibility(View.VISIBLE);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        wmgr.updateViewLayout(mView, params);

                        break;

                    case MotionEvent.ACTION_UP:
                        mHandler.postDelayed(mSetIconTransparent, 8000);
                        mDelete.setVisibility(View.GONE);

                        int[] locationDelete = new int[2];
                        int[] locationMain = new int[2];
                        mDelete.getLocationOnScreen(locationDelete);
                        mView.getLocationOnScreen(locationMain);

                        locationMain[0] = locationMain[0] + mDelete.getWidth() / 2;
                        locationMain[1] = locationMain[1] + mDelete.getHeight()/2;

                        if((locationMain[0]  >= locationDelete[0]  &&  locationMain[0] <=
                                locationDelete[0] + mDelete.getWidth()) &&
                                (locationMain[1] >= locationDelete[1]  &&  locationMain[1]  <=
                                    locationDelete[1] + mDelete.getHeight())){
                                stopSelf();
                            }
                        break;
                }
                return false;
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setAlpha(1f);
                constructPopupWindow();
            }
        });


        mHandler.postDelayed(mSetIconTransparent, 8000);

        super.onCreate();
    }


    // Below function is used to display the pop when user clicks on the floating icon;

    private void constructPopupWindow() {
        mHandler.removeCallbacksAndMessages(null);
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
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandler.postDelayed(mSetIconTransparent, 8000);
            }
        });


        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mCategories = (LinearLayout) view.findViewById(R.id.categories);
        mPackages = new ArrayList();
        mAdapter = new GridViewAdapter(mContext, mPackages);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // here the Launching of application and toggles are  handled when the user clicks on application
                // icon in popup window 

                if (mPackages.get(position).equalsIgnoreCase("bluetooth")) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        Toast.makeText(mContext, "Bluetooth disabled.", Toast.LENGTH_SHORT).show();
                    } else {
                        mBluetoothAdapter.enable();
                        Toast.makeText(mContext, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
                    }
                } else if (mPackages.get(position).equalsIgnoreCase("wifi")) {
                    WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
                    if (wifiManager.isWifiEnabled()) {
                        Toast.makeText(mContext, "Wifi disabled.", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(mContext, "Wifi enabled.", Toast.LENGTH_SHORT).show();
                } else if (mPackages.get(position).equalsIgnoreCase("gps")) {
                    Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else if (mPackages.get(position).equalsIgnoreCase("mobileData")) {
                    Intent i = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    String pack = (String) mAdapter.getItem(position);
                    Intent i = pm.getLaunchIntentForPackage(pack);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (i != null) {
                        startActivity(i);
                    }

                }
                pw.dismiss();
            }
        });
        SharedPreferences mCategoriesPref = mContext.getSharedPreferences(MainActivity.CATEGORIES, MODE_PRIVATE);
        Set<String> mCategoriesList = mCategoriesPref.getStringSet(MainActivity.CATEGORIES, null);
        if (mCategoriesList != null) {
            mCategoriesList.add("Toggle");
            for (String category : mCategoriesList) {
                View v = inflater.inflate(R.layout.category_item, null, false);
                v.setPadding(15, 15, 15, 15);
                ImageView iv = (ImageView) v.findViewById(R.id.imageView);
                TextView tv = (TextView) v.findViewById(R.id.title);
                tv.setText(category);
                v.setOnClickListener(mCategoryClickListener);
                v.setTag(category);
                if (category.equalsIgnoreCase("recents")) {
                    iv.setImageResource(R.drawable.document_open_recent);
                } else if (category.equalsIgnoreCase("favourites")) {
                    iv.setImageResource(R.drawable.favourite);
                } else if (category.equalsIgnoreCase("music")) {
                    iv.setImageResource(R.drawable.music);
                } else if (category.equalsIgnoreCase("toggle")) {
                    iv.setImageResource(R.drawable.toggles);
                } else
                    iv.setImageResource(R.drawable.default_icon);
                v.setBackgroundResource(R.drawable.background_selector);
                mCategories.addView(v);
            }
        }
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandler.postDelayed(mSetIconTransparent, 6000);
            }
        });
        pw.showAsDropDown(mView, 0, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        pm = getPackageManager();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        wmgr.removeView(mView);
        wmgr.removeView(mDelete);
        super.onDestroy();
    }

}
