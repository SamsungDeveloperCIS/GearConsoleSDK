package com.samsung.mscr.gearconsolesdk.demo.manager;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.mscr.gearconsolesdk.library.GCUtils;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

public class GearConsoleProviderService extends SAAgent  {
    public static final String TAG = "GearConsoleProviderService";
    private static final boolean D = true;
    public static final int ACCESSORY_CHANNEL_ID = 134;
    public static final int MSG_SEND_MESSAGE = 1;
    public static SASocket mConnectionHandler;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (D) Log.d(TAG, "handleMessage");
            switch (msg.what) {
                case MSG_SEND_MESSAGE:
                    Bundle b = msg.getData();
                    String message = b.getString("message");
                    try {
                        mConnectionHandler.send(ACCESSORY_CHANNEL_ID, message.getBytes());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        			GearConsoleProviderService.this.findPeerAgents();
                    if (D) Log.d(TAG, "handleMessage MSG_SAY_HELLO");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public class LocalBinder extends Binder {
        public GearConsoleProviderService getService() {
            return GearConsoleProviderService.this;
        }
    }

    public GearConsoleProviderService() {
        super(TAG, ProviderConnection.class);
    }

    public class ProviderConnection extends SASocket {
        public int mConnectionId;

        public ProviderConnection() {
            super(ProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelId, String errorString, int error) {
            if (D) Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  " + error);
        }

        Handler mHandler = new Handler();
        @Override
        public void onReceive(int channelId, byte[] data) {
            final String strToUpdateUI = new String(data);
            final String jsonString = new String(data);

            if (D) Log.d(TAG, "onReceive:"+strToUpdateUI);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(GCUtils.INTENT_NAME);
                    JSONRPC2Response respIn = null;
                    try {
                        respIn = JSONRPC2Response.parse(jsonString);
                    } catch (JSONRPC2ParseException e) {
                        e.printStackTrace();
                    }
                    if (respIn != null && respIn.indicatesSuccess()) {
                        System.out.println("The request succeeded :");
                        System.out.println("\tresult : " + respIn.getResult());
                        System.out.println("\tid     : " + respIn.getID());
                        intent.setAction(GCUtils.SUBSCRIBE_DEVICEMOTION);
//                        intent.putExtra(GCUtils.MOTION_ROTATION, rotation);
                        getApplication().getApplicationContext().sendBroadcast(intent);
                    } else {
                        System.out.println("The request failed :");
                        JSONRPC2Error err = respIn.getError();
                        System.out.println("\terror.code    : " + err.getCode());
                        System.out.println("\terror.message : " + err.getMessage());
                        System.out.println("\terror.data    : " + err.getData());
                    }

                    JSONObject dataReceived, dataItem;
                    try {
                        dataReceived = new JSONObject(strToUpdateUI);
                        if (dataReceived.has("mode")) {
                            if (dataReceived.getString("mode").equals("motion")) {
                                dataItem = dataReceived.getJSONObject(GCUtils.MOTION_ROTATION);
                                float[] rotation = new float[3];
                                rotation[0] = Float.parseFloat(dataItem.getString("x"));
                                rotation[1] = Float.parseFloat(dataItem.getString("y"));
                                rotation[2] = Float.parseFloat(dataItem.getString("z"));
                                Intent broadcast = new Intent(GCUtils.EVENT_MOTION);
                                broadcast.setAction(GCUtils.SUBSCRIBE_DEVICEMOTION);
                                broadcast.putExtra(GCUtils.MOTION_ROTATION, rotation);
                                getApplication().getApplicationContext().sendBroadcast(broadcast);
                            }
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected void onServiceConnectionLost(int errorCode) {
            if (D) Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
                    + "error code =" + errorCode);
            mConnectionHandler.close();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (D) Log.i(TAG, "onCreate of smart view Provider Service");
        SA mAccessory = new SA();
        try {
            mAccessory.initialize(this);
            boolean isFeatureEnabled = mAccessory.isFeatureEnabled(SA.DEVICE_ACCESSORY);
        } catch (SsdkUnsupportedException e) {
            // Error Handling
        } catch (Exception e1) {
            if (D) Log.e(TAG, "Cannot initialize SAccessory package.");
            e1.printStackTrace();
            Toast.makeText(getApplicationContext(), "Cannot initialize SAccessory package.", Toast.LENGTH_LONG).show();
			/*
			 * Your application can not use Samsung Accessory SDK. You
			 * application should work smoothly without using this SDK, or you
			 * may want to notify user and close your app gracefully (release
			 * resources, stop Service threads, close UI thread, etc.)
			 */
            stopSelf();
        }
        registerReceiver(activityHandler, new IntentFilter(GCUtils.INTENT_NAME));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (D) Log.d(TAG, "Destroy Provider Service");
        unregisterReceiver(activityHandler);
        activityHandler = null;
    }

    private BroadcastReceiver activityHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (D) Log.d(TAG, "Update event received");
            try {
                if (intent.getAction().equals(GCUtils.INTENT_NAME)) {
                    //TODO rewrite to use JSONObject
                    JSONObject messageJson = new JSONObject();
                    String message = "";//"{\"config\":";
                    JSONObject messageConfig = new JSONObject();
                    if (intent.hasExtra("inputType")) {
//                        message.concat("\"inputType\":\"\"+inputType+\"\"");
                        messageConfig.put("inputType", intent.getStringExtra("inputType"));
                    }
                    if (intent.hasExtra("iconBase64")) {
//                        String packageId = "";//intent.getStringExtra(KEY_PACKAGE);
//                        String inputType = "";//intent.getStringExtra(KEY_INPUT);
//                        if (D) Log.d(TAG, String.format("packageId=%s", packageId));
//                        Drawable icon = getApplicationContext().getPackageManager().getApplicationIcon(packageId);
//                        Bitmap bitmap = GearConsoleUtils.drawableToBitmap(icon);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] b = baos.toByteArray();
//                        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
//                        message.concat(",\"iconBase64\":\"\"+imageEncoded+\"\"");
                        messageConfig.put("iconBase64", intent.getStringExtra("iconBase64"));
                    }
                    if (intent.hasExtra("loader")) {
                        messageJson.put("loader", true);
                    }
                    messageJson.put("config", messageConfig);
                    message = messageJson.toString();
                    if (intent.hasExtra("raw")) {
                        message = intent.getStringExtra("raw");
                    }
                    if (D) Log.d(TAG, "message="+message);
                    mConnectionHandler.send(ACCESSORY_CHANNEL_ID, message.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to send icon: " + e.toString());
            }
        }
    };

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        acceptServiceConnectionRequest(peerAgent);
    }

    @Override
    protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);
    }

    @Override
    protected void onPeerAgentUpdated(SAPeerAgent arg0, int arg1) {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, "onPeerAgentUpdated  arg1 =" + arg1);
        super.onPeerAgentUpdated(arg0, arg1);
    }

    public void findPeers() {
        if (D) Log.d(TAG, "findPeerAgents()");
        findPeerAgents();
    }

    @Override
    protected void onServiceConnectionResponse(SASocket thisConnection, int result) {
        if (D) Log.d(TAG, "onServiceConnectionResponse: connResult" + result);
        if (result == CONNECTION_SUCCESS) {
            if (D) Log.d(TAG, "Service connection CONNECTION_SUCCESS");
            this.mConnectionHandler = thisConnection;
            //TODO if app has been started then send app config again

        } else if (result == CONNECTION_ALREADY_EXIST) {
            if (D) Log.d(TAG, "Service connection CONNECTION_ALREADY_EXIST");
        } else {
            if (D) Log.d(TAG, "Service connection establishment failed,iConnResult=" + result);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }
}
