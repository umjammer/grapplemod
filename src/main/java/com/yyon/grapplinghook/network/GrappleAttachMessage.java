
package com.yyon.grapplinghook.network;

import java.util.LinkedList;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.GrappleArrow;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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


public class GrappleAttachMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "grapple_attach_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;
    public double x;
    public double y;
    public double z;
    public int controlid;
    public int entityid;
    public BlockPos blockpos;
    public LinkedList<Vec> segments;
    public LinkedList<Direction> segmenttopsides;
    public LinkedList<Direction> segmentbottomsides;
    public GrappleCustomization custom;

    public GrappleAttachMessage(int id,
            double x,
            double y,
            double z,
            int controlid,
            int entityid,
            BlockPos blockpos,
            LinkedList<Vec> segments,
            LinkedList<Direction> segmenttopsides,
            LinkedList<Direction> segmentbottomsides,
            GrappleCustomization custom) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlid = controlid;
        this.entityid = entityid;
        this.blockpos = blockpos;
        this.segments = segments;
        this.segmenttopsides = segmenttopsides;
        this.segmentbottomsides = segmentbottomsides;
        this.custom = custom;
    }

    GrappleAttachMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlid = buf.readInt();
        this.entityid = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        this.blockpos = new BlockPos(blockx, blocky, blockz);

        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);

        int size = buf.readInt();
        this.segments = new LinkedList<>();
        this.segmentbottomsides = new LinkedList<>();
        this.segmenttopsides = new LinkedList<>();

        segments.add(new Vec(0, 0, 0));
        segmentbottomsides.add(null);
        segmenttopsides.add(null);

        for (int i = 1; i < size - 1; i++) {
            this.segments.add(new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
            this.segmentbottomsides.add(Direction.values()[buf.readInt()]);
            this.segmenttopsides.add(Direction.values()[buf.readInt()]);
        }

        segments.add(new Vec(0, 0, 0));
        segmentbottomsides.add(null);
        segmenttopsides.add(null);
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlid);
        buf.writeInt(this.entityid);
        buf.writeInt(this.blockpos.getX());
        buf.writeInt(this.blockpos.getY());
        buf.writeInt(this.blockpos.getZ());

        this.custom.writeToBuf(buf);

        buf.writeInt(this.segments.size());
        for (int i = 1; i < this.segments.size() - 1; i++) {
            buf.writeDouble(this.segments.get(i).x);
            buf.writeDouble(this.segments.get(i).y);
            buf.writeDouble(this.segments.get(i).z);
            buf.writeInt(this.segmentbottomsides.get(i).ordinal());
            buf.writeInt(this.segmenttopsides.get(i).ordinal());
        }

        return buf;
    }

    public static void run(PlayerEntity player, PacketByteBuf packetByteBuf) {
        GrappleAttachMessage message = new GrappleAttachMessage(packetByteBuf);

        World world = player.world;
        Entity grapple = world.getEntityById(message.id);
        if (grapple instanceof GrappleArrow) {
            ((GrappleArrow) grapple).clientAttach(message.x, message.y, message.z);
            SegmentHandler segmenthandler = ((GrappleArrow) grapple).segmenthandler;
            segmenthandler.segments = message.segments;
            segmenthandler.segmentbottomsides = message.segmentbottomsides;
            segmenthandler.segmenttopsides = message.segmenttopsides;

            segmenthandler.forceSetPos(new Vec(message.x, message.y, message.z), Vec.positionVec(player));
        } else {
        }

        GrappleMod.createControl(message.controlid,
                                 message.id,
                                 message.entityid,
                                 world,
                                 new Vec(message.x, message.y, message.z),
                                 message.blockpos,
                                 message.custom);
    }
}
