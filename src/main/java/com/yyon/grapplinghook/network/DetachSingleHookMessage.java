/*
 * This file is part of GrappleMod.
 *
 *  GrappleMod is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GrappleMod is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class DetachSingleHookMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "detach_single_hook_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    int id;
    int hookid;

    public DetachSingleHookMessage(int id, int hookid) {
        this.id = id;
        this.hookid = hookid;
    }

    DetachSingleHookMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.hookid = buf.readInt();
    }

    @Override
    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeInt(hookid);
        return buf;
    }

    public static void run(PlayerEntity player, PacketByteBuf buf) {
        DetachSingleHookMessage message = new DetachSingleHookMessage(buf);

        GrappleMod.receiveGrappleDetachHook(message.id, message.hookid);
    }
}
