
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
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


public class GrappleDetachMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "grapple_detach_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;

    public GrappleDetachMessage(int id) {
        this.id = id;
    }

    GrappleDetachMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.id);
        return buf;
    }

    public static void run(PlayerEntity player, PacketByteBuf buf) {
        GrappleDetachMessage message = new GrappleDetachMessage(buf);
        GrappleMod.receiveGrappleDetach(message.id);
    }
}
