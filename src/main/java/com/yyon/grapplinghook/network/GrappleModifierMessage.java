/*
    GrappleMod is free software: you can redistribute it and/or modify
    it under the teHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.grapplemod.MODID;


public class GrappleModifierMessage implements BaseMessageServer {

    public static final Identifier IDENTIFIER = new Identifier(MODID, "grapple_modifier");

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

	public GrappleModifierMessage(PacketByteBuf buf) {
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.custom = new GrappleCustomization();
		this.custom.readFromBuf(buf);
	}

	public PacketByteBuf toPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    	buf.writeInt(this.pos.getX());
    	buf.writeInt(this.pos.getY());
    	buf.writeInt(this.pos.getZ());
    	this.custom.writeToBuf(buf);
		return buf;
    }

    public void processMessage(World w) {
		BlockEntity ent = w.getBlockEntity(this.pos);

		if (ent instanceof TileEntityGrappleModifier) {
			((TileEntityGrappleModifier) ent).setCustomizationServer(this.custom);
		}
    }
}
