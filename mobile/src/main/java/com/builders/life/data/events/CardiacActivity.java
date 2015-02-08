package com.builders.life.data.events;

import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.widget.Toast;

import com.github.pocmo.sensordashboard.R;
import com.builders.life.data.GPSTracker;

public class CardiacActivity extends ActionBarActivity {
    Button cardiac;
    static LatLng mypos;
    GPSTracker gps;
    Double mylat, mylong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardiac);
        Log.d("shabaz mp3", "starting!");
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.emergency);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();
        Log.d("shabaz mp3", "did something happen, IT mustø!");
        cardiac = (Button) findViewById(R.id.falseAlarm);
        cardiac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });


        gps = new GPSTracker(CardiacActivity.this);

        if (gps.canGetLocation()) {

            mylat = gps.getLatitude();
            mylong = gps.getLongitude();
            mypos = new LatLng(mylat, mylong);

            //Parse.enableLocalDatastore(this);
            //Parse.initialize(this, "8SPdCRfZ106A5GcZpK8Q5X5jG27cxzcZ2zUYljU2", "YWPxlO6AgdOVziH939tqIwMd6xDBR0ro4HiMxTDE");
            ParseUser.enableAutomaticUser();
            ParseInstallation.getCurrentInstallation().saveInBackground();
            ParsePush.subscribeInBackground("", new SaveCallback() {
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mylat + "\nLong: " + mylong, Toast.LENGTH_LONG).show();

                    } else {
                        Log.e("com.parse.push", "failed to subscribe for push", e);
                    }
                }
            });
            ParseACL defaultACL = new ParseACL();
            ParseACL.setDefaultACL(defaultACL, true);
            ParseGeoPoint geoPoint = new ParseGeoPoint(gps.getLatitude(), gps.getLongitude());
            ParsePush.subscribeInBackground("Emergency");
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("location", geoPoint);
            installation.saveInBackground();
            ParsePush push = new ParsePush();
            push.setChannel("Emergency");
            push.setMessage("Emergency at" + geoPoint.toString());
            push.sendInBackground();

        } else {
            gps.showSettingsAlert();
            // 	  mypos=new LatLng(51.5033630,-0.1276250);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cardiac, menu);
        //int resID=getResources().getIdentifier("raw/emergency", "raw", getPackageName());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
