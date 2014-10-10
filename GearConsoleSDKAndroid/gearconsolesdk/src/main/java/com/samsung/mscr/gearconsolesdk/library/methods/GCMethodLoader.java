package com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods;

import android.content.Context;

public class GCMethodLoader extends GCMethodAbstract {

    public GCMethodLoader(Context context) {
        super(context);
        this.mMethodName = "loader";
        mParams.put("show", true);
    }

    public GCMethodLoader setShowing(Boolean state) {
        mParams.put("show", state);
        return this;
    }

    public GCMethodLoader setText(String text) {
        mParams.put("text", text);
        return this;
    }
}