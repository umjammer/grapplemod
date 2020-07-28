
package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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


public class PlayerMovementMessage implements Packet {

    public static final Identifier IDENTIFIER = new Identifier(GrappleMod.MODID, "player_movement_message");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int entityId;
    public double x;
    public double y;
    public double z;
    public double mx;
    public double my;
    public double mz;

    public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
    }

    PlayerMovementMessage(PacketByteBuf buf) {
        try {
            this.entityId = buf.readInt();
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            this.mx = buf.readDouble();
            this.my = buf.readDouble();
            this.mz = buf.readDouble();
        } catch (Exception e) {
            System.out.print("Playermovement error: ");
            System.out.println(buf);
        }
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(mx);
        buf.writeDouble(my);
        buf.writeDouble(mz);
        return buf;
    }

    public static void run(ServerPlayerEntity player, PacketByteBuf buf) {
        PlayerMovementMessage message = new PlayerMovementMessage(buf);

        player.setPos(message.x, message.y, message.z);
        player.setVelocity(message.mx, message.my, message.mz);
//        player.networkHandler.connection.update();

        if (!player.isOnGround()) {
            if (message.my >= 0) {
                player.fallDistance = 0;
            } else {
                double gravity = 0.05 * 2;
                // d = v^2 / 2g
                player.fallDistance = (float) (Math.pow(message.my, 2) / (2 * gravity));
            }
        }
    }
}
