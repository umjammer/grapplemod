
package com.yyon.grapplinghook.network;

import java.util.HashSet;
import java.util.Set;

import com.yyon.grapplinghook.GrappleMod;
//* // 1.8 Compatability

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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


public class GrappleEndMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "grapple_end_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int entityid;
    public Set<Integer> arrowIds;

    public GrappleEndMessage(int entityid, Set<Integer> arrowIds) {
        this.entityid = entityid;
        this.arrowIds = arrowIds;
    }

    GrappleEndMessage(PacketByteBuf buf) {
        this.entityid = buf.readInt();
        int size = buf.readInt();
        this.arrowIds = new HashSet<>();
        for (int i = 0; i < size; i++) {
            this.arrowIds.add(buf.readInt());
        }
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.entityid);
        buf.writeInt(this.arrowIds.size());
        for (int id : this.arrowIds) {
            buf.writeInt(id);
        }
        return buf;
    }

    public static void run(ServerPlayerEntity player, PacketByteBuf buf) {
        GrappleEndMessage message = new GrappleEndMessage(buf);

        int id = message.entityid;
        World w = player.world;
        GrappleMod.receiveGrappleEnd(id, w, message.arrowIds);
    }
}
