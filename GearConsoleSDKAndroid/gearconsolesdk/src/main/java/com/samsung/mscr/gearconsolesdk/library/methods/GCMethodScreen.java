package com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods;

import android.content.Context;

public class GCMethodScreen extends GCMethodAbstract<GCMethodScreen> {

    public static String SCREEN_NORMAL = "SCREEN_NORMAL";
    public static String SCREEN_OFF = "SCREEN_OFF";
    public static String SCREEN_DIM = "SCREEN_DIM";

    public GCMethodScreen(Context context) {
        super(context);
        this.mMethodName = "screen";
    }

    public GCMethodScreen setState(String state) {
        mParams.put("state", state);
        return this;
    }

    public GCMethodScreen setTurnedOff() {
        mParams.put("turnOff", true);
        return this;
    }
}