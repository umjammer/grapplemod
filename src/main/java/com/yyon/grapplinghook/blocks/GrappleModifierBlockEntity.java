
package com.yyon.grapplinghook.blocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.GrappleMod.UpgradeCategories;
import com.yyon.grapplinghook.network.GrappleModifierMessage;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;


public class GrappleModifierBlockEntity extends BlockEntity {

    public Map<GrappleMod.UpgradeCategories, Boolean> unlockedCategories = new HashMap<>();

    public GrappleCustomization customization;

    public GrappleModifierBlockEntity() {
        super(BlockEntityType.COMMAND_BLOCK);
        this.customization = new GrappleCustomization();
    }

    public void unlockCategory(UpgradeCategories category) {
        unlockedCategories.put(category, true);
        this.sendUpdates();
    }

    public void setCustomizationClient(GrappleCustomization customization) {
        this.customization = customization;
        new GrappleModifierMessage(this.getPos(), this.customization).send();
        this.sendUpdates();
    }

    public void setCustomizationServer(GrappleCustomization customization) {
        this.customization = customization;
        this.sendUpdates();
    }

    private void sendUpdates() {
//		this.world.markBlockRangeForRenderUpdate(pos, pos);
        this.world
                .onBlockChanged(getPos(),
                                this.world.getBlockState(this.getPos()),
                                this.world.getBlockState(this.getPos())/* , 3 */);
//		this.world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
        markDirty();
    }

    public boolean isUnlocked(UpgradeCategories category) {
        return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
    }

    // https://github.com/TheGreyGhost/MinecraftByExample/blob/master/src/main/java/minecraftbyexample/mbe20_tileentity_data/TileEntityData.java

    // When the world loads from disk, the server needs to send the TileEntity
    // information to the client
    // it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and
    // handleUpdateTag() to do this:
    // getUpdatePacket() and onDataPacket() are used for one-at-a-time
    // TileEntity
    // updates
    // getUpdateTag() and handleUpdateTag() are used by vanilla to collate
    // together
    // into a single chunk update packet
    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        CompoundTag compoundTag = new CompoundTag();
        toTag(compoundTag);
        int metadata = 1;
        return new BlockEntityUpdateS2CPacket(this.getPos(), metadata, compoundTag);
    }

//	@Override
    public void onDataPacket(/* NetworkManager net, SPacketUpdateTileEntity pkt */) {
        fromTag(pkt.getNbtCompound());
    }

    /*
     * Creates a tag containing the TileEntity information, used by vanilla to
     * transmit from server to client
     */
//	@Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        toTag(compoundTag);
        return compoundTag;
    }

    /*
     * Populates this TileEntity with information from the tag, used by vanilla
     * to transmit from server to client
     */
//	@Override
    public void handleUpdateTag(CompoundTag tag) {
        fromTag(null, tag);
    }

    // This is where you save any data that you don't want to lose when the tile
    // entity unloads
    // In this case, we only need to store the ticks left until explosion, but
    // we
    // store a bunch of other
    // data as well to serve as an example.
    // NBTexplorer is a very useful tool to examine the structure of your NBT
    // saved
    // data and make sure it's correct:
    // http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1262665-nbtexplorer-nbt-editor-for-windows-and-mac
    @Override
    public CompoundTag toTag(CompoundTag parentTag) {
        super.toTag(parentTag); // The super call is required to save the tiles location

        CompoundTag unlockedTag = parentTag.getCompound("unlocked");

        for (GrappleMod.UpgradeCategories category : GrappleMod.UpgradeCategories.values()) {
            String num = String.valueOf(category.ordinal());
            boolean unlocked = this.isUnlocked(category);

            unlockedTag.putBoolean(num, unlocked);
        }

        parentTag.put("unlocked", unlockedTag);
        parentTag.put("customization", this.customization.toCompoundTag());

        return parentTag;
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    public void fromTag(BlockState blockState, CompoundTag parentTag) {
        super.fromTag(blockState, parentTag); // The super call is required to load the tiles location

        CompoundTag unlockedTag = parentTag.getCompound("unlocked");

        for (GrappleMod.UpgradeCategories category : GrappleMod.UpgradeCategories.values()) {
            String num = String.valueOf(category.ordinal());
            boolean unlocked = unlockedTag.getBoolean(num);

            this.unlockedCategories.put(category, unlocked);
        }

        CompoundTag customizationTag = parentTag.getCompound("customization");
        this.customization.loadNBT(customizationTag);
    }
}
