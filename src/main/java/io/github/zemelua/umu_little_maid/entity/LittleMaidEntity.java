package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.entity.goal.MaidAvoidGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidFollowGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidPounceGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidSitGoal;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner {
	public static final EquipmentSlot[] ARMORS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.FEET};

	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_SITTING;
	private static final TrackedData<MaidPersonality> PERSONALITY;
	private static final TrackedData<MaidJob> JOB;

	private final SimpleInventory inventory = new SimpleInventory(15);

	protected LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		this.dataTracker.startTracking(LittleMaidEntity.OWNER, Optional.empty());
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, false);
		this.dataTracker.startTracking(LittleMaidEntity.PERSONALITY, ModEntities.BRAVERY);
		this.dataTracker.startTracking(LittleMaidEntity.JOB, ModEntities.NONE);
	}

	@Override
	protected void initGoals() {
		super.initGoals();

		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new MaidSitGoal(this));
		this.goalSelector.add(3, new MaidAvoidGoal(this));
		this.goalSelector.add(4, new MaidPounceGoal(this));
		this.goalSelector.add(5, this.new JobWrapperGoal(new MeleeAttackGoal(this, 1.0D, true), ModEntities.FENCER));
		this.goalSelector.add(5, this.new JobWrapperGoal(new MeleeAttackGoal(this, 0.4D, true), ModEntities.CRACKER));
		this.goalSelector.add(6, new MaidFollowGoal(this));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));

		this.targetSelector.add(0, this.new JobWrapperGoal(
				new PersonalityWrapperGoal(
						new ActiveTargetGoal<>(this, MobEntity.class, false, living
								-> !living.getType().getSpawnGroup().isPeaceful()),
						ModEntities.BRAVERY, ModEntities.TSUNDERE),
				ModEntities.FENCER, ModEntities.CRACKER)
		);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D);
	}

	@Override
	public void tick() {
		super.tick();

		ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
		List<MaidJob> jobs = ModRegistries.MAID_JOB.stream().toList();
		boolean applied = false;
		for (MaidJob job : jobs) {
			if (!applied && job.canApply(itemStack)) {
				this.setJob(job);
				applied = true;
			}
		}
		if (!applied) this.setJob(ModEntities.NONE);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		this.tickHandSwing();
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack interactItem = player.getStackInHand(hand);
		ActionResult defaultResult = super.interactMob(player, hand);

		if (this.isTamed()) {
			if (player.isSneaking()) {
				if (!this.world.isClient()) {
					player.openHandledScreen(new LittleMaidScreenHandlerFactory(this));
				}
			} else {
				if (player == this.getOwner() && !defaultResult.isAccepted()) {
					if (!this.world.isClient()) {
						this.navigation.stop();
						this.setTarget(null);
						this.setSitting(!this.isSitting());
					}

					return ActionResult.success(this.world.isClient());
				}
			}

		} else {
			if (interactItem.isOf(Items.CAKE)) {
				if (!this.world.isClient()) {
					if (!player.getAbilities().creativeMode) {
						interactItem.decrement(1);
					}

					this.setOwner(player);
					this.getNavigation().stop();
					this.setTarget(null);

					this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
				}

				return ActionResult.success(this.world.isClient());
			}
		}

		return super.interactMob(player, hand);
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

	public boolean isTamed() {
		return this.getOwner() != null;
	}

	public boolean isSitting() {
		return this.dataTracker.get(LittleMaidEntity.IS_SITTING);
	}

	public void setSitting(boolean sitting) {
		this.dataTracker.set(LittleMaidEntity.IS_SITTING, sitting);
	}

	public MaidJob getJob() {
		return this.dataTracker.get(LittleMaidEntity.JOB);
	}

	private void setJob(MaidJob job) {
		this.dataTracker.set(LittleMaidEntity.JOB, job);
	}

	public MaidPersonality getPersonality() {
		return this.dataTracker.get(LittleMaidEntity.PERSONALITY);
	}

	public double getIntimacy() {
		return 0;
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

		UUID uuid;
		super.readCustomDataFromNbt(nbt);
		if (nbt.containsUuid(LittleMaidEntity.KEY_OWNER)) {
			uuid = nbt.getUuid(LittleMaidEntity.KEY_OWNER);
		} else {
			String string = nbt.getString(LittleMaidEntity.KEY_OWNER);
			uuid = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
		}
		if (uuid != null) {
			this.setOwnerUuid(uuid);
		}

		this.setSitting(nbt.getBoolean(LittleMaidEntity.KEY_IS_SITTING));
	}

	private class JobWrapperGoal extends Goal {
		private final MaidJob[] jobs;
		private final Goal goal;

		private JobWrapperGoal(Goal goal, MaidJob... jobs) {
			this.jobs = jobs;
			this.goal = goal;

			this.setControls(this.goal.getControls());
		}

		@Override
		public boolean canStart() {
			return Arrays.stream(this.jobs).anyMatch(job -> job == LittleMaidEntity.this.getJob())
					&& this.goal.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return Arrays.stream(this.jobs).anyMatch(job -> job == LittleMaidEntity.this.getJob())
					&& this.goal.shouldContinue();
		}

		@Override
		public boolean canStop() {
			return Arrays.stream(this.jobs).noneMatch(job -> job == LittleMaidEntity.this.getJob())
					&& this.goal.canStop();
		}

		@Override
		public void start() {
			this.goal.start();
		}

		@Override
		public void stop() {
			this.goal.stop();
		}

		@Override
		public boolean shouldRunEveryTick() {
			return this.goal.shouldRunEveryTick();
		}

		@Override
		public void tick() {
			this.goal.tick();
		}

		@Override
		public void setControls(EnumSet<Control> controls) {
			this.goal.setControls(controls);
		}

		@Override
		public String toString() {
			return this.goal.toString();
		}

		@Override
		public EnumSet<Control> getControls() {
			return this.goal.getControls();
		}

		@Override
		protected int getTickCount(int ticks) {
			return this.shouldRunEveryTick() ? ticks : Goal.toGoalTicks(ticks);
		}
	}

	private class PersonalityWrapperGoal extends Goal {
		private final MaidPersonality[] personalities;
		private final Goal goal;

		private PersonalityWrapperGoal(Goal goal, MaidPersonality... personalities) {
			this.personalities = personalities;
			this.goal = goal;

			this.setControls(this.goal.getControls());
		}

		@Override
		public boolean canStart() {
			return Arrays.stream(this.personalities).anyMatch(personality -> personality == LittleMaidEntity.this.getPersonality())
					&& this.goal.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return Arrays.stream(this.personalities).anyMatch(personality -> personality == LittleMaidEntity.this.getPersonality())
					&& this.goal.shouldContinue();
		}

		@Override
		public boolean canStop() {
			return Arrays.stream(this.personalities).noneMatch(personality -> personality == LittleMaidEntity.this.getPersonality())
					&& this.goal.canStop();
		}

		@Override
		public void start() {
			this.goal.start();
		}

		@Override
		public void stop() {
			this.goal.stop();
		}

		@Override
		public boolean shouldRunEveryTick() {
			return this.goal.shouldRunEveryTick();
		}

		@Override
		public void tick() {
			this.goal.tick();
		}

		@Override
		public void setControls(EnumSet<Control> controls) {
			this.goal.setControls(controls);
		}

		@Override
		public String toString() {
			return this.goal.toString();
		}

		@Override
		public EnumSet<Control> getControls() {
			return this.goal.getControls();
		}

		@Override
		protected int getTickCount(int ticks) {
			return this.shouldRunEveryTick() ? ticks : Goal.toGoalTicks(ticks);
		}
	}

	static {
		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		JOB = DataTracker.registerData(LittleMaidEntity.class, ModEntities.JOB_HANDLER);
	}
}
