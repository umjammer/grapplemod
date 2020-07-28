
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
//* // 1.8 Compatability
import com.yyon.grapplinghook.items.KeypressItem;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */


public class KeypressMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "keypress_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    KeypressItem.Keys key;
    boolean isDown;

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
        this.key = thekey;
        this.isDown = isDown;
    }

    KeypressMessage(PacketByteBuf buf) {
        this.key = KeypressItem.Keys.values()[buf.readInt()];
        this.isDown = buf.readBoolean();
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.key.ordinal());
        buf.writeBoolean(this.isDown);
        return buf;
    }

    public static void run(ServerPlayerEntity player, PacketByteBuf buf) {
        KeypressMessage message = new KeypressMessage(buf);

        GrappleMod.receiveKeypress(player, message.key, message.isDown);
    }
}
