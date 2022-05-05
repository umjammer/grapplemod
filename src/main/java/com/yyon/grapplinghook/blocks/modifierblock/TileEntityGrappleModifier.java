package com.yyon.grapplinghook.blocks.modifierblock;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrappleCustomization.upgradeCategories;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class TileEntityGrappleModifier extends BlockEntity {
	public Map<upgradeCategories, Boolean> unlockedCategories = new HashMap<>();
	public GrappleCustomization customization;

	public TileEntityGrappleModifier(BlockPos pos, BlockState state) {
		super(CommonSetup.grappleModifierTileEntityType, pos, state);
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.markDirty();
		this.getWorld().onBlockChanged(this.getPos(), this.getCachedState(), this.getCachedState());
	}

	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		new GrappleModifierMessage(this.getPos(), this.customization).send();
		this.markDirty();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.markDirty();
	}

	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}

	@Override
	public void writeNbt(NbtCompound nbtTagCompound) {
		super.writeNbt(nbtTagCompound);

		NbtCompound unlockedNBT = nbtTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);

			unlockedNBT.putBoolean(num, unlocked);
		}

		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());
	}

	@Override
	public void readNbt(NbtCompound parentNBTTagCompound) {
		super.readNbt(parentNBTTagCompound); // The super call is required to load the tiles location

		NbtCompound unlockedNBT = parentNBTTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);

			this.unlockedCategories.put(category, unlocked);
		}

		NbtCompound custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
	}

	// When the world loads from disk, the server needs to send the TileEntity information to the client
	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	//  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
	//  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
	//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
	@Override
	@Nullable
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		int tileEntityType = 42;  // arbitrary number; only used for vanilla TileEntities.  You can use it, or not, as you want.
		return BlockEntityUpdateS2CPacket.create(this);
	}

	/**
	 * Creates a tag containing all the TileEntity information, used by vanilla to transmit from server to client
	 */
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}
}
