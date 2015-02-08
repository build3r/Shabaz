package com.builders.life.data;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.builders.life.data.events.BusProvider;
import com.builders.life.data.events.CardiacActivity;
import com.builders.life.data.events.NewSensorEvent;
import com.github.pocmo.sensordashboard.R;
import com.parse.Parse;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    public RemoteSensorManager remoteSensorManager;

    private ViewPager pager;
    private View emptyState;
    final Handler handler = new Handler();
    TimerTask timerTask;
    Button cardiac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remoteSensorManager = RemoteSensorManager.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        pager = (ViewPager) findViewById(R.id.pager);

        emptyState = findViewById(R.id.empty_state);
        cardiac = (Button) findViewById(R.id.cardiac);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8SPdCRfZ106A5GcZpK8Q5X5jG27cxzcZ2zUYljU2", "YWPxlO6AgdOVziH939tqIwMd6xDBR0ro4HiMxTDE");
        cardiac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CardiacActivity.class));
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int id) {
                ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) pager.getAdapter();
                if (adapter != null) {
                    Sensor sensor = adapter.getItemObject(id);
                    if (sensor != null) {
                        remoteSensorManager.filterBySensorId((int) sensor.getId());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        timerTask = new TimerTask() {
//            public void run() {
//
//                //use a handler to run a toast that shows the current timestamp
//                handler.post(new Runnable() {
//                    public void run() {
//                        //get the current timeStamp
//                        Calendar calendar = Calendar.getInstance();
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
//                        final String strDate = simpleDateFormat.format(calendar.getTime());
//
//                        //show the toast
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
//                        toast.show();
//                    }
//                });
//            }
//        };
//        int resID=getResources().getIdentifier("raw/emergency", "raw", getPackageName());
//
//        MediaPlayer mediaPlayer = MediaPlayer.create(this, resID);
//        mediaPlayer.setVolume(1.0f,1.0f);
//        mediaPlayer.start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        List<Sensor> sensors = RemoteSensorManager.getInstance(this).getSensors();
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));

        if (sensors.size() > 0) {
            emptyState.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }

        remoteSensorManager.startMeasurement();


    }


    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().register(this);

        remoteSensorManager.stopMeasurement();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Sensor> sensors;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Sensor> symbols) {
            super(fm);
            this.sensors = symbols;
        }


        public void addNewSensor(Sensor sensor) {
            this.sensors.add(sensor);
        }


        private Sensor getItemObject(int position) {
            return sensors.get(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SensorFragment.newInstance(sensors.get(position).getId());
        }

        @Override
        public int getCount() {
            return sensors.size();
        }

    }


    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
        ((ScreenSlidePagerAdapter) pager.getAdapter()).addNewSensor(event.getSensor());
        pager.getAdapter().notifyDataSetChanged();
        emptyState.setVisibility(View.GONE);
        notifyUSerForNewSensor(event.getSensor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {

            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
