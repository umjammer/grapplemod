
package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.GrappleBow;


public class SmartHook extends GrappleBow {

    @Override
    public GrappleCustomization getDefaultCustomization() {
        GrappleCustomization custom = new GrappleCustomization();
        custom.motor = true;
        custom.smartmotor = true;

        custom.maxlen = GrappleConfig.options.upgraded_maxlen;
        custom.throwSpeed = GrappleConfig.options.upgraded_throwspeed;

        custom.playermovementmult = 2;

        return custom;
    }
}
