package com.samsung.mscr.gearconsolesdk.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.samsung.mscr.gearconsolesdk.library.GCUtils;
import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodUnsubscribe;
import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.subscribes.GCSubscribeDevicemotion;

public class MotionActivity extends Activity {

    private static final String TAG = MotionActivity.class.getName();
    private ImageView mImageView;
    private float mBaseX, mBaseY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        registerReceiver(activityHandler, new IntentFilter(GCUtils.EVENT_MOTION));
        mImageView = (ImageView)findViewById(R.id.image_view);
        mBaseX = mImageView.getTranslationX();
        mBaseY = mImageView.getTranslationY();

        final Context context = getApplicationContext();
        Button button1 = (Button)findViewById(R.id.button_choose_image);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new GCSubscribeDevicemotion(context)).send();
            }
        });

        Button button2 = (Button)findViewById(R.id.button_choose_image);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new GCMethodUnsubscribe(context)).setName(GCUtils.SUBSCRIBE_DEVICEMOTION).send();
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver activityHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GCUtils.EVENT_MOTION)) {
                float[] rotation = intent.getFloatArrayExtra(GCUtils.MOTION_ROTATION);
                Log.d(TAG, "MOTION x:"+rotation[0]);
                mImageView.setTranslationX(mBaseX - rotation[0]*10);
                mImageView.setTranslationY(mBaseY - rotation[1]*10);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(activityHandler);
    }
}
