package com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.subscribes;

import android.content.Context;

import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodAbstract;

public abstract class GCSubscribeAbstract extends GCMethodAbstract<GCSubscribeAbstract> {

    public GCSubscribeAbstract(Context context) {
        super(context);
        this.mMethodName = "subscribe";
    }

    public GCSubscribeAbstract setName(String name) {
        mParams.put("name", name);
        return this;
    }
}