package com.polant.projectsport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.polant.projectsport.activity.ActivityCalculateFood;
import com.polant.projectsport.activity.ActivityOtherCalculators;
import com.polant.projectsport.adapter.TabsPagerFragmentAdapter;
import com.polant.projectsport.data.Database;
import com.polant.projectsport.preferences.PreferencesNewActivity;
import com.polant.projectsport.preferences.PreferencesOldActivity;

/**
 * Created by Антон on 02.10.2015.
 */
public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    public static final int DBVersion = Database.getDatabaseVersion();
    public static final String DB_VERSION_KEY = "DB_VERSION_KEY";


    private DrawerLayout drawerLayout;

    private  Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Database DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings.setCurrentTheme(this, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initNavigationView();
        initTabLayout();

        DB = new Database(this);
        DB.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MY_DB_LOGS", "OnDestroyMainActivity");
        //Закрываю базу при закрытии всего приложения.
        DB.close();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_view_open,
                R.string.navigation_view_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.actionArticleItem:
                        showNotificationTab();
                        break;
                    case R.id.ActionIndexBodyWeight:
                        Intent indexBodyIntent = new Intent(MainActivity.this, ActivityOtherCalculators.class);
                        indexBodyIntent.setAction(ActivityOtherCalculators.ACTION_INDEX_BODY);
                        startActivityForResult(indexBodyIntent, Constants.SHOW_ACTIVITY_OTHER_CALCULATORS);
                        break;
                    case R.id.ActionDayNeedCalories:
                        Intent needCaloriesIntent = new Intent(MainActivity.this, ActivityOtherCalculators.class);
                        needCaloriesIntent.setAction(ActivityOtherCalculators.ACTION_NEED_CALORIES);
                        startActivity(needCaloriesIntent);
                        //startActivityForResult(needCaloriesIntent, Constants.SHOW_ACTIVITY_OTHER_CALCULATORS);
                        break;
                    case R.id.ActionCalculateFood:
                        Intent foodCaloriesCounter = new Intent(MainActivity.this, ActivityCalculateFood.class);
                        startActivityForResult(foodCaloriesCounter, Constants.SHOW_ACTIVITY_CALCULATE_FOOD);
                        break;
                    case R.id.actionSettingsItem:
                        //добавим совместимость со старыми версиями платформы.
                        Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                                PreferencesOldActivity.class : PreferencesNewActivity.class;

                        Intent intent = new Intent(MainActivity.this, c);
                        Log.d("Class in intent", c.getName());
                        startActivityForResult(intent, PreferencesNewActivity.SHOW_PREFERENCES);
                        break;
                }

                return true;
            }
        });
    }

    public void initTabLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Открытие таба "Статьи" из NavigationView.
    private void showNotificationTab() {
        viewPager.setCurrentItem(Constants.TAB_TWO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Не проверяю это через if, потому что Активити настроей приложения может вызваться в
        //любой другой Активити - и тогда requestCode не будет равен PreferencesNewActivity.SHOW_PREFERENCES;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        updateFromPreferences(sp);
        /*if (requestCode == PreferencesNewActivity.SHOW_PREFERENCES){
            updateFromPreferences();
        }*/

        if (requestCode == Constants.SHOW_ACTIVITY_OTHER_CALCULATORS){
            //Удаляю значение настройки текущего действия, которое используется в ActivityOtherCalculators.
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(ActivityOtherCalculators.CURRENT_ACTION);
            editor.apply();
        }

    }

    //Применение настроек приложения.
    private void updateFromPreferences(SharedPreferences sp){
        //TODO: сделать обработчик применения выбранных настроек.

        //Применяю тему.
        ThemeSettings.setUpdatedTheme(this, sp);
    }

}
