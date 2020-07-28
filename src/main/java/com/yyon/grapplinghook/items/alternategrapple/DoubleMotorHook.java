
package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.GrappleBow;


public class DoubleMotorHook extends GrappleBow {

    @Override
    public GrappleCustomization getDefaultCustomization() {
        GrappleCustomization custom = new GrappleCustomization();
        custom.doublehook = true;
        custom.motor = true;
        custom.motormaxspeed = 10;
        custom.reelin = false;
        custom.sticky = true;

        custom.hookGravity = 100;
        custom.verticalThrowAngle = 30;
        custom.sneakingVerticalThrowAngle = 25;
        custom.reelin = false;

        custom.motorwhencrouching = true;

        custom.smartdoublemotor = true;
//        custom.smartmotor = true;

        custom.angle = 25;
        custom.sneakingAngle = 0;

        custom.maxlen = GrappleConfig.options.upgraded_maxlen;
        custom.throwSpeed = 20;

        custom.playermovementmult = 2;

        return custom;
    }
}
