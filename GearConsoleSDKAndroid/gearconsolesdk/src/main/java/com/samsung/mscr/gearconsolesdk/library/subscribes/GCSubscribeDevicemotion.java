package com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.subscribes;

import android.content.Context;

import com.samsung.mscr.gearconsolesdk.library.com.samsung.mscr.gearconsolesdk.library.methods.GCMethodAbstract;

public class GCSubscribeDevicemotion extends GCMethodAbstract<GCSubscribeDevicemotion> {

    public GCSubscribeDevicemotion(Context context) {
        super(context);
        setName("devicemotion");
    }

    public GCSubscribeDevicemotion setName(String name) {
        mParams.put("name", name);
        return this;
    }
}