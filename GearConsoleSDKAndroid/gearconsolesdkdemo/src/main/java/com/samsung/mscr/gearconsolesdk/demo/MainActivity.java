package com.samsung.mscr.gearconsolesdk.demo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.samsung.mscr.gearconsolesdk.library.GCUtils;
import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodLoader;
import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodScreen;
import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodVisibility;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getName();
    private String[] mStrings = new String[]{"Notification", "Motion", "Test", "Images", "Screen manage", "App state"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, mStrings));
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        final Context context = getApplication().getApplicationContext();
        switch (position) {
            case 0:
                GCUtils.sendNotification(
                        MainActivity.this,
                        "com.samsung.mscr.gearconsolesdk.demo",
                        "Title",
                        "Message " + Integer.valueOf((int) Math.round(Math.random())),
                        Integer.valueOf((int) Math.round(Math.random())),
                        null,
                        "5t13HmJcA3.GearConsoleSDKDemo");
                break;
            case 1:
                intent = new Intent(GCUtils.INTENT_NAME);
                intent.putExtra("inputType", "sdk1");
                getApplication().getApplicationContext().sendBroadcast(intent);
                startActivity(new Intent(getApplicationContext(), MotionActivity.class));
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_edittext, null);
                final EditText editText = (EditText)view.findViewById(R.id.input);
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (new GCMethodLoader(context))
                            .setText("Hello, Gear")
                            .setShowing(true)
                            .setId(GCUtils.getNextRequestId(context))
                            .send();
                    }
                });
                builder.create().show();
                break;
            case 3:
                intent = new Intent(GCUtils.INTENT_NAME);
                intent.putExtra("inputType", "default");
                getApplication().getApplicationContext().sendBroadcast(intent);
                startActivity(new Intent(getApplicationContext(), ImagesActivity.class));
                break;
            case 4:
                (new GCMethodScreen(context)).setId(GCUtils.getNextRequestId(context)).setTurnedOff().send();
                break;
            case 5:
                (new GCMethodVisibility(context)).send();
                break;
            default:
        }
    }
}
