
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.blocks.GrappleModifierBlockEntity;

import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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


public class GrappleModifierMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "grapple_modifier_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public BlockPos pos;
    public GrappleCustomization custom;

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
        this.pos = pos;
        this.custom = custom;
    }

    GrappleModifierMessage(PacketByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        this.custom.writeToBuf(buf);
        return buf;
    }

    public static void run(ServerPlayerEntity player, PacketByteBuf buf) {
        GrappleModifierMessage message = new GrappleModifierMessage(buf);

        BlockEntity entity = player.world.getBlockEntity(message.pos);

        if (entity instanceof GrappleModifierBlockEntity) {
            ((GrappleModifierBlockEntity) entity).setCustomizationServer(message.custom);
        }
    }
}
