package com.jc.ms.jcproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    public final static String CATEGORIES = "categories";

    private ListView mListView;
    private SharedPreferences mPrefCategories;
    private ArrayAdapter<String> mAdapeter;
    private MainFragment mFragment;
    private int mCurPos;
    private String[] catList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.category_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mPrefCategories = getSharedPreferences(CATEGORIES, MODE_PRIVATE);
        Set<String> categoryList = mPrefCategories.getStringSet(CATEGORIES, null);
        if (categoryList == null) {
            categoryList = new HashSet<String>();
            categoryList.add("Recents");
            categoryList.add("Favourites");
            SharedPreferences.Editor edit = mPrefCategories.edit();
            edit.putStringSet(CATEGORIES, categoryList);
            edit.commit();
        }
        catList = categoryList.toArray(new String[categoryList.size()]);
        mAdapeter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catList);
        mListView.setAdapter(mAdapeter);

        FragmentManager fm = getFragmentManager();

        mFragment = new MainFragment();
        fm.beginTransaction().add(R.id.main_fragment, mFragment).commit();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFragment.onCategoryChanged(catList[position]);
                mListView.setSelection(position);
                mCurPos = position;
                getActionBar().setTitle(catList[position]);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


        startService(new Intent(this, QuickAppService.class));

    }

    @Override
    protected void onResume() {
        mFragment.onCategoryChanged(catList[0]);
        mCurPos = 0;
        mListView.setSelection(0);
        getActionBar().setTitle(catList[0]);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_category) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Add Category");
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    Set<String> categoryList = mPrefCategories.getStringSet(CATEGORIES, null);
                    SharedPreferences.Editor edit = mPrefCategories.edit();
                    edit.clear();
                    categoryList.add(value);
                    edit.putStringSet(CATEGORIES, categoryList);
                    edit.commit();
                    catList = categoryList.toArray(new String[categoryList.size()]);
                    mAdapeter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, catList);
                    mListView.setAdapter(mAdapeter);
                    mAdapeter.notifyDataSetChanged();


                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
