package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner {
	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_SITTING;

	private final SimpleInventory inventory = new SimpleInventory(15);

	protected LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		this.dataTracker.startTracking(LittleMaidEntity.OWNER, Optional.empty());
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, false);
	}

	@Override
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@Nullable
	@Override
	public UUID getOwnerUuid() {
		return this.dataTracker.get(LittleMaidEntity.OWNER).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uuid) {
		this.dataTracker.set(LittleMaidEntity.OWNER, Optional.ofNullable(uuid));
	}

	@Nullable
	@Override
	public Entity getOwner() {
		UUID uuid = this.getOwnerUuid();

		if (uuid == null) return null;

		return this.world.getPlayerByUuid(uuid);
	}

	public void setOwner(PlayerEntity player) {
		this.setOwnerUuid(player.getUuid());
	}

	private boolean isTamed() {
		return this.getOwner() != null;
	}

	private boolean isSitting() {
		return this.dataTracker.get(LittleMaidEntity.IS_SITTING);
	}

	private void setSitting(boolean sitting) {
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, sitting);
	}

	private static final String KEY_OWNER = "Owner";
	private static final String KEY_IS_SITTING = "IsSitting";

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);

		UUID uuid = this.getOwnerUuid();
		if (uuid != null) {
			nbt.putUuid(LittleMaidEntity.KEY_OWNER, uuid);
		}

		nbt.putBoolean(LittleMaidEntity.KEY_IS_SITTING, this.isSitting());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		if (nbt.contains(LittleMaidEntity.KEY_OWNER)) {
			this.setOwnerUuid(nbt.getUuid(LittleMaidEntity.KEY_OWNER));
		}

		this.setSitting(nbt.getBoolean(LittleMaidEntity.KEY_IS_SITTING));
	}

	static {
		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	}

	public double getIntimacy() {
		return 0;
	}
}
