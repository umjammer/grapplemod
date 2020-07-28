
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.entities.GrappleArrow;
//* // 1.8 Compatability

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

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


public class GrappleAttachPosMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "grapple_attach_pos_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;
    public double x;
    public double y;
    public double z;

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    GrappleAttachPosMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        return buf;
    }

    public static void run(PlayerEntity player, PacketByteBuf buf) {
        GrappleAttachPosMessage message = new GrappleAttachPosMessage(buf);
        World world = player.world;
        Entity grapple = world.getEntityById(message.id);
        if (grapple instanceof GrappleArrow) {
            ((GrappleArrow) grapple).setAttachPos(message.x, message.y, message.z);
        }
    }
}
