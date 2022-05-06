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

import com.yyon.grapplinghook.client.ClientControllerManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;
import static com.yyon.grapplinghook.grapplemod.MODID;

public class DetachSingleHookMessage implements BaseMessageClient {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "detach_single_hook");

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
    }

    public int id;
	public int hookid;

    public DetachSingleHookMessage(PacketByteBuf buf) {
        this.id = buf.readInt();
        this.hookid = buf.readInt();
    }

    public DetachSingleHookMessage(int id, int hookid) {
    	this.id = id;
    	this.hookid = hookid;
    }

    public PacketByteBuf toPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.id);
    	buf.writeInt(this.hookid);
        return buf;
    }
    
    public void processMessage() {
    	clientControllerManager.receiveGrappleDetachHook(this.id, this.hookid);
    }
}
