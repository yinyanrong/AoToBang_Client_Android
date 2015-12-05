//package com.aotobang.app.activity;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.ListView;
//
///**
// * Created by Administrator on 2015/12/3.
// */
//public class NavListViewActivity extends Activity
//{
//    private ListView mLvLeftMenu;
//    private DrawerLayout mDrawerLayout;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nav_list_view);
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
//        mLvLeftMenu = (ListView) findViewById(R.id.id_lv_left_menu);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
//        setSupportActionBar(toolbar);
//
//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu_camera);
//        ab.setDisplayHomeAsUpEnabled(true);
//
//        setUpDrawer();
//    }
//    private void setUpDrawer()
//    {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        mLvLeftMenu.addHeaderView(inflater.inflate(R.layout.nav_header_home, mLvLeftMenu, false));
//        mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
////            getMenuInflater().inflate(R.menu.nav_header_home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == android.R.id.home)
//        {
//            mDrawerLayout.openDrawer(GravityCompat.START);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//
//}
