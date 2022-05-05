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

import com.yyon.grapplinghook.items.KeypressItem;
import io.netty.buffer.Unpooled;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import static com.yyon.grapplinghook.grapplemod.MODID;


public class KeypressMessage implements BaseMessageServer {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "key_press");

	@Override
	public Identifier getIdentifier() {
		return IDENTIFIER;
	}

	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage(PacketByteBuf buf) {
		this.key = KeypressItem.Keys.values()[buf.readInt()];
		this.isDown = buf.readBoolean();
    }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

	public PacketByteBuf toPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.key.ordinal());
    	buf.writeBoolean(this.isDown);
		return buf;
    }

    public void processMessage(final ServerPlayerEntity player) {

		if (player != null) {
			ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof KeypressItem) {
					if (isDown) {
						((KeypressItem)item).onCustomKeyDown(stack, player, key, true);
					} else {
						((KeypressItem)item).onCustomKeyUp(stack, player, key, true);
					}
					return;
				}
			}

			stack = player.getStackInHand(Hand.OFF_HAND);
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof KeypressItem) {
					if (isDown) {
						((KeypressItem)item).onCustomKeyDown(stack, player, key, false);
					} else {
						((KeypressItem)item).onCustomKeyUp(stack, player, key, false);
					}
				}
			}
		}
	}
}
