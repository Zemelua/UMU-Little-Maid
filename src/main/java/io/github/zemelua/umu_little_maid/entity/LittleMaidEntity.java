package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.goal.MaidAvoidGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidFollowGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidPounceGoal;
import io.github.zemelua.umu_little_maid.entity.goal.MaidSitGoal;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner, RangedAttackMob {
	public static final EquipmentSlot[] ARMORS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.FEET};

	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_TAMED;
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
		this.dataTracker.startTracking(LittleMaidEntity.IS_TAMED, false);
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
		this.goalSelector.add(4, this.new JobWrapperGoal(new MaidPounceGoal(this), ModEntities.FENCER));
		this.goalSelector.add(5, this.new JobWrapperGoal(new MeleeAttackGoal(this, 1.0D, true), ModEntities.FENCER));
		this.goalSelector.add(5, this.new JobWrapperGoal(new MeleeAttackGoal(this, 0.7D, true), ModEntities.CRACKER));
		this.goalSelector.add(5, this.new JobWrapperGoal(this.new MaidBowGoal(), ModEntities.BOW));
		this.goalSelector.add(6, new MaidFollowGoal(this));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));

		this.targetSelector.add(0, this.new JobWrapperGoal(
				new PersonalityWrapperGoal(
						new ActiveTargetGoal<>(this, MobEntity.class, false, living
								-> !living.getType().getSpawnGroup().isPeaceful() && living.getType() != EntityType.CREEPER
						),
						ModEntities.BRAVERY, ModEntities.TSUNDERE),
				ModEntities.FENCER, ModEntities.CRACKER, ModEntities.BOW)
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

		MaidJob lastJob = this.getJob();
		boolean applied = false;

		for (MaidJob job : ModRegistries.MAID_JOB.stream().toList()) {
			if (!applied && job.canApply(this)) {
				this.setJob(job);
				if (lastJob != job) {
					this.setTarget(null);
				}
				applied = true;
			}
		}

		if (!applied) {
			this.setJob(ModEntities.NONE);
			this.setTarget(null);
		}

		UMULittleMaid.LOGGER.info(this.isSitting());
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

		if (this.isTamed() && player == this.getOwner()) {
			if (player.isSneaking()) {
				if (!this.world.isClient()) {
					player.openHandledScreen(new LittleMaidScreenHandlerFactory(this));
				}
			} else {
				if (!defaultResult.isAccepted()) {
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
					this.setTamed(true);
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
	public void attack(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getArrowType(this.getMainHandStack());
		if (itemStack.isEmpty()) return;

		PersistentProjectileEntity projectile = ProjectileUtil.createArrowProjectile(this, itemStack, pullProgress);
		double xVelocity = target.getX() - this.getX();
		double yVelocity = target.getBodyY(1.0D / 3.0D) - projectile.getY();
		double zVelocity = target.getZ() - this.getZ();
		double power = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
		projectile.setVelocity(xVelocity, yVelocity + power * 0.2D, zVelocity, 1.6F, 14 - this.world.getDifficulty().getId() * 4);
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(projectile);
	}

	@Override
	public ItemStack getArrowType(ItemStack itemStack) {
		if (itemStack.getItem() instanceof RangedWeaponItem rangedItem) {
			Predicate<ItemStack> projectiles = rangedItem.getHeldProjectiles();
			ItemStack heldProjectile = RangedWeaponItem.getHeldProjectile(this, projectiles);
			if (!heldProjectile.isEmpty()) return heldProjectile;

			projectiles = rangedItem.getProjectiles();
			for (int i = 0; i < this.inventory.size(); i++) {
				ItemStack inventoryStack = this.inventory.getStack(i);
				if (projectiles.test(inventoryStack)) {
					return inventoryStack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
			DefaultParticleType particleEffect = ParticleTypes.HEART;
			for (int i = 0; i < 7; ++i) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		return !this.isSitting() && super.canTarget(type);
	}

	@Override
	public boolean canTarget(LivingEntity living) {
		return !this.isSitting() && super.canTarget(living);
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
		return this.dataTracker.get(LittleMaidEntity.IS_TAMED);
	}

	public void setTamed(boolean tamed) {
		this.dataTracker.set(LittleMaidEntity.IS_TAMED, tamed);
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
			this.setTamed(true);
		} else {
			this.setTamed(false);
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

	private class MaidBowGoal extends Goal {
		private static final double RANGE = 15.0D;
		private static final double SPEED = 1.0D;
		private static final int INTERVAL = 20;

		private int targetSeeingTicker;
		private int coolDown = -1;
		private int combatTicks = -1;
		private boolean movingToLeft;
		private boolean backward;

		private MaidBowGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return LittleMaidEntity.this.getTarget() != null
					&& !LittleMaidEntity.this.getArrowType(LittleMaidEntity.this.getMainHandStack()).isEmpty();
		}

		@Override
		public boolean shouldContinue() {
			return (this.canStart() || !LittleMaidEntity.this.getNavigation().isIdle())
					&& !LittleMaidEntity.this.getArrowType(LittleMaidEntity.this.getMainHandStack()).isEmpty();
		}

		@Override
		public void start() {
			LittleMaidEntity.this.setAttacking(true);
		}

		@Override
		public void stop() {
			LittleMaidEntity.this.setAttacking(false);
			this.targetSeeingTicker = 0;
			this.coolDown = -1;
			LittleMaidEntity.this.clearActiveItem();
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity target = LittleMaidEntity.this.getTarget();
			if (target == null) return;

			double distance = LittleMaidEntity.this.distanceTo(target);
			boolean canSeeTarget = LittleMaidEntity.this.getVisibilityCache().canSee(target);
			boolean isSeeingTarget = this.targetSeeingTicker > 0;

			if (canSeeTarget != isSeeingTarget) this.targetSeeingTicker = 0;
			if (canSeeTarget) this.targetSeeingTicker++;
			else              this.targetSeeingTicker--;

			if (distance > MaidBowGoal.RANGE || this.targetSeeingTicker < 20) {
				LittleMaidEntity.this.getNavigation().startMovingTo(target, MaidBowGoal.SPEED);
				this.combatTicks = -1;
			} else {
				LittleMaidEntity.this.getNavigation().stop();
				this.combatTicks++;
			}

			if (this.combatTicks >= 20) {
				if (LittleMaidEntity.this.getRandom().nextDouble() < 0.3D) {
					this.movingToLeft = !this.movingToLeft;
				}
				if (LittleMaidEntity.this.getRandom().nextDouble() < 0.3D) {
					this.backward = !this.backward;
				}

				this.combatTicks = 0;
			}

			if (this.combatTicks > -1) {
				if      (distance > MaidBowGoal.RANGE * Math.sqrt(0.75D)) this.backward = false;
				else if (distance < MaidBowGoal.RANGE * Math.sqrt(0.25D)) this.backward = true;

				LittleMaidEntity.this.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
				LittleMaidEntity.this.lookAtEntity(target, 30.0F, 30.0F);
			} else {
				LittleMaidEntity.this.getLookControl().lookAt(target, 30.0F, 30.0F);
			}

			if (LittleMaidEntity.this.isUsingItem()) {
				int itemUseTime = LittleMaidEntity.this.getItemUseTime();

				if (!canSeeTarget && this.targetSeeingTicker < -60) {
					LittleMaidEntity.this.clearActiveItem();
				} else if (canSeeTarget && itemUseTime >= 20) {
					LittleMaidEntity.this.clearActiveItem();
					LittleMaidEntity.this.attack(target, BowItem.getPullProgress(itemUseTime));
					this.coolDown = MaidBowGoal.INTERVAL;
				}
			} else if (--this.coolDown <= 0 && this.targetSeeingTicker >= -60) {
				LittleMaidEntity.this.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(LittleMaidEntity.this, Items.BOW));
			}
		}
	}

	static {
		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_TAMED = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		JOB = DataTracker.registerData(LittleMaidEntity.class, ModEntities.JOB_HANDLER);
	}
}
