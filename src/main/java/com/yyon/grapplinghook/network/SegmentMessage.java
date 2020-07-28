
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.GrappleArrow;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

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


public class SegmentMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "segment_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;
    public boolean add;
    public int index;
    public Vec pos;
    public Direction topfacing;
    public Direction bottomfacing;

    DimensionType dimensionType;
    double x;
    double y;
    double z;
    int i;

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing,
                          DimensionType dimensionType, double x, double y, double z, int i) {
        this.id = id;
        this.add = add;
        this.index = index;
        this.pos = pos;
        this.topfacing = topfacing;
        this.bottomfacing = bottomfacing;

        this.dimensionType = dimensionType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.i = i;
    }

    SegmentMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.add = buf.readBoolean();
        this.index = buf.readInt();
        this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.topfacing = Direction.values()[buf.readInt()];
        this.bottomfacing = Direction.values()[buf.readInt()];

        // TODO
        this.dimensionType = DimensionType.fromTag(buf.readCompoundTag());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.i = buf.readInt();
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.id);
        buf.writeBoolean(this.add);
        buf.writeInt(this.index);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeInt(this.topfacing.ordinal());
        buf.writeInt(this.bottomfacing.ordinal());

        // TODO
        buf.writeCompoundTag(this.dimensionType.toTag());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(this.i);

        return buf;
    }

    public static void run(PlayerEntity player, PacketByteBuf buf) {
        SegmentMessage message = new SegmentMessage(buf);

        World world = player.world;
        Entity grapple = world.getEntityById(message.id);
        if (grapple == null) {
            return;
        }

        if (grapple instanceof GrappleArrow) {
            SegmentHandler segmenthandler = ((GrappleArrow) grapple).segmenthandler;
            if (message.add) {
                segmenthandler.actuallyaddsegment(message.index, message.pos, message.bottomfacing, message.topfacing);
            } else {
                segmenthandler.removesegment(message.index);
            }
        } else {
        }
    }

    // TODO
    public void sendToAllAround() {
        MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(getIdentifier(), toBytes()));
    }
}
