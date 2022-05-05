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

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entities.grapplehook.SegmentHandler;
import com.yyon.grapplinghook.utils.Vec;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.grapplemod.MODID;


public class SegmentMessage implements BaseMessageClient {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "segment");

	@Override
	public Identifier getIdentifier() {
		return IDENTIFIER;
	}

    public int id;
	public boolean add;
	public int index;
	public Vec pos;
	public Direction topFacing;
	public Direction bottomFacing;

    public SegmentMessage(PacketByteBuf buf) {
		this.id = buf.readInt();
		this.add = buf.readBoolean();
		this.index = buf.readInt();
		this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
		this.topFacing = Direction.byId(buf.readInt());
		this.bottomFacing = Direction.byId(buf.readInt());
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topFacing = topfacing;
    	this.bottomFacing = bottomfacing;
    }

    public PacketByteBuf toPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeInt(this.topFacing.getId());
    	buf.writeInt(this.bottomFacing.getId());
		return buf;
    }

    public void processMessage() {
    	World world = MinecraftClient.getInstance().world;
    	Entity grapple = world.getEntityById(this.id);
    	if (grapple == null) {
    		return;
    	}

    	if (grapple instanceof GrapplehookEntity) {
    		SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmentHandler;
    		if (this.add) {
    			segmenthandler.actuallyAddSegment(this.index, this.pos, this.bottomFacing, this.topFacing);
    		} else {
    			segmenthandler.removeSegment(this.index);
    		}
    	}
	}
}
