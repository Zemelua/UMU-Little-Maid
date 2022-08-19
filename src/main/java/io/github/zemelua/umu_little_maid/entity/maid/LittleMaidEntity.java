package io.github.zemelua.umu_little_maid.entity.maid;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.control.MaidControl;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.mixin.MobEntityAccessor;
import io.github.zemelua.umu_little_maid.mixin.PersistentProjectileEntityAccessor;
import io.github.zemelua.umu_little_maid.mixin.TridentEntityAccessor;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.util.IAvoidRain;
import io.github.zemelua.umu_little_maid.util.IPoseidonMob;
import io.github.zemelua.umu_little_maid.util.ITameable;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AxolotlSwimNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.zemelua.umu_little_maid.entity.ModEntities.*;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner, RangedAttackMob, IPoseidonMob, CrossbowUser, ITameable, IAvoidRain {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static final int MAX_COMMITMENT = 300;
	public static final int DAY_MAX_COMMITMENT = 30;
	public static final float LEFT_HAND_CHANCE = 0.15F;
	public static final Identifier TEXTURE_NONE = UMULittleMaid.identifier("textures/entity/little_maid/little_maid.png");
	public static final Identifier TEXTURE_FENCER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_fencer.png");
	public static final Identifier TEXTURE_CRACKER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_cracker.png");
	public static final Identifier TEXTURE_ARCHER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_archer.png");
	public static final Identifier TEXTURE_GUARD = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_guard.png");
	public static final Identifier TEXTURE_FARMER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_farmer.png");
	public static final Identifier TEXTURE_HEALER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_healer.png");
	public static final Identifier TEXTURE_POSEIDON = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_poseidon.png");

	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_SITTING;
	private static final TrackedData<Boolean> IS_ENGAGING;
	private static final TrackedData<MaidJob> JOB;
	private static final TrackedData<MaidPersonality> PERSONALITY;
	private static final TrackedData<Boolean> IS_USING_DRIPLEAF;
	private static final TrackedData<Boolean> IS_VARIABLE_COSTUME;
	private static final TrackedData<Integer> COMMITMENT;

	private final EntityNavigation landNavigation;
	private final EntityNavigation canSwimNavigation;
	private final SimpleInventory inventory = new SimpleInventory(15);
	private final List<Item> givenFoods = new ArrayList<>();
	private final AnimationState eatAnimation = new AnimationState();
	private final AnimationState healAnimation = new AnimationState();
	private final AnimationState useDripleafAnimation = new AnimationState();
	private final AnimationState changeCostumeAnimation = new AnimationState();

	@Nonnull private MaidJob lastJob;

	private int eatingTicks;
	@Nullable private Consumer<ItemStack> onFinishEating;
	// TODO: もぐもぐをクラスにまとめるかなんかしてきれいにする！

	private int changingCostumeTicks;
	private boolean damageBlocked;
	private long lastClearCommitmentDayTime;
	private int increasedCommitment;

	private float sitProgress;
	private float lastSitProgress;
	private float begProgress;
	private float lastBegProgress;

	public LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);

		this.lastJob = ModEntities.JOB_NONE;
		this.changingCostumeTicks = 0;
		this.damageBlocked = false;

		this.begProgress = 0.0F;
		this.lastBegProgress = 0.0F;

		this.sitProgress = 0.0F;
		this.lastSitProgress = 0.0F;

		this.moveControl = new MaidControl(this);

		this.landNavigation = new MobNavigation(this, world);
		this.canSwimNavigation = new AxolotlSwimNavigation(this, world);
		((MobNavigation) this.landNavigation).setCanPathThroughDoors(true);
		((MobNavigation) this.landNavigation).setCanEnterOpenDoors(true);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		Random random = world.getRandom();

		MaidPersonality personality = MaidSpawnHandler.randomPersonality(random, world.getBiome(this.getBlockPos()));
		this.setPersonality(personality);
		this.initializeAttributes();

		this.setLeftHanded(random.nextDouble() < LittleMaidEntity.LEFT_HAND_CHANCE);

		this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.OFFHAND, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.FEET, 2.0F);

		this.setCanPickUpLoot(true);

		return entityData;
	}

	@SuppressWarnings("ConstantConditions")
	private void initializeAttributes() {
		try {
			MaidPersonality personality = this.getPersonality();

			this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(personality.getMaxHealth());
			this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(personality.getMovementSpeed());
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(personality.getAttackDamage());
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).setBaseValue(personality.getAttackKnockback());
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(personality.getArmor());
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).setBaseValue(personality.getArmorToughness());
			this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(personality.getKnockbackResistance());
			this.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(personality.getLuck());
		} catch (NullPointerException exception) {
			UMULittleMaid.LOGGER.error("メイドさんにAttributeが登録されてないよ～");
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		this.dataTracker.startTracking(LittleMaidEntity.OWNER, Optional.empty());
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, false);
		this.dataTracker.startTracking(LittleMaidEntity.IS_ENGAGING, false);
		this.dataTracker.startTracking(LittleMaidEntity.JOB, ModEntities.JOB_NONE);
		this.dataTracker.startTracking(LittleMaidEntity.PERSONALITY, ModEntities.PERSONALITY_BRAVERY);
		this.dataTracker.startTracking(LittleMaidEntity.IS_USING_DRIPLEAF, false);
		this.dataTracker.startTracking(LittleMaidEntity.IS_VARIABLE_COSTUME, true);
		this.dataTracker.startTracking(COMMITMENT, 0);
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
				.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D)
				.add(EntityAttributes.GENERIC_ARMOR, 0.0D)
				.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.0D)
				.add(EntityAttributes.GENERIC_LUCK, 0.0D);
	}

	// <editor-fold desc="Brain">
	@Override
	protected Brain.Profile<LittleMaidEntity> createBrainProfile() {
		return Brain.createProfile(LittleMaidEntity.MEMORY_MODULES, LittleMaidEntity.SENSORS);
	}

	@Override
	protected Brain<LittleMaidEntity> deserializeBrain(Dynamic<?> dynamic) {
		Brain<LittleMaidEntity> brain = this.createBrainProfile().deserialize(dynamic);
		this.getJob().initializeBrain(brain);

		return brain;
	}
	// </editor-fold>

	//<editor-fold desc="Movement">
	@Override
	protected EntityNavigation createNavigation(World world) {
		MobNavigation navigation = new MobNavigation(this, world);
		navigation.setCanPathThroughDoors(true);
		navigation.setCanEnterOpenDoors(true);

		return navigation;
	}

	@Override
	public float getPathfindingPenalty(PathNodeType nodeType) {
		if (nodeType == PathNodeType.WATER) {
			return this.getJob() == JOB_POSEIDON ? 0.0F : 0.8F;
		}

		return super.getPathfindingPenalty(nodeType);
	}

	public boolean canSwim() {
		return this.isTouchingWater() && this.getJob() == JOB_POSEIDON;
	}

	private static final EntityDimensions DIMENSIONS_SLEEPING = EntityDimensions.fixed(0.2F, 0.2F);
	private static final EntityDimensions DIMENSIONS_CRAWLING = EntityDimensions.changing(0.6F, 0.6F);

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return switch (pose) {
			case SLEEPING -> DIMENSIONS_SLEEPING;
			case SWIMMING, SPIN_ATTACK -> DIMENSIONS_CRAWLING;

			default -> this.getType().getDimensions();
		};
	}

	private static final float EYE_HEIGHT_STANDING = 1.2F;
	private static final float EYE_HEIGHT_SLEEPING = 0.2F;
	private static final float EYE_HEIGHT_CRAWLING = 0.4F;

	@Override
	public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return switch (pose) {
			case SLEEPING -> EYE_HEIGHT_SLEEPING;
			case SWIMMING, SPIN_ATTACK -> EYE_HEIGHT_CRAWLING;

			default -> EYE_HEIGHT_STANDING;
		};
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		if (!this.onGround) {
			if (!this.isUsingDripleaf() && this.hasDripleaf() && ModUtils.getHeightFromGround(this.world, this) >= 2) {
				this.setUsingDripleaf(true);
			}
		} else {
			if (this.isUsingDripleaf()) {
				this.setUsingDripleaf(false);
			}
		}

		this.updateAttributes();

		Vec3d velocity = this.getVelocity();
		if (this.isUsingDripleaf() && velocity.getY() < 0.0D) {
			this.setVelocity(velocity.multiply(1.0D, 0.6D, 1.0D));
		}

		if (this.getJob() == JOB_ARCHER || this.getJob() == JOB_HUNTER) this.pickUpArrows();
		if (this.getJob() == JOB_POSEIDON) this.pickUpTridents();

		this.tickHandSwing();
	}

	private static final UUID ATTRIBUTE_ID_CRACKER_SLOW = UUID.fromString("7ED8E765-F4D0-4428-BCBE-654FFF51BAF0");
	private static final UUID ATTRIBUTE_ID_CRACKER_DAMAGE = UUID.fromString("56A32427-E09D-BA9C-EC1C-A1C7BAD8E88A");

	private void updateAttributes() {
		MaidJob job = this.getJob();
		EntityAttributeInstance speedAttribute = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
		EntityAttributeInstance attackAttribute = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE));

		if (job == JOB_CRACKER) {
			if (speedAttribute.getModifier(ATTRIBUTE_ID_CRACKER_SLOW) == null) {
				speedAttribute.addTemporaryModifier(
						new EntityAttributeModifier(ATTRIBUTE_ID_CRACKER_SLOW, "Cracker slow", -0.25D, Operation.MULTIPLY_TOTAL)
				);
			}

			if (attackAttribute.getModifier(ATTRIBUTE_ID_CRACKER_DAMAGE) == null) {
				attackAttribute.addTemporaryModifier(
						new EntityAttributeModifier(ATTRIBUTE_ID_CRACKER_DAMAGE, "Cracker attack", 0.4D, Operation.MULTIPLY_TOTAL)
				);
			}
		} else {
			if (speedAttribute.getModifier(ATTRIBUTE_ID_CRACKER_SLOW) != null) {
				speedAttribute.removeModifier(ATTRIBUTE_ID_CRACKER_SLOW);
			}

			if (attackAttribute.getModifier(ATTRIBUTE_ID_CRACKER_DAMAGE) != null) {
				attackAttribute.removeModifier(ATTRIBUTE_ID_CRACKER_DAMAGE);
			}
		}
	}

	@Override
	public void updateSwimming() {
		if (this.getJob() == JOB_POSEIDON) {
			this.navigation = this.canSwimNavigation;
		} else {
			this.navigation = this.landNavigation;
		}

		super.updateSwimming();
	}

	private void pickUpArrows() {
		if (!this.world.isClient()) {
			Box box = this.getBoundingBox().expand(1.0D, 0.5D, 1.0D);
			Predicate<? super PersistentProjectileEntity> filter = arrow -> {
				@Nullable Entity owner = arrow.getOwner();

				return owner != null && owner.equals(this)
						&& arrow.pickupType == PickupPermission.ALLOWED
						&& (((PersistentProjectileEntityAccessor) arrow).isInGround() || arrow.isNoClip())
						&& arrow.shake <= 0;
			};

			List<? extends PersistentProjectileEntity> collideArrows = ImmutableList.<PersistentProjectileEntity>builder()
					.addAll(this.world.getEntitiesByType(EntityType.ARROW, box, filter))
					.addAll(this.world.getEntitiesByType(EntityType.SPECTRAL_ARROW, box, filter)).build();

			for (PersistentProjectileEntity arrow : collideArrows) {
				ItemStack arrowStack = ((PersistentProjectileEntityAccessor) arrow).callAsItemStack();

				if (this.inventory.canInsert(arrowStack)) {
					this.inventory.addStack(arrowStack);
					this.sendPickup(arrow, 1);
					arrow.discard();
				}
			}
		}
	}

	private void pickUpTridents() {
		if (!this.world.isClient()) {
			Box box = this.getBoundingBox().expand(1.0D, 0.5D, 1.0D);
			Predicate<TridentEntity> filter = trident -> {
				@Nullable Entity owner = trident.getOwner();

				return owner != null && owner.equals(this)
						&& trident.pickupType == PickupPermission.ALLOWED
						&& (((PersistentProjectileEntityAccessor) trident).isInGround() || trident.isNoClip())
						&& trident.shake <= 0
						&& ((TridentEntityAccessor) trident).isDealtDamage();
			};

			List<TridentEntity> collideTridents = this.world.getEntitiesByType(EntityType.TRIDENT, box, filter);

			for (TridentEntity trident : collideTridents) {
				ItemStack tridentStack = ((PersistentProjectileEntityAccessor) trident).callAsItemStack();

				if (this.getMainHandStack().isEmpty()) {
					this.setStackInHand(Hand.MAIN_HAND, tridentStack);
					this.sendPickup(trident, 1);
					trident.discard();
				} else if (this.inventory.canInsert(tridentStack)) {
					this.inventory.addStack(tridentStack);
					this.sendPickup(trident, 1);
					trident.discard();
				}
			}
		}
	}
	//</editor-fold>

	@Override
	public boolean teleport(double x, double y, double z, boolean spawnParticles) {
		if (this.canTeleport(new BlockPos(x, y, z))) {
			this.navigation.stop();
			this.requestTeleport(x, y, z);

			if (spawnParticles) {
				this.spawnTeleportParticles(x, y, z);
			}

			return true;
		}

		return false;
	}

	public boolean canTeleport(BlockPos toPos) {
		if (this.getJob() == JOB_POSEIDON) {
			PathNodeType pathType = this.getNavigation().getNodeMaker()
					.getDefaultNodeType(this.getWorld(), toPos.getX(), toPos.getY(), toPos.getZ());
			if (pathType != PathNodeType.WALKABLE && pathType != PathNodeType.WATER) return false;
			if (pathType != PathNodeType.WATER && this.getWorld().getBlockState(toPos.down()).getBlock() instanceof LeavesBlock)
				return false;
		} else {
			PathNodeType pathType = LandPathNodeMaker.getLandNodeType(this.getWorld(), toPos.mutableCopy());
			if (pathType != PathNodeType.WALKABLE) return false;
			if (this.getWorld().getBlockState(toPos.down()).getBlock() instanceof LeavesBlock) return false;
		}

		return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(Vec3d.of(toPos).subtract(this.getPos())));
	}

	@Override
	public boolean shouldAvoidRain() {
		return this.getJob() != JOB_POSEIDON;
	}

	@Override
	public void tick() {
		super.tick();

		this.lastSitProgress = this.sitProgress;
		if (this.isSitting()) {
			this.sitProgress += (1.0F - this.sitProgress) * 0.4F;
		} else {
			this.sitProgress += (0.0F - this.sitProgress) * 0.4F;
		}

		this.lastBegProgress = this.begProgress;
		if (this.getOwner() != null && this.isSitting() && this.distanceTo(this.getOwner()) < 7.0F) {
			this.begProgress += (1.0F - this.begProgress) * 0.4F;

			if (!this.world.isClient()) {
				this.lookControl.lookAt(this.getOwner());
			}
		} else {
			this.begProgress += (0.0F - this.begProgress) * 0.4F;
		}
	}

	/**
	 * サーバーでのみ呼ばれるよ！
	 */
	@Override
	protected void mobTick() {
		this.updateJob();
		if (!this.getJob().equals(this.lastJob)) {
			this.onJobChanged((ServerWorld) this.world);
		}
		this.lastJob = this.getJob();

		this.getJob().tickBrain(this.getBrain());
		this.getBrain().tick((ServerWorld) this.world, this);

		this.updatePose();

		if (this.getPose() == POSE_EATING) {
			this.eatingTicks++;
			if (this.eatingTicks % 5 == 0) {
				this.spawnEatingParticles();
			}

			this.navigation.stop();
		}
		if (this.eatingTicks >= 16) {
			if (this.onFinishEating != null) {
				this.onFinishEating.accept(this.getOffHandStack());
			}
			this.onFinishEating = null;
			this.eatingTicks = 0;
			this.getOffHandStack().decrement(1);
			this.setPose(EntityPose.STANDING);
		} else if (this.getPose() == EntityPose.STANDING) {
			this.onFinishEating = null;
			this.eatingTicks = 0;
			this.inventory.addStack(this.getOffHandStack().copy());
			this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}

		if (this.getPose() == ModEntities.POSE_CHANGING_COSTUME) {
			this.changingCostumeTicks++;
			this.spawnChangingCostumeParticles();
		}
		if (this.changingCostumeTicks >= 10) {
			this.setPose(EntityPose.STANDING);
			this.changingCostumeTicks = 0;
		}

		long dayTime = this.world.getTimeOfDay();
		long currentDay = dayTime / 24000L;
		long lastDay = this.lastClearCommitmentDayTime / 24000L;
		if (currentDay > lastDay) {
			this.givenFoods.clear();
			this.increaseCommitment(9, true);
			this.increasedCommitment = 0;

			this.lastClearCommitmentDayTime = dayTime;
		}
	}

	private void updatePose() {
		EntityPose pose = this.getPose();
		if (pose == POSE_EATING || pose == ModEntities.POSE_CHANGING_COSTUME || pose == ModEntities.POSE_HEALING) {
			return;
		}

		if (this.isUsingDripleaf()) {
			this.setPose(ModEntities.POSE_USING_DRIPLEAF);

			return;
		}

		if (!this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
			return;
		}
		EntityPose entityPose = this.isFallFlying() ? EntityPose.FALL_FLYING : (this.isSleeping() ? EntityPose.SLEEPING : (this.isSwimming() ? EntityPose.SWIMMING : (this.isUsingRiptide() ? EntityPose.SPIN_ATTACK : (this.isSneaking() ? EntityPose.CROUCHING : EntityPose.STANDING))));
		EntityPose entityPose2 = this.isSpectator() || this.hasVehicle() || this.wouldPoseNotCollide(entityPose) ? entityPose : (this.wouldPoseNotCollide(EntityPose.CROUCHING) ? EntityPose.CROUCHING : EntityPose.SWIMMING);
		this.setPose(entityPose2);
	}

	private void updateJob() {
		for (MaidJob job : ModRegistries.MAID_JOB.stream().toList()) {
			if (job.canApply(this)) {
				this.setJob(job);

				return;
			}
		}

		this.setJob(ModEntities.JOB_NONE);
	}

	private void onJobChanged(ServerWorld world) {
		Brain<LittleMaidEntity> brain = this.getBrain();
		brain.stopAllTasks(world, this);

		this.brain = brain.copy();
		this.getJob().initializeBrain(this.getBrain());
		this.brain.forget(ModEntities.MEMORY_JOB_SITE);
		this.brain.forget(ModEntities.MEMORY_JOB_SITE_CANDIDATE);
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack interactItem = player.getStackInHand(hand);

		if (this.isTamed()) {
			if (player == this.getOwner()) {
				if (player.isSneaking()) {
					if (!this.world.isClient()) {
						player.openHandledScreen(new LittleMaidScreenHandlerFactory(this));
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isIn(ModTags.ITEM_MAID_HEAL_FOODS) && this.getPose() != POSE_EATING) {
					if (!this.world.isClient()) {
						ItemStack food = (player.getAbilities().creativeMode ? interactItem.copy() : interactItem).split(1);
						this.eatFood(food, foodArg -> this.heal(6.5F));

						Item foodType = food.getItem();
						if (!this.givenFoods.contains(foodType)) {
							this.increaseCommitment(this.getPersonality().isIn(ModTags.PERSONALITY_FLUTTER_WHEN_KINDS) ? 6 : 11, true);
							this.givenFoods.add(foodType);
						}
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isIn(ModTags.ITEM_MAID_REINFORCE_FOODS) && this.getPose() != POSE_EATING) {
					if (!this.world.isClient()) {
						ItemStack food = (player.getAbilities().creativeMode ? interactItem.copy() : interactItem).split(1);
						this.eatFood(food, (foodArg) -> {
							if (food.isFood()) {
								for (Pair<StatusEffectInstance, Float> effect : Objects.requireNonNull(food.getItem().getFoodComponent()).getStatusEffects()) {
									if (this.world.getRandom().nextFloat() < effect.getSecond()) {
										this.addStatusEffect(new StatusEffectInstance(effect.getFirst()));
									}
								}
							}
						});

						Item foodType = food.getItem();
						if (!this.givenFoods.contains(foodType)) {
							this.increaseCommitment(this.getPersonality().isIn(ModTags.PERSONALITY_FLUTTER_WHEN_KINDS) ? 50 : 70, true);
							this.givenFoods.add(foodType);
						}
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isIn(ModTags.ITEM_MAID_CHANGE_COSTUMES) && this.getPose() != ModEntities.POSE_CHANGING_COSTUME) {
					if (!this.world.isClient()) {
						this.setVariableCostume(!this.isVariableCostume());
						this.setPose(ModEntities.POSE_CHANGING_COSTUME);
						this.navigation.stop();
						this.jump();

						if (!player.getAbilities().creativeMode) {
							interactItem.decrement(1);
						}
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isIn(ModTags.ITEM_MAID_ENGAGE_BATONS)) {
					if (!this.world.isClient()) {
						if (!this.isEngaging()) {
							this.setEngaging(true);
							this.spawnEngageParticle();
						} else {
							this.setEngaging(false);
							this.spawnSingleParticle(ParticleTypes.ANGRY_VILLAGER);
						}

						this.playEngageSound();
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isOf(Items.DEBUG_STICK)) {
					if (!this.world.isClient()) {
						List<MaidPersonality> allPersonalities = ModRegistries.MAID_PERSONALITY.stream().toList();
						int currentIndex = allPersonalities.indexOf(this.getPersonality());

						if (currentIndex + 1 < allPersonalities.size()) {
							this.setPersonality(allPersonalities.get(currentIndex + 1));
						} else {
							this.setPersonality(allPersonalities.get(0));
						}

						this.initializeAttributes();

						((ServerPlayerEntity) player).sendMessageToClient(Text.translatable(
								Items.DEBUG_STICK.getTranslationKey() + ".select",
								"Personality",
								ModRegistries.MAID_PERSONALITY.getId(this.getPersonality())
						), true);
					}

					return ActionResult.success(this.world.isClient());
				} else {
					if (!this.world.isClient()) {
						this.setSitting(!this.isSitting());
						this.playSitSound();
					}

					return ActionResult.success(this.world.isClient());
				}
			}
		} else {
			if (interactItem.isIn(ModTags.ITEM_MAID_CONTRACT_FOODS) && this.getPose() != POSE_EATING) {
				if (!this.world.isClient()) {
					ItemStack food = (player.getAbilities().creativeMode ? interactItem.copy() : interactItem).split(1);
					this.eatFood(food, (foodArg) -> {
						this.setOwner(player);
						this.spawnContractParticles();
						this.playContractSound();
					});
					this.brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(player, true));
				}

				return ActionResult.success(this.world.isClient());
			}
		}

		return super.interactMob(player, hand);
	}

	public void eatFood(ItemStack food, Consumer<ItemStack> onFinishEating) {
		this.setStackInHand(Hand.OFF_HAND, food);
		this.onFinishEating = onFinishEating;
		this.playEatSound(food);
		this.brain.forget(MemoryModuleType.WALK_TARGET);
		this.brain.forget(MemoryModuleType.LOOK_TARGET);
		this.setPose(POSE_EATING);
	}

	/**
	 * 継承元との差異: 攻撃したときにアイテムの耐久値が減少するよ！
	 */
	@Override
	public boolean tryAttack(Entity target) {
		MaidJob job = this.getJob();
		if (job == ModEntities.JOB_FENCER) {
			this.playFencerAttackSound();
		} else if (job == ModEntities.JOB_CRACKER) {
			this.playCrackerAttackSound();
		}

		double damage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		double knockback = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
		int fireLevel = EnchantmentHelper.getFireAspect(this);

		if (target instanceof LivingEntity living) {
			damage += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), living.getGroup());
			knockback += EnchantmentHelper.getKnockback(this);

			if (living instanceof MobEntity mob) {
				@Nullable LivingEntity targetTarget = mob.getTarget();
				@Nullable Entity owner = this.getOwner();

				if ((targetTarget != null && owner != null)
						&& targetTarget.equals(owner)
						&& this.getPersonality().isIn(ModTags.PERSONALITY_DEVOTE_WHEN_ATTACK_OWNERS_ENEMIES)) {
					damage *= 1.0D + this.getCommitment() / 500.0D;
				}
			}
		}


		if (fireLevel > 0) {
			target.setOnFireFor(fireLevel * 4);
		}

		boolean damagePassed = target.damage(DamageSource.mob(this), (float) damage);

		if (damagePassed) {
			ItemStack itemStack = this.getMainHandStack();

			if (knockback > 0.0f && target instanceof LivingEntity living) {
				double xStrength = Math.sin(this.getYaw() * Math.PI / 180.0D);
				double zStrength = -Math.cos(this.getYaw() * Math.PI / 180.0D);

				living.takeKnockback(knockback * 0.5D, xStrength, zStrength);

				this.setVelocity(this.getVelocity().multiply(0.6D, 1.0D, 0.6D));
			}
			if (target instanceof PlayerEntity player && player.isUsingItem()) {
				((MobEntityAccessor) this).callDisablePlayerShield(player, itemStack, player.getActiveItem());
			}

			this.applyDamageEffects(this, target);
			this.onAttacking(target);

			if (target instanceof LivingEntity living) {
				itemStack.getItem().postHit(itemStack, living, this);
				if (itemStack.isEmpty()) {
					this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
				}

				if (living.isDead()) {
					if (this.getHealth() <= this.getMaxHealth() * 0.3F) {
						this.playKilledBarelySound();
					} else {
						this.playKilledSound();
					}
				}
			}
		}

		return damagePassed;
	}

	/**
	 * 弓で攻撃するとき呼ばれるよ！ {@code PersistentProjectileEntity} を生成して発射してるよ
	 *
	 * @param target       攻撃対象の {@code LivingEntity}
	 * @param pullProgress どれだけ弓を引いた状態で撃ったか
	 */
	@Override
	public void attack(LivingEntity target, float pullProgress) {
		ItemStack mainStack = this.getMainHandStack();

		if (this.getJob() == ModEntities.JOB_ARCHER) {
			ItemStack arrow = this.getArrowType(mainStack);
			if (arrow.isEmpty()) return;

			boolean consumeArrow = !ModUtils.hasEnchantment(Enchantments.INFINITY, mainStack)
					|| arrow.isOf(Items.TIPPED_ARROW)
					|| arrow.isOf(Items.SPECTRAL_ARROW);

			PersistentProjectileEntity projectile = ProjectileUtil.createArrowProjectile(this, arrow, pullProgress);
			double xVelocity = target.getX() - this.getX();
			double yVelocity = target.getBodyY(1.0D / 3.0D) - projectile.getY();
			double zVelocity = target.getZ() - this.getZ();
			double power = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
			projectile.setVelocity(xVelocity, yVelocity + power * 0.2D, zVelocity, 1.6F, 6);
			projectile.pickupType = consumeArrow ? PickupPermission.ALLOWED : PickupPermission.CREATIVE_ONLY;

			this.world.spawnEntity(projectile);

			this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
			this.playArcherAttackSound();

			mainStack.damage(1, this, entity -> {});
			if (consumeArrow) {
				arrow.decrement(1);
			}
		} else if (this.getJob() == ModEntities.JOB_HUNTER) {
			this.shoot(this, 1.6F);
		}
	}

	@Override
	public void throwTrident(Entity target) {
		ItemStack mainStack = this.getMainHandStack();
		if (EnchantmentHelper.getRiptide(mainStack) > 0) return;

		TridentEntity trident = new TridentEntity(this.getWorld(), this, mainStack);

		double x = target.getX() - this.getX();
		double y = target.getBodyY(1.0D / 3.0D) - trident.getY();
		double z = target.getZ() - this.getZ();
		double power = Math.sqrt(x * x + z * z);
		trident.setVelocity(x, y + power * 0.2D, z, 1.6F, 6.0F);
		trident.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;

		this.getWorld().spawnEntity(trident);

		this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		mainStack.decrement(1);

		if (mainStack.getCount() <= 0) {
			this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

			this.brain.remember(ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN, Unit.INSTANCE, 200);
			if (EnchantmentHelper.getLoyalty(trident.asItemStack()) <= 0) {
				this.brain.remember(ModEntities.MEMORY_THROWN_TRIDENT, trident, 200);
			}
		}
	}

	/**
	 * Riptideで敵にぶつかったときに呼ばれるよ！
	 *
	 * @param target 攻撃対象の {@code LivingEntity}
	 */
	@Override
	protected void attackLivingEntity(LivingEntity target) {
		this.tryAttack(target);
	}

	@Override
	public void setCharging(boolean charging) {}

	@Override
	public void shoot(LivingEntity target, ItemStack crossbow, ProjectileEntity projectile, float multiShotSpray) {
		this.shoot(this, target, projectile, multiShotSpray, 1.6F);
	}

	@Override
	public void postShoot() {}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return this.getJob() == ModEntities.JOB_ARCHER || this.getJob() == JOB_POSEIDON || this.getJob() == ModEntities.JOB_HUNTER;
	}

	@Override
	@Nullable
	public LivingEntity getTarget() {
		return this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		@Nullable UUID owner = this.getOwnerUuid();
		@Nullable Entity attacker = source.getAttacker();

		if (owner != null && attacker instanceof PlayerEntity player) {
			return owner.equals(player.getUuid());
		} else if (attacker != null) {
			if (attacker.getType().equals(ModEntities.LITTLE_MAID)) {
				return true;
			}
		}

		return super.isInvulnerableTo(source);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean result = super.damage(source, amount);

		this.damageBlocked = false;

		if (!this.world.isClient() && this.isSitting()) {
			this.setSitting(false);
		}

		return result;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
		if (this.isUsingDripleaf()) {
			return false;
		} else {
			return super.handleFallDamage(fallDistance, damageMultiplier, source);
		}
	}

	@Override
	protected void damageArmor(DamageSource source, float amount) {
		this.damageArmor(source, amount, this.getEquippedStack(EquipmentSlot.FEET));
		this.damageArmor(source, amount, this.getEquippedStack(EquipmentSlot.HEAD));
	}

	@Override
	protected void damageHelmet(DamageSource source, float amount) {
		this.damageArmor(source, amount, this.getEquippedStack(EquipmentSlot.HEAD));
	}

	private void damageArmor(DamageSource source, float amount, ItemStack itemStack) {
		if (amount <= 0.0f) return;
		if (!(itemStack.getItem() instanceof ArmorItem)) return;
		if (source.isFire() && itemStack.getItem().isFireproof()) return;

		if ((amount /= 4.0f) < 1.0f) {
			amount = 1.0f;
		}

		itemStack.damage((int) amount, this, maid -> {});
	}

	@Override
	protected void damageShield(float amount) {
		if (!this.activeItemStack.isOf(Items.SHIELD)) return;

		this.damageBlocked = true;

		if (!this.world.isClient) {
			this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.getRandom().nextFloat() * 0.4F);
		}
		if (amount >= 3.0f) {
			int damage = 1 + MathHelper.floor(amount);
			Hand hand = this.getActiveHand();
			this.activeItemStack.damage(damage, this, entity -> {});

			if (this.activeItemStack.isEmpty()) {
				if (hand == Hand.MAIN_HAND) {
					this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				} else {
					this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}

				this.activeItemStack = ItemStack.EMPTY;
				this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.getRandom().nextFloat() * 0.4F);
			}
		}
	}

	@Override
	protected int getNextAirOnLand(int air) {
		return Math.min(air + 10, this.getMaxAir());
	}

	@Override
	public void sleep(BlockPos pos) {
		super.sleep(pos);

		this.brain.forget(MemoryModuleType.WALK_TARGET);
		this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		this.brain.forget(MemoryModuleType.LOOK_TARGET);
		this.brain.forget(MemoryModuleType.HOME);
	}

	@Override
	public void wakeUp() {
		super.wakeUp();

		this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		if (!this.damageBlocked) {
			super.playHurtSound(source);
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

	public ItemStack getHasCrop() {
		if (this.getOffHandStack().isIn(ModTags.ITEM_MAID_CROPS)) {
			return this.getOffHandStack();
		}

		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (itemStack.isIn(ModTags.ITEM_MAID_CROPS)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}

	public ItemStack getHasChorusFruit() {
		if (this.getOffHandStack().isIn(ModTags.ITEM_MAID_CROPS)) {
			return this.getOffHandStack();
		}

		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (itemStack.isIn(ModTags.ITEM_MAID_CHORUS_FRUITS)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}

	public boolean canBreakGourd() {
		return this.getCommitment() >= 75;
	}

	public boolean hasDripleaf() {
		for (int i = 0; i < this.inventory.size(); i++) {
			if (this.inventory.getStack(i).isIn(ModTags.ITEM_MAID_DRIPLEAFS)) {
				return true;
			}
		}

		return false;
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

	public void spawnContractParticles() {
		final ParticleEffect particle = ParticleTypes.HEART;

		for (int i = 0; i < 7; i++) {
			double x = this.random.nextGaussian() * 0.02;
			double y = this.random.nextGaussian() * 0.02;
			double z = this.random.nextGaussian() * 0.02;

			((ServerWorld) this.world).spawnParticles(particle, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0, x, y, z, 1.0D);
		}
	}

	public void spawnEatingParticles() {
		if (!this.world.isClient()) {
			for (int i = 0; i < 6; i++) {
				double y = -this.random.nextDouble() * 0.6D - 0.3D;
				Vec3d pos = new Vec3d((this.random.nextDouble() - 0.5D) * 0.3D, y, 0.6D)
						.rotateX(-this.getPitch() * ((float) Math.PI / 180))
						.rotateY(-this.bodyYaw * ((float) Math.PI / 180))
						.add(this.getX(), this.getEyeY(), this.getZ());

				Vec3d delta = new Vec3d((this.random.nextDouble() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)
						.rotateX(-this.getPitch() * ((float) Math.PI / 180))
						.rotateY(-this.getYaw() * ((float) Math.PI / 180));

				for (ServerPlayerEntity target : ((ServerWorld) this.world).getPlayers()) {
					PacketByteBuf packet = PacketByteBufs.create();
					packet.writeItemStack(this.getOffHandStack());
					packet.writeDouble(pos.getX());
					packet.writeDouble(pos.getY());
					packet.writeDouble(pos.getZ());
					packet.writeDouble(delta.getX());
					packet.writeDouble(delta.getY());
					packet.writeDouble(delta.getZ());

					ServerPlayNetworking.send(target, NetworkHandler.CHANNEL_MAID_EAT_PARTICLE, packet);
				}
			}
		}
	}

	public void spawnChangingCostumeParticles() {
		if (!this.world.isClient()) {
			double rotate = Math.toRadians(this.getYaw() + this.changingCostumeTicks * 360.0D / 10);
			Vec3d left = new Vec3d(0.0D, 0.0D, -0.5D)
					.rotateY((float) rotate)
					.add(this.getX(), this.getY() + 0.7D, this.getZ());
			Vec3d right = new Vec3d(0.0D, 0.0D, 0.5D)
					.rotateY((float) rotate)
					.add(this.getX(), this.getY() + 0.7D, this.getZ());

			((ServerWorld) this.world).spawnParticles(ParticleTypes.GLOW, left.getX(), left.getY(), left.getZ(), 0, 0.0D, 0.0D, 0.0D, 1.0D);
			((ServerWorld) this.world).spawnParticles(ParticleTypes.GLOW, right.getX(), right.getY(), right.getZ(), 0, 0.0D, 0.0D, 0.0D, 1.0D);
		}
	}

	public void spawnTeleportParticles(double x, double y, double z) {
		if (!this.world.isClient()) {
			for (int i = 0; i < 128; i++) {
				double delta = i / 127.0D;
				double xPos = MathHelper.lerp(delta, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5D) * this.getWidth() * 2.0D;
				double yPos = MathHelper.lerp(delta, this.prevY, this.getY()) + this.random.nextDouble() * this.getHeight();
				double zPos = MathHelper.lerp(delta, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5D) * this.getWidth() * 2.0D;
				float xDelta = (this.random.nextFloat() - 0.5F) * 0.2F;
				float yDelta = (this.random.nextFloat() - 0.5F) * 0.2F;
				float zDelta = (this.random.nextFloat() - 0.5F) * 0.2F;

				((ServerWorld) this.world).spawnParticles(ParticleTypes.PORTAL, xPos, yPos, zPos, 0, xDelta, yDelta, zDelta, 1.0D);
			}
		}
	}

	private void spawnSingleParticle(@SuppressWarnings("SameParameterValue") ParticleEffect particle) {
		if (!this.world.isClient()) {
			((ServerWorld) this.world).spawnParticles(particle, this.getX(), this.getY() + 1.2D, this.getZ(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
		}
	}

	private void spawnEngageParticle() {
		if (!this.world.isClient()) {
			double color = MathHelper.clamp(0.3D, 0.0D, 1.0D);
			((ServerWorld) this.world).spawnParticles(ParticleTypes.NOTE, this.getX(), this.getY() + 1.2D, this.getZ(), 0, color, 0.0D, 0.0D, 1.0D);
		}
	}

	@Override
	protected void dropInventory() {
		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
				this.dropStack(itemStack);
			}
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
	protected void loot(ItemEntity item) {
		InventoryOwner.pickUpItem(this, this, item);
	}

	@Override
	public boolean canPickupItem(ItemStack itemStack) {
		if (this.getJob() == ModEntities.JOB_FARMER) {
			return itemStack.isIn(ModTags.ITEM_MAID_PRODUCTS);
		}

		return false;
	}

	public static boolean canSpawn(EntityType<LittleMaidEntity> ignoredType, ServerWorldAccess world, SpawnReason ignoredReason, BlockPos pos, Random ignoredRandom) {
		return world.getBlockState(pos.down()).isIn(BlockTags.RABBITS_SPAWNABLE_ON) && world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public boolean cannotDespawn() {
		return this.isTamed();
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (data.equals(Entity.POSE)) {
			EntityPose pose = this.getPose();
			if (pose == POSE_EATING) {
				this.eatAnimation.start(this.age);
			} else {
				this.eatAnimation.stop();
			}

			if (pose == ModEntities.POSE_USING_DRIPLEAF) {
				this.useDripleafAnimation.start(this.age);
			} else {
				this.useDripleafAnimation.stop();
			}

			if (pose == ModEntities.POSE_CHANGING_COSTUME) {
				this.changeCostumeAnimation.start(this.age);
			} else {
				this.changeCostumeAnimation.stop();
			}

			if (pose == ModEntities.POSE_HEALING) {
				this.healAnimation.start(this.age);
			} else {
				this.healAnimation.stop();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Brain<LittleMaidEntity> getBrain() {
		return (Brain<LittleMaidEntity>) super.getBrain();
	}

	@Nullable
	@Override
	public UUID getOwnerUuid() {
		return this.dataTracker.get(LittleMaidEntity.OWNER).orElse(null);
	}

	private void setOwnerUuid(@Nullable UUID uuid) {
		this.dataTracker.set(LittleMaidEntity.OWNER, Optional.ofNullable(uuid));
		this.getBrain().remember(ModEntities.MEMORY_OWNER, uuid);
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

	@Override
	public Optional<PlayerEntity> getMaster() {
		Optional<UUID> masterID = this.dataTracker.get(LittleMaidEntity.OWNER);

		return masterID.map(value -> this.world.getPlayerByUuid(value));
	}

	public boolean isTamed() {
		return this.getOwnerUuid() != null;
	}

	public boolean isSitting() {
		return this.dataTracker.get(LittleMaidEntity.IS_SITTING);
	}

	private void setSitting(boolean value) {
		this.dataTracker.set(LittleMaidEntity.IS_SITTING, value);

		if (value) {
			this.brain.remember(ModEntities.MEMORY_IS_SITTING, Unit.INSTANCE);
		} else {
			this.brain.forget(ModEntities.MEMORY_IS_SITTING);
		}
	}

	public boolean isEngaging() {
		return this.dataTracker.get(LittleMaidEntity.IS_ENGAGING);
	}

	private void setEngaging(boolean value) {
		this.dataTracker.set(LittleMaidEntity.IS_ENGAGING, value);
	}

	public MaidJob getJob() {
		return this.dataTracker.get(LittleMaidEntity.JOB);
	}

	public void setJob(MaidJob value) {
		this.dataTracker.set(LittleMaidEntity.JOB, value);
	}

	@SuppressWarnings("unused")
	public MaidPersonality getPersonality() {
		return this.dataTracker.get(LittleMaidEntity.PERSONALITY);
	}

	public void setPersonality(MaidPersonality value) {
		this.dataTracker.set(LittleMaidEntity.PERSONALITY, value);
	}

	public boolean isIdle() {
		return this.brain.hasActivity(Activity.IDLE);
	}

	public boolean isUsingDripleaf() {
		return this.dataTracker.get(LittleMaidEntity.IS_USING_DRIPLEAF);
	}

	public void setUsingDripleaf(boolean value) {
		this.dataTracker.set(LittleMaidEntity.IS_USING_DRIPLEAF, value);
	}

	public boolean isVariableCostume() {
		return this.dataTracker.get(LittleMaidEntity.IS_VARIABLE_COSTUME);
	}

	private void setVariableCostume(boolean value) {
		this.dataTracker.set(LittleMaidEntity.IS_VARIABLE_COSTUME, value);
	}

	public int getCommitment() {
		return this.dataTracker.get(LittleMaidEntity.COMMITMENT);
	}

	private void setCommitment(int value) {
		this.dataTracker.set(LittleMaidEntity.COMMITMENT, value);
	}

	public void increaseCommitment(int value, boolean force) {
		if (force) {
			if (this.increasedCommitment + value > DAY_MAX_COMMITMENT) {
				value = value - (this.increasedCommitment + value - DAY_MAX_COMMITMENT);
			}

			this.increasedCommitment += value;
		}

		if (value > 0) {
			this.setCommitment(Math.min(this.getCommitment() + value, LittleMaidEntity.MAX_COMMITMENT));
			this.spawnSingleParticle(ParticleTypes.HEART);
		}
	}

	public float getSitProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastSitProgress, this.sitProgress);
	}

	public float getBegProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastBegProgress, this.begProgress);
	}

	public AnimationState getEatAnimation() {
		return this.eatAnimation;
	}

	public AnimationState getHealAnimation() {
		return this.healAnimation;
	}

	public AnimationState getUseDripleafAnimation() {
		return this.useDripleafAnimation;
	}

	public AnimationState getChangeCostumeAnimation() {
		return this.changeCostumeAnimation;
	}

	@Override
	public float getSoundPitch() {
		return 1.0F;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return this.getPersonality().getAmbientSound();
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.getPersonality().getHurtSound();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return this.getPersonality().getDeathSound();
	}

	@Override
	public SoundEvent getEatSound(ItemStack stack) {
		return this.getPersonality().getEatSound();
	}

	@Override
	public void playAmbientSound() {
		if (!this.world.isClient() && this.isIdle()) {
			this.playSound(this.getAmbientSound(), this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public void playFencerAttackSound() {
		this.playSound(this.getPersonality().getFencerAttackSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playCrackerAttackSound() {
		this.playSound(this.getPersonality().getCrackerAttackSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playArcherAttackSound() {
		this.playSound(this.getPersonality().getArcherAttackSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playKilledSound() {
		this.playSound(this.getPersonality().getKilledSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playKilledBarelySound() {
		this.playSound(this.getPersonality().getKilledBarelySound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playEatSound(ItemStack itemStack) {
		this.playSound(this.getEatSound(itemStack), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playContractSound() {
		this.playSound(this.getPersonality().getContractSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playSitSound() {
		this.playSound(this.getPersonality().getSitSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	public void playEngageSound() {
		this.playSound(this.getPersonality().getEngageSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 300;
	}

	public Identifier getTexture() {
		if (this.isVariableCostume()) {
			return this.getJob().getTexture();
		}

		return LittleMaidEntity.TEXTURE_NONE;
	}

	//<editor-fold desc="Save/Load">
	private static final String KEY_INVENTORY = "Inventory";
	private static final String KEY_SLOT = "Slot";
	private static final String KEY_GIVEN_FOODS = "GivenFoods";
	private static final String KEY_OWNER = "Owner";
	private static final String KEY_IS_SITTING = "IsSitting";
	private static final String KEY_IS_ENGAGING = "IsEngaging";
	private static final String KEY_JOB = "Job";
	private static final String KEY_PERSONALITY = "Personality";
	private static final String KEY_IS_VARIABLE_COSTUME = "IsVariableCostume";
	private static final String KEY_COMMITMENT = "Commitment";

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);

		NbtList inventoryNbt = new NbtList();
		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putInt(LittleMaidEntity.KEY_SLOT, i);
				itemStack.writeNbt(nbtCompound);
				inventoryNbt.add(nbtCompound);
			}
		}
		nbt.put(LittleMaidEntity.KEY_INVENTORY, inventoryNbt);

		NbtList givenFoodsNbt = new NbtList();
		for (Item givenFood : this.givenFoods) {
			givenFoodsNbt.add(NbtString.of(Registry.ITEM.getId(givenFood).toString()));
		}
		nbt.put(KEY_GIVEN_FOODS, givenFoodsNbt);

		UUID uuid = this.getOwnerUuid();
		if (uuid != null) {
			nbt.putUuid(LittleMaidEntity.KEY_OWNER, uuid);
		}

		nbt.putBoolean(LittleMaidEntity.KEY_IS_SITTING, this.isSitting());
		nbt.putBoolean(LittleMaidEntity.KEY_IS_ENGAGING, this.isEngaging());

		@Nullable Identifier job = ModRegistries.MAID_JOB.getId(this.getJob());
		if (job != null) {
			nbt.putString(LittleMaidEntity.KEY_JOB, job.toString());
		}

		@Nullable Identifier personality = ModRegistries.MAID_PERSONALITY.getId(this.getPersonality());
		if (personality != null) {
			nbt.putString(LittleMaidEntity.KEY_PERSONALITY, personality.toString());
		}

		nbt.putBoolean(LittleMaidEntity.KEY_IS_VARIABLE_COSTUME, this.isVariableCostume());
		nbt.putInt(KEY_COMMITMENT, this.getCommitment());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);

		NbtList inventoryNbt = nbt.getList(LittleMaidEntity.KEY_INVENTORY, NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < inventoryNbt.size(); i++) {
			NbtCompound nbtCompound = inventoryNbt.getCompound(i);
			this.inventory.setStack(nbtCompound.getInt("Slot"), ItemStack.fromNbt(nbtCompound));
		}

		NbtList givenFoodsNbt = nbt.getList(KEY_GIVEN_FOODS, NbtElement.STRING_TYPE);
		for (int i = 0; i < givenFoodsNbt.size(); i++) {
			Item foodType = Registry.ITEM.get(Identifier.tryParse(givenFoodsNbt.getString(i)));
			if (foodType != Items.AIR) {
				this.givenFoods.add(foodType);
			}
		}

		UUID uuid;
		if (nbt.containsUuid(LittleMaidEntity.KEY_OWNER)) {
			uuid = nbt.getUuid(LittleMaidEntity.KEY_OWNER);
		} else {
			String string = nbt.getString(LittleMaidEntity.KEY_OWNER);
			uuid = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
		}
		this.setOwnerUuid(uuid);

		this.setSitting(nbt.getBoolean(LittleMaidEntity.KEY_IS_SITTING));
		this.setEngaging(nbt.getBoolean(LittleMaidEntity.KEY_IS_ENGAGING));

		if (nbt.contains(LittleMaidEntity.KEY_JOB)) {
			this.setJob(ModRegistries.MAID_JOB.get(Identifier.tryParse(nbt.getString(LittleMaidEntity.KEY_JOB))));
		}

		if (nbt.contains(LittleMaidEntity.KEY_PERSONALITY)) {
			this.setPersonality(ModRegistries.MAID_PERSONALITY.get(Identifier.tryParse(nbt.getString(LittleMaidEntity.KEY_PERSONALITY))));
		}

		this.setVariableCostume(nbt.getBoolean(LittleMaidEntity.KEY_IS_VARIABLE_COSTUME));
		this.setCommitment(nbt.getInt(KEY_COMMITMENT));
	}
	//</editor-fold>

	@Override
	public MobEntity self() {
		return this;
	}

	static {
		MEMORY_MODULES = ImmutableSet.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleType.PATH,
				MemoryModuleType.DOORS_TO_CLOSE,
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleType.MOBS,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleType.HURT_BY,
				MemoryModuleType.HURT_BY_ENTITY,
				MemoryModuleType.IS_PANICKING,
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_SHOULD_EAT,
				ModEntities.MEMORY_SHOULD_SLEEP,
				MemoryModuleType.NEAREST_BED,
				MemoryModuleType.HOME,
				MemoryModuleType.LAST_WOKEN,
				MemoryModuleType.LAST_SLEPT,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleType.ATTACK_COOLING_DOWN,
				ModEntities.MEMORY_HAS_ARROWS,
				ModEntities.MEMORY_ATTRACTABLE_LIVINGS,
				ModEntities.MEMORY_GUARDABLE_LIVING,
				ModEntities.MEMORY_GUARD_TARGET,
				ModEntities.MEMORY_SHOULD_HEAL,
				ModEntities.MEMORY_FARMABLE_POSES,
				ModEntities.MEMORY_FARM_POS,
				ModEntities.MEMORY_FARM_COOLDOWN,
				ModEntities.MEMORY_JOB_SITE,
				ModEntities.MEMORY_JOB_SITE_CANDIDATE,
				ModEntities.MEMORY_THROWN_TRIDENT,
				ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN,
				ModEntities.MEMORY_SHOULD_BREATH,
				MEMORY_IS_HUNTING
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				SensorType.HURT_BY,
				ModEntities.SENSOR_HOME_CANDIDATE,
				ModEntities.SENSOR_MAID_ATTACKABLE,
				ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS,
				ModEntities.SENSOR_MAID_GUARDABLE_LIVING,
				ModEntities.SENSOR_MAID_FARMABLE_POSES,
				ModEntities.SENSOR_JOB_SITE_CANDIDATE
		);

		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		IS_ENGAGING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		JOB = DataTracker.registerData(LittleMaidEntity.class, ModEntities.JOB_HANDLER);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		IS_USING_DRIPLEAF = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		IS_VARIABLE_COSTUME = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		COMMITMENT = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.INTEGER);
	}
}
