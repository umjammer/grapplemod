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

package com.yyon.grapplinghook.network;

import java.util.HashSet;
import java.util.Set;

import com.yyon.grapplinghook.server.ServerControllerManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.common.CommonSetup.serverControllerManager;
import static com.yyon.grapplinghook.grapplemod.MODID;


public class GrappleEndMessage implements BaseMessageServer {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "grapple_end");

	@Override
	public Identifier getIdentifier() {
		return IDENTIFIER;
	}

	public int entityId;
	public Set<Integer> hookEntityIds;

    public GrappleEndMessage(PacketByteBuf buf)	{
		this.entityId = buf.readInt();
		int size = buf.readInt();
		this.hookEntityIds = new HashSet<Integer>();
		for (int i = 0; i < size; i++) {
			this.hookEntityIds.add(buf.readInt());
		}
    }

    public GrappleEndMessage(int entityId, Set<Integer> hookEntityIds) {
    	this.entityId = entityId;
    	this.hookEntityIds = hookEntityIds;
    }

	public PacketByteBuf toPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.entityId);
    	buf.writeInt(this.hookEntityIds.size());
    	for (int id : this.hookEntityIds) {
        	buf.writeInt(id);
    	}
		return buf;
    }

    public void processMessage(ServerPlayerEntity player) {
		int id = this.entityId;

		if (player == null) {
			return;
		}
		World w = player.world;

		serverControllerManager.receiveGrappleEnd(id, w, this.hookEntityIds);
    }
}
