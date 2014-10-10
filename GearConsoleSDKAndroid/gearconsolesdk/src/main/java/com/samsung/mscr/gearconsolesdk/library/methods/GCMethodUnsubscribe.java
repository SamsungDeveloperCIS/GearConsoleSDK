package com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods;

import android.content.Context;

import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.subscribes.GCSubscribeDevicemotion;

public class GCMethodUnsubscribe extends GCMethodAbstract<GCSubscribeDevicemotion> {

    public GCMethodUnsubscribe(Context context) {
        super(context);
        this.mMethodName = "unsubscribe";
    }

    public GCMethodUnsubscribe setName(String name) {
        mParams.put("name", name);
        return this;
    }
}