package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.entity.goal.*;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
		this.goalSelector.add(3, new MaidWrapperGoal.Builder(this, new MaidAvoidGoal(this))
				.addJob(ModEntities.NONE)
				.addPredicate(maid -> maid.getJob() == ModEntities.ARCHER && maid.getArrowType(maid.getMainHandStack()).isEmpty())
				.build());
		this.goalSelector.add(4, new MaidWrapperGoal.Builder(this, new MaidPounceGoal(this))
				.addJob(ModEntities.FENCER)
				.build());
		this.goalSelector.add(5, new MaidWrapperGoal.Builder(this, new MeleeAttackGoal(this, 1.0D, true))
				.addJob(ModEntities.FENCER)
				.build());
		this.goalSelector.add(5, new MaidWrapperGoal.Builder(this, new MeleeAttackGoal(this, 0.5D, true))
				.addJob(ModEntities.CRACKER)
				.build());
		this.goalSelector.add(5, new MaidWrapperGoal.Builder(this, new MaidBowGoal(this))
				.addJob(ModEntities.ARCHER)
				.build());
		this.goalSelector.add(6, new MaidFollowGoal(this));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));

		this.targetSelector.add(0, new MaidWrapperGoal.Builder(this, new ActiveTargetGoal<>(
				this, MobEntity.class, false, living
				-> !living.getType().getSpawnGroup().isPeaceful() && living.getType() != EntityType.CREEPER))
				.addJob(ModEntities.FENCER, ModEntities.CRACKER, ModEntities.ARCHER)
				.addPersonality(ModEntities.BRAVERY, ModEntities.TSUNDERE)
				.build());
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
		ItemStack itemStack = this.getMainHandStack();
		ItemStack arrow = this.getArrowType(itemStack);
		if (arrow.isEmpty()) return;

		boolean consumeArrow = ModUtils.hasEnchantment(Enchantments.INFINITY, itemStack);

		PersistentProjectileEntity projectile = ProjectileUtil.createArrowProjectile(this, arrow, pullProgress);
		double xVelocity = target.getX() - this.getX();
		double yVelocity = target.getBodyY(1.0D / 3.0D) - projectile.getY();
		double zVelocity = target.getZ() - this.getZ();
		double power = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
		projectile.setVelocity(xVelocity, yVelocity + power * 0.2D, zVelocity, 1.6F, 6);
		projectile.pickupType = consumeArrow ? PickupPermission.ALLOWED : PickupPermission.CREATIVE_ONLY;

		this.world.spawnEntity(projectile);
		itemStack.damage(1, this, entity -> {
		});
		this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		if (!consumeArrow) {
			arrow.decrement(1);
		}
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

	private static final String KEY_INVENTORY = "Inventory";
	private static final String KEY_SLOT = "Slot";
	private static final String KEY_OWNER = "Owner";
	private static final String KEY_IS_SITTING = "IsSitting";

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);

		NbtList nbtList = new NbtList();
		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putInt(LittleMaidEntity.KEY_SLOT, i);
				itemStack.writeNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}
		nbt.put(LittleMaidEntity.KEY_INVENTORY, nbtList);

		UUID uuid = this.getOwnerUuid();
		if (uuid != null) {
			nbt.putUuid(LittleMaidEntity.KEY_OWNER, uuid);
		}

		nbt.putBoolean(LittleMaidEntity.KEY_IS_SITTING, this.isSitting());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		NbtList nbtList = nbt.getList(LittleMaidEntity.KEY_INVENTORY, NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			this.inventory.setStack(nbtCompound.getInt("Slot"), ItemStack.fromNbt(nbtCompound));
		}

		UUID uuid;
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
		private final List<Predicate<MaidJob>> jobPredicates = new ArrayList<>();
		private final Goal goal;

		private JobWrapperGoal(Goal goal, MaidJob... jobs) {
			this.jobs = jobs;
			this.goal = goal;

			this.setControls(this.goal.getControls());
		}

		private void addJobPredicate(Predicate<MaidJob> jobPredicate) {
			this.jobPredicates.add(jobPredicate);
		}

		@Override
		public boolean canStart() {
			return Arrays.stream(this.jobs).anyMatch(job -> job == LittleMaidEntity.this.getJob())
					&& this.jobPredicates.stream().anyMatch(jobPredicate -> jobPredicate.test(LittleMaidEntity.this.getJob()))
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
		IS_TAMED = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		JOB = DataTracker.registerData(LittleMaidEntity.class, ModEntities.JOB_HANDLER);
	}
}
