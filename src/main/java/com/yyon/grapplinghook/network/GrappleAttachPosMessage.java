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
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.grapplemod.LOGGER;
import static com.yyon.grapplinghook.grapplemod.MODID;

public class GrappleAttachPosMessage implements BaseMessageClient {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "grapple_attach_pos");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;
	public double x;
	public double y;
	public double z;

    public GrappleAttachPosMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
LOGGER.info("GrappleAttachPosMessage::<init>: grapple entity id: " + id);
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.id);
LOGGER.info("GrappleAttachPosMessage::toPacket: grapple entity id: " + id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        return buf;
    }

    public void processMessage() {
        World world = MinecraftClient.getInstance().world;
    	Entity entity = world.getEntityById(this.id); // TODO null
    	if (entity instanceof GrapplehookEntity grapple) {
        	grapple.setAttachPos(this.x, this.y, this.z);
    	} else {
LOGGER.warn("GrappleAttachPosMessage::processMessage: entity is not grapple: " + world.isClient + ", " + this.id + ", world: " + world.hashCode());
        }
    }
}
