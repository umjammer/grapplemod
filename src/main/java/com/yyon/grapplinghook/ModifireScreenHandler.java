/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.yyon.grapplinghook;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;


/**
 * ModifireScreenHandler.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/07/17 umjammer initial version <br>
 */
class ModifireScreenHandler extends ScreenHandler {

    static Identifier ID = new Identifier(GrappleMod.MODID, ""); // TODO

    protected ModifireScreenHandler(ScreenHandlerType<?> type, int i) {
        super(type, i);
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return false;
    }
}

/* */
