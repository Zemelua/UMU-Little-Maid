package io.github.zemelua.umu_little_maid.entity.maid;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
import io.github.zemelua.umu_little_maid.client.particle.ZZZParticle;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModDataHandlers;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.ModSensors;
import io.github.zemelua.umu_little_maid.entity.control.MaidControl;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.entity.maid.attack.MaidAttackType;
import io.github.zemelua.umu_little_maid.entity.maid.feeling.IMaidFeeling;
import io.github.zemelua.umu_little_maid.entity.maid.feeling.MaidFeeling;
import io.github.zemelua.umu_little_maid.entity.maid.job.IMaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJobs;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.mixin.MobEntityAccessor;
import io.github.zemelua.umu_little_maid.mixin.PersistentProjectileEntityAccessor;
import io.github.zemelua.umu_little_maid.mixin.TridentEntityAccessor;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import io.github.zemelua.umu_little_maid.particle.ModParticles;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.util.*;
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
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.zemelua.umu_little_maid.entity.ModEntities.*;

public non-sealed class LittleMaidEntity extends PathAwareEntity implements ILittleMaidEntity, InventoryOwner, RangedAttackMob, IPoseidonMob, CrossbowUser, ITameable, IAvoidRain, IInstructable, IHeadpattable, IAnimatable {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static final int MAX_COMMITMENT = 300;
	public static final int DAY_CEIL_COMMITMENT = 30;
	public static final float LEFT_HAND_CHANCE = 0.15F;

	private static final TrackedData<Optional<UUID>> MASTER;
	private static final TrackedData<MaidMode> MODE;
	private static final TrackedData<IMaidJob> JOB;
	private static final TrackedData<MaidPersonality> PERSONALITY;
	private static final TrackedData<IMaidFeeling> FEELING;
	private static final TrackedData<Optional<MaidAction>> ACTION;
	private static final TrackedData<Boolean> IS_VARIABLE_COSTUME;
	private static final TrackedData<Integer> COMMITMENT;
	private static final TrackedData<Optional<GlobalPos>> HOME;
	private static final TrackedData<Optional<GlobalPos>> ANCHOR;
	private static final TrackedData<Collection<GlobalPos>> DELIVERY_BOXES;
	private static final TrackedData<Optional<Integer>> ATTACK_TARGET;
	private static final TrackedData<MaidAttackType> ATTACK_TYPE;

	private final EntityNavigation landNavigation;
	private final EntityNavigation canSwimNavigation;
	private final SimpleInventory inventory = new SimpleInventory(15);
	private final List<Item> givenFoods = new ArrayList<>();

	private int attackingTicks;
	@Nullable private LivingEntity attackingTarget;

	private int eatingTicks;
	@Nullable private Consumer<ItemStack> onFinishEating;

	private int changingCostumeTicks;

	/**
	 * {@link LittleMaidEntity#damageShield(float)} で {@code true} になり、その後 {@link LittleMaidEntity#damage(DamageSource, float)}
	 * で {@code false} になります。つまり、 {@link LittleMaidEntity#damageShield(float)} を呼び出した直後(通常は {@link LittleMaidEntity#damage(DamageSource, float)}
	 * 内においてスーパーメソッドが呼び出された直後)にこのフィールドをチェックすることでダメージが盾によって防がれたかど
	 * うかを得ることができます。
	 */
	private boolean damageBlocked;

	/**
	 * ダメージを受けたか攻撃を盾で防いだとき40が設定され、毎ティック1ずつ減少します。頭突きを発生させるかどうかの判定に
	 * 使用します。サーバー側でのみ使用されます。
	 */
	private int attackedCooldown;

	/**
	 * 連続で攻撃を受けた回数。
	 */
	private int continuityAttackedCount;

	private long lastClearCommitmentDayTime;
	private int increasedCommitment;

	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);

		this.changingCostumeTicks = 0;
		this.damageBlocked = false;

		this.moveControl = new MaidControl(this);

		this.landNavigation = new MobNavigation(this, world);
		this.canSwimNavigation = new AxolotlSwimNavigation(this, world);
		((MobNavigation) this.landNavigation).setCanPathThroughDoors(true);
		((MobNavigation) this.landNavigation).setCanEnterOpenDoors(true);

		this.ignoreCameraFrustum = true;
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

		this.dataTracker.startTracking(MASTER, Optional.empty());
		this.dataTracker.startTracking(MODE, MaidMode.FOLLOW);
		this.dataTracker.startTracking(JOB, MaidJobs.NONE);
		this.dataTracker.startTracking(ACTION, Optional.empty());
		this.dataTracker.startTracking(PERSONALITY, PERSONALITY_BRAVERY);
		this.dataTracker.startTracking(FEELING, MaidFeeling.NORMAL);
		this.dataTracker.startTracking(IS_VARIABLE_COSTUME, true);
		this.dataTracker.startTracking(COMMITMENT, 0);
		this.dataTracker.startTracking(HOME, Optional.empty());
		this.dataTracker.startTracking(ANCHOR, Optional.empty());
		this.dataTracker.startTracking(DELIVERY_BOXES, new HashSet<>());
		this.dataTracker.startTracking(ATTACK_TARGET, Optional.empty());
		this.dataTracker.startTracking(ATTACK_TYPE, MaidAttackType.NO_ATTACKING);
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

	@Override
	protected Brain.Profile<LittleMaidEntity> createBrainProfile() {
		return Brain.createProfile(MEMORY_MODULES, SENSORS);
	}

	@Override
	protected Brain<LittleMaidEntity> deserializeBrain(Dynamic<?> dynamic) {
		Brain<LittleMaidEntity> brain = this.createBrainProfile().deserialize(dynamic);
		this.getJob().initBrain(brain);

		return brain;
	}

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
			return this.getJob().equals(MaidJobs.POSEIDON) ? 0.0F : 0.8F;
		}

		return super.getPathfindingPenalty(nodeType);
	}

	public boolean canSwim() {
		return this.isTouchingWater() && this.getJob().equals(MaidJobs.POSEIDON);
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

		if (this.getAction().isEmpty()) {
			if (!this.isOnGround() && this.hasDripleaf() && ModUtils.getHeightFromGround(this.world, this) >= 3) {
				this.setAction(MaidAction.GLIDING);
			}
		} else if (this.isGliding()) {
			if (this.isOnGround() || !this.hasDripleaf()) {
				this.removeAction();
			}
		}

		this.updateAttributes();

		Vec3d velocity = this.getVelocity();
		if (this.isGliding() && velocity.getY() < 0.0D) {
			this.setVelocity(velocity.multiply(1.0D, 0.6D, 1.0D));
		}

		if (this.getJob().equals(MaidJobs.ARCHER) || this.getJob().equals(MaidJobs.HUNTER)) this.pickUpArrows();
		if (this.getJob().equals(MaidJobs.POSEIDON)) this.pickUpTridents();

		this.tickHandSwing();
	}

	private static final UUID ATTRIBUTE_ID_CRACKER_SLOW = UUID.fromString("7ED8E765-F4D0-4428-BCBE-654FFF51BAF0");
	private static final UUID ATTRIBUTE_ID_CRACKER_DAMAGE = UUID.fromString("56A32427-E09D-BA9C-EC1C-A1C7BAD8E88A");

	private void updateAttributes() {
		IMaidJob job = this.getJob();
		EntityAttributeInstance speedAttribute = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED));
		EntityAttributeInstance attackAttribute = Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE));

		if (job.equals(MaidJobs.CRACKER)) {
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
		if (this.getJob().equals(MaidJobs.POSEIDON)) {
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
				this.spawnTeleportParticles();
			}

			return true;
		}

		return false;
	}

	public boolean canTeleport(BlockPos toPos) {
		if (this.getJob().equals(MaidJobs.POSEIDON)) {
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
		return !this.getJob().equals(MaidJobs.POSEIDON);
	}

	@Override
	public void tick() {
		super.tick();

		if (HeadpatManager.isHeadpatted(this)) {
			this.setFeeling(MaidFeeling.HAPPY);
		} else {
			this.setFeeling(MaidFeeling.NORMAL);
		}
	}

	/**
	 * サーバーでのみ呼ばれるよ！
	 */
	@Override
	protected void mobTick() {
		this.updateJob();

		this.getJob().tickBrain(this.getBrain());
		this.getBrain().tick((ServerWorld) this.world, this);

		this.updatePose();

		//攻撃関係
		MaidAttackType state = this.getAttackType();
		switch (state) {
			case SWING_SWORD_DOWNWARD_RIGHT, SWING_SWORD_DOWNWARD_LEFT -> {
				if (this.attackingTicks == 3) {
					this.tryAttack(this.attackingTarget);
				} else if (attackingTicks >= 10) {
					this.finishAttack();
				}
			} case SWEEP_SWORD -> {
				if (this.attackingTicks == 5) {
					this.tryAttack(this.attackingTarget);
				} else if (attackingTicks >= 10) {
					this.finishAttack();
				}
			} case HEADBUTT -> {
				if (this.attackingTicks == 24) {
					this.headbutt();
				} else if (attackingTicks >= 40) {
					this.finishAttack();
				}
			}
		}
		if (state != MaidAttackType.NO_ATTACKING) {
			this.getNavigation().stop();
			this.attackingTicks++;
		}

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
			this.removeAction();
		} else if (this.getPose() == EntityPose.STANDING) {
			this.onFinishEating = null;
			this.eatingTicks = 0;
			this.inventory.addStack(this.getOffHandStack().copy());
			this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}

		if (this.getPose() == ModEntities.POSE_CHANGING_COSTUME) {
			this.changingCostumeTicks++;

			this.navigation.stop();
		}
		if (this.changingCostumeTicks == 20) {
			this.jump();
		}
		if (this.changingCostumeTicks == 25) {
			this.setVariableCostume(!this.isVariableCostume());
		}
		if (this.changingCostumeTicks >= 59) {
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

		this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
				.ifPresentOrElse(this::setAttackTarget, this::removeAttackTarget);

		if (this.attackedCooldown > 0) {
			this.attackedCooldown--;
		} else {
			this.continuityAttackedCount = 0;
		}
	}

	private void updatePose() {
		EntityPose pose = this.getPose();
		if (pose == POSE_EATING || pose == ModEntities.POSE_CHANGING_COSTUME || pose == ModEntities.POSE_HEALING) {
			return;
		}

		if (this.isGliding()) {
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
		IMaidJob job = MaidJobs.getAllJobs()
				.filter(j -> j.canAssume(this))
				.max(Comparator.comparingInt(IMaidJob::getPriority))
				.orElse(MaidJobs.NONE);

		if (!job.equals(this.getJob())) {
			this.setJob(job);
			this.onJobChanged((ServerWorld) this.world);
		}
	}

	private void onJobChanged(ServerWorld world) {
		Brain<LittleMaidEntity> brain = this.getBrain();
		brain.stopAllTasks(world, this);

		// TODO: 村人のコードを参考にしてるんだけど、なんでコピーしとかなきゃなのかを確認する
		this.brain = brain.copy();
		this.getJob().initBrain(this.getBrain());
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
						this.setPose(ModEntities.POSE_CHANGING_COSTUME);
						this.navigation.stop();
						// this.jump();

						if (!player.getAbilities().creativeMode) {
							interactItem.decrement(1);
						}
					}

					return ActionResult.success(this.world.isClient());
				} else if (interactItem.isIn(ModTags.ITEM_MAID_INSTRUCTORS)) {
					if (!this.world.isClient()) {
						IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
						if (instructionComponent.isInstructing()) {
							instructionComponent.cancelInstruction();
						} else {
							instructionComponent.startInstruction(this);
						}
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
						this.setMode(this.getMode().getNext());
						player.sendMessage(this.getMode().getMessage(this), true);
					}

					return ActionResult.success(this.world.isClient());
				}
			}
		} else {
			if (interactItem.isIn(ModTags.ITEM_MAID_CONTRACT_FOODS) && this.getPose() != POSE_EATING) {
				if (!this.world.isClient()) {
					ItemStack food = (player.getAbilities().creativeMode ? interactItem.copy() : interactItem).split(1);
					this.eatFood(food, (foodArg) -> {
						this.setMaster(player);
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

	@Override
	public void takeKnockback(double strength, double x, double z) {
		if (this.getPersonality().isIn(ModTags.PERSONALITY_PERSISTENCES) || this.getAttackType() != MaidAttackType.NO_ATTACKING) {
			return;
		}

		super.takeKnockback(strength, x, z);
	}

	public void eatFood(ItemStack food, Consumer<ItemStack> onFinishEating) {
		this.setStackInHand(Hand.OFF_HAND, food);
		this.onFinishEating = onFinishEating;
		this.playEatSound(food);
		this.brain.forget(MemoryModuleType.WALK_TARGET);
		this.brain.forget(MemoryModuleType.LOOK_TARGET);
		this.setPose(POSE_EATING);
		this.setAction(MaidAction.EATING);
	}

	/**
	 * 継承元との差異: 攻撃したときにアイテムの耐久値が減少するよ！
	 */
	@Override
	public boolean tryAttack(Entity target) {
		IMaidJob job = this.getJob();
		if (job.equals(MaidJobs.FENCER)) {
			this.playFencerAttackSound();
		} else if (job.equals(MaidJobs.CRACKER)) {
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

	private void headbutt() {
		boolean damagePassed = Objects.requireNonNull(this.attackingTarget).damage(DamageSource.mob(this), 5.0F);

		if (damagePassed) {
			double xDir = Math.sin(this.getYaw() * Math.PI / 180.0D);
			double zDir = -Math.cos(this.getYaw() * Math.PI / 180.0D);
			this.attackingTarget.takeKnockback(1.6F, xDir, zDir);
		}
	}

	public void startAttack(LivingEntity target, MaidAttackType type) {
		this.setAttackType(type);
		this.attackingTarget = target;
		this.attackingTicks = 0;
	}

	public void finishAttack() {
		this.setAttackType(MaidAttackType.NO_ATTACKING);
		this.attackingTarget = null;
		this.attackingTicks = 0;
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

		if (this.getJob().equals(MaidJobs.ARCHER)) {
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
		} else if (this.getJob().equals(MaidJobs.HUNTER)) {
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
		mainStack.damage(1, this, selfValue -> selfValue.sendToolBreakStatus(this.getActiveHand()));
		mainStack.decrement(1);

		this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		if (mainStack.getCount() <= 0) {
			this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

			this.brain.remember(ModMemories.THROWN_TRIDENT_COOLDOWN, Unit.INSTANCE, 200);
			if (EnchantmentHelper.getLoyalty(trident.asItemStack()) <= 0) {
				this.brain.remember(ModMemories.THROWN_TRIDENT, trident, 200);
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
		return this.getJob().equals(MaidJobs.ARCHER) || this.getJob().equals(MaidJobs.POSEIDON) || this.getJob().equals(MaidJobs.HUNTER);
	}

	@Override
	@Nullable
	public LivingEntity getTarget() {
		return this.getAttackTarget().orElse(null);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		@Nullable UUID owner = this.getMasterUuid();
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

		if (!this.world.isClient()) {
			if (result || this.damageBlocked) {
				if (this.attackedCooldown > 0) {
					this.continuityAttackedCount++;
				}

				this.attackedCooldown = 40;
			}
		}

		this.damageBlocked = false;

		if (!this.world.isClient() && this.isSitting()) {
			this.setMode(MaidMode.FREE);
		}

		return result;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
		if (this.isGliding()) {
			return false;
		} else {
			return super.handleFallDamage(fallDistance, damageMultiplier, source);
		}
	}

	@Override
	public void damageArmor(DamageSource source, float amount) {
		this.damageArmor(source, amount, this.getEquippedStack(EquipmentSlot.FEET));
		this.damageArmor(source, amount, this.getEquippedStack(EquipmentSlot.HEAD));
	}

	@Override
	public void damageHelmet(DamageSource source, float amount) {
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
	public void damageShield(float amount) {
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
	}

	@Override
	public void wakeUp() {
		super.wakeUp();

		this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
		this.brain.forget(ModMemories.SLEEP_POS);
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

	public void spawnTeleportParticles() {
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
		if (this.getJob().equals(MaidJobs.FARMER)) {
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
	public SimpleInventory getInventory() {
		return this.inventory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Brain<LittleMaidEntity> getBrain() {
		return (Brain<LittleMaidEntity>) super.getBrain();
	}

	@Nullable
	private UUID getMasterUuid() {
		return this.dataTracker.get(LittleMaidEntity.MASTER).orElse(null);
	}

	private void setMasterUuid(@Nullable UUID uuid) {
		this.dataTracker.set(LittleMaidEntity.MASTER, Optional.ofNullable(uuid));
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getMasterUuid();

		if (uuid == null) return null;

		return this.world.getPlayerByUuid(uuid);
	}

	@Override
	public Optional<PlayerEntity> getMaster() {
		return Optional.ofNullable(this.getMasterUuid()).map(id -> this.world.getPlayerByUuid(id));
	}

	private void setMaster(PlayerEntity master) {
		this.setMasterUuid(master.getUuid());
	}

	public boolean isTamed() {
		return this.getMasterUuid() != null;
	}

	public MaidMode getMode() {
		return this.dataTracker.get(LittleMaidEntity.MODE);
	}

	private void setMode(MaidMode value) {
		this.dataTracker.set(LittleMaidEntity.MODE, value);

		if (value == MaidMode.WAIT) {
			this.brain.remember(ModMemories.IS_SITTING, Unit.INSTANCE);
		} else {
			this.brain.forget(ModMemories.IS_SITTING);
		}

		if (value == MaidMode.FREE) {
			// TODO: おうちから離れてるときのみアンカーを設定するように
			this.setAnchor(GlobalPos.create(this.world.getRegistryKey(), this.getBlockPos()));
		} else {
			this.removeAnchor();
		}
	}

	@Override
	public boolean isFollowingMaster() {
		return this.dataTracker.get(MODE) == MaidMode.FOLLOW;
	}

	@Override
	public boolean isSitting() {
		return this.dataTracker.get(MODE) == MaidMode.WAIT;
	}

	@Override
	public boolean isHeadpatted() {
		if (this.getMaster().isPresent()) {
			return HeadpatManager.isHeadpattingWith(this.getMaster().get(), this);
		}

		return false;
	}

	@Override
	public Optional<GlobalPos> getHome() {
		return this.dataTracker.get(HOME);
	}

	@Override
	public void setHome(GlobalPos value) {
		this.dataTracker.set(HOME, Optional.of(value));
		this.brain.remember(MemoryModuleType.HOME, Optional.of(value));
	}

	@Override
	public void removeHome() {
		this.dataTracker.set(HOME, Optional.empty());
		this.brain.forget(MemoryModuleType.HOME);
	}

	@Override
	public boolean isSettableAsHome(World world, BlockPos pos) {
		return world.getBlockState(pos).isIn(ModTags.BLOCK_MAID_SETTABLE_AS_HOME);
	}

	@Override
	public Optional<GlobalPos> getAnchor() {
		return this.dataTracker.get(ANCHOR);
	}

	@Override
	public void setAnchor(GlobalPos value) {
		this.dataTracker.set(ANCHOR, Optional.of(value));
		this.brain.remember(ModMemories.ANCHOR, Optional.of(value));
	}

	@Override
	public void removeAnchor() {
		this.dataTracker.set(ANCHOR, Optional.empty());
		this.brain.forget(ModMemories.ANCHOR);
	}

	@Override
	public Collection<GlobalPos> getDeliveryBoxes() {
		return this.dataTracker.get(DELIVERY_BOXES);
	}

	@Override
	public void addDeliveryBox(GlobalPos value) {
		Collection<GlobalPos> boxes = this.dataTracker.get(DELIVERY_BOXES);
		boxes.add(value);
		this.dataTracker.set(DELIVERY_BOXES, boxes);
		this.brain.remember(ModMemories.DELIVERY_BOXES, List.copyOf(boxes));
	}

	@Override
	public void removeDeliveryBox(GlobalPos value) {
		Collection<GlobalPos> boxes = this.dataTracker.get(DELIVERY_BOXES);
		boxes.remove(value);
		this.dataTracker.set(DELIVERY_BOXES, boxes);
		this.brain.forget(ModMemories.DELIVERY_BOXES);
	}

	@Override
	public boolean isSettableAsDeliveryBox(World world, BlockPos pos) {
		return world.getBlockState(pos).isIn(ModTags.BLOCK_MAID_SETTABLE_AS_DELIVERY_BOX);
	}

	public IMaidJob getJob() {
		return this.dataTracker.get(LittleMaidEntity.JOB);
	}

	public void setJob(IMaidJob value) {
		this.dataTracker.set(LittleMaidEntity.JOB, value);
	}

	@SuppressWarnings("unused")
	public MaidPersonality getPersonality() {
		return this.dataTracker.get(LittleMaidEntity.PERSONALITY);
	}

	public void setPersonality(MaidPersonality value) {
		this.dataTracker.set(LittleMaidEntity.PERSONALITY, value);
	}

	@Override
	@SuppressWarnings("unused")
	public Optional<MaidAction> getAction() {
		return this.dataTracker.get(ACTION);
	}

	@SuppressWarnings("unused")
	public void setAction(MaidAction value) {
		this.dataTracker.set(ACTION, Optional.of(value));
	}

	@SuppressWarnings("unused")
	public void removeAction() {
		this.dataTracker.set(ACTION, Optional.empty());
	}

	@Deprecated
	public IMaidFeeling getFeeling() {
		return this.dataTracker.get(FEELING);
	}

	public void setFeeling(IMaidFeeling value) {
		this.dataTracker.set(FEELING, value);
	}

	public boolean isIdle() {
		return this.brain.hasActivity(Activity.IDLE);
	}

	public boolean isGliding() {
		return this.getAction()
				.map(action -> action.equals(MaidAction.GLIDING))
				.orElse(false);
	}

	@Override
	public boolean isEating() {
		return this.getAction()
				.map(action -> action.equals(MaidAction.EATING))
				.orElse(false);
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

	public Optional<LivingEntity> getAttackTarget() {
		return this.dataTracker.get(ATTACK_TARGET)
				.map(id -> (LivingEntity) this.world.getEntityById(id));
	}

	public void setAttackTarget(LivingEntity value) {
		this.dataTracker.set(ATTACK_TARGET, Optional.of(value.getId()));
	}

	public void removeAttackTarget() {
		this.dataTracker.set(ATTACK_TARGET, Optional.empty());
	}

	public MaidAttackType getAttackType() {
		return this.dataTracker.get(ATTACK_TYPE);
	}

	public void setAttackType(MaidAttackType value) {
		this.dataTracker.set(ATTACK_TYPE, value);
	}

	public void increaseCommitment(int value, boolean force) {
		if (force) {
			if (this.increasedCommitment + value > DAY_CEIL_COMMITMENT) {
				value = value - (this.increasedCommitment + value - DAY_CEIL_COMMITMENT);
			}

			this.increasedCommitment += value;
		}

		if (value > 0) {
			this.setCommitment(Math.min(this.getCommitment() + value, LittleMaidEntity.MAX_COMMITMENT));
			this.spawnSingleParticle(ParticleTypes.HEART);
		}
	}

	public int getContinuityAttackedCount() {
		return this.continuityAttackedCount;
	}

	public void resetContinuityAttackedCount() {
		this.continuityAttackedCount = 0;
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

	@SuppressWarnings("unused")
	public void playSitSound() {
		this.playSound(this.getPersonality().getSitSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	@SuppressWarnings("unused")
	public void playEngageSound() {
		this.playSound(this.getPersonality().getEngageSound(), this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 300;
	}

	//<editor-fold desc="Save/Load">
	private static final String KEY_INVENTORY = "Inventory";
	private static final String KEY_SLOT = "Slot";
	private static final String KEY_GIVEN_FOODS = "GivenFoods";
	private static final String KEY_OWNER = "Owner";
	private static final String KEY_MODE = "Mode";
	private static final String KEY_JOB = "Job";
	private static final String KEY_PERSONALITY = "Personality";
	private static final String KEY_IS_VARIABLE_COSTUME = "IsVariableCostume";
	private static final String KEY_COMMITMENT = "Commitment";
	private static final String KEY_HOME = "Home";
	private static final String KEY_ANCHOR = "Anchor";
	private static final String KEY_DELIVERY_BOXES = "DeliveryBoxes";

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

		UUID uuid = this.getMasterUuid();
		if (uuid != null) {
			nbt.putUuid(LittleMaidEntity.KEY_OWNER, uuid);
		}

		nbt.putInt(LittleMaidEntity.KEY_MODE, this.getMode().getId());

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

		this.getHome().map(ModUtils.Conversions::globalPosToNBT)
				.ifPresent(n -> nbt.put(KEY_HOME, n));
		this.getAnchor().map(ModUtils.Conversions::globalPosToNBT)
				.ifPresent(n -> nbt.put(KEY_ANCHOR, n));
		NbtList boxesNBT = new NbtList();
		this.getDeliveryBoxes().stream()
				.map(ModUtils.Conversions::globalPosToNBT)
				.forEach(boxesNBT::add);
		nbt.put(KEY_DELIVERY_BOXES, boxesNBT);
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
		this.setMasterUuid(uuid);

		this.setMode(MaidMode.fromId(nbt.getInt(LittleMaidEntity.KEY_MODE)));

		if (nbt.contains(LittleMaidEntity.KEY_JOB)) {
			this.setJob(ModRegistries.MAID_JOB.get(Identifier.tryParse(nbt.getString(LittleMaidEntity.KEY_JOB))));
		}

		if (nbt.contains(LittleMaidEntity.KEY_PERSONALITY)) {
			this.setPersonality(ModRegistries.MAID_PERSONALITY.get(Identifier.tryParse(nbt.getString(LittleMaidEntity.KEY_PERSONALITY))));
		}

		this.setVariableCostume(nbt.getBoolean(LittleMaidEntity.KEY_IS_VARIABLE_COSTUME));
		this.setCommitment(nbt.getInt(KEY_COMMITMENT));

		if (nbt.contains(KEY_HOME)) {
			this.setHome(ModUtils.Conversions.nbtToGlobalPos(nbt.get(KEY_HOME)));
		}
		if (nbt.contains(KEY_ANCHOR)) {
			this.setAnchor(ModUtils.Conversions.nbtToGlobalPos(nbt.get(KEY_ANCHOR)));
		}
		NbtList boxesNBT = nbt.getList(KEY_DELIVERY_BOXES, NbtElement.COMPOUND_TYPE);
		for (NbtElement boxNBT : boxesNBT) {
			this.addDeliveryBox(ModUtils.Conversions.nbtToGlobalPos(boxNBT));
		}
	}
	//</editor-fold>

	@Override
	public MobEntity self() {
		return this;
	}

	@Override
	public AnimationFactory getFactory() {
		return this.animationFactory;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "behavior", 1.5F, e -> {
			AnimationController<LittleMaidEntity> controller = e.getController();
			AnimationBuilder builder = new AnimationBuilder();

			AtomicBoolean itemAnimated = new AtomicBoolean(false);
			if (this.getItemUseTimeLeft() > 0) {
				Optional<IMaidItemAnimationSetter> animationSetter = getItemAnimation(this);
				animationSetter.ifPresent(setter -> {
					setter.setItemAnimation(this, builder);
					itemAnimated.set(true);
				});
			}

			if (!itemAnimated.get()) {
				if (this.isGliding()) {
					builder.addRepeatingAnimation("wait", 1);
					builder.addAnimation("glide", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (this.isTransforming()) {
					builder.addAnimation("transform", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
				} else if (this.isHeadpatted()) {
					builder.addAnimation("headpatted", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (this.isEating()) {
					builder.addAnimation("eat", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (this.isSleeping()) {
					builder.addAnimation("sleeping", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (this.isSleeping()) {
					builder.addAnimation("sleeping", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (this.getItemUseTimeLeft() > 0) {
					switch (this.getActiveItem().getUseAction()) {
						case BOW -> MaidItemAnimationSetter.BOW.setItemAnimation(this, builder);
						case SPEAR -> MaidItemAnimationSetter.SPEAR.setItemAnimation(this, builder);
					}
				} else if (this.isSitting()) {
					builder.addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP);
				} else if (e.isMoving()) {
					if (this.getTarget() != null && this.getJob().equals(MaidJobs.FENCER)) {
						builder.addAnimation("chase_sword", ILoopType.EDefaultLoopTypes.LOOP);
					} else {
						builder.addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
					}
				} else {
					builder.addAnimation("stand", ILoopType.EDefaultLoopTypes.LOOP);
				}
			}

			controller.setAnimation(builder);
			controller.registerParticleListener(event -> {
				switch (event.effect) {
					case "kirakira" -> {
						double tick = event.getAnimationTick();
						double rotate = Math.toRadians(this.getYaw() + (tick - 1) * 360.0D / 10);
						Vec3d left = new Vec3d(0.0D, 0.0D, -0.5D)
								.rotateY((float) rotate)
								.add(this.getX(), this.getY() + 0.7D, this.getZ());
						Vec3d right = new Vec3d(0.0D, 0.0D, 0.5D)
								.rotateY((float) rotate)
								.add(this.getX(), this.getY() + 0.7D, this.getZ());

						this.world.addParticle(ModParticles.TWINKLE, left.getX(), left.getY(), left.getZ(), 0.0D, 0.0D, 0.0D);
						this.world.addParticle(ModParticles.TWINKLE, right.getX(), right.getY(), right.getZ(), 0.0D, 0.0D, 0.0D);
					} case "shock" -> {
						Vec3d pos = new Vec3d(0.0D, 0.0D, 1.6D)
								.rotateY((float) Math.toRadians(-this.getYaw()))
								.add(this.getPos())
								.add(0.0D, 1.25D, 0.0D);

						this.world.addParticle(ModParticles.SHOCK, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
						this.world.addParticle(ModParticles.SHOCKWAVE, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
					} case "zzz" -> {
						double rotateX = Math.toRadians(this.random.nextGaussian() * 5.0D);
						double rotateY = Math.toRadians(Objects.requireNonNull(this.getSleepingDirection()).asRotation() + this.random.nextGaussian() * 5.0D);
						double rotateZ = Math.toRadians(this.random.nextGaussian() * 5.0D);

						Vec3d vel = new Vec3d(0.34D, 0.78D, 0.34D)
								.rotateX((float) rotateX)
								.rotateY((float) rotateY)
								.rotateZ((float) rotateZ)
								.normalize()
								.multiply(0.2D);
						Vec3d pos = vel.normalize()
								.multiply(0.8D)
								.add(this.getPos());

						this.world.addParticle(new ZZZParticle.Mediator(0), pos.getX(), pos.getY(), pos.getZ(), vel.getX(), vel.getY(), vel.getZ());
						this.world.addParticle(new ZZZParticle.Mediator(1), pos.getX(), pos.getY(), pos.getZ(), vel.getX(), vel.getY(), vel.getZ());
						this.world.addParticle(new ZZZParticle.Mediator(2), pos.getX(), pos.getY(), pos.getZ(), vel.getX(), vel.getY(), vel.getZ());
					}
				}
			});

			return PlayState.CONTINUE;
		}));

		data.addAnimationController(new AnimationController<>(this, "sub", 1.5F, event -> {
			AnimationController<LittleMaidEntity> controller = event.getController();
			AnimationBuilder builder = new AnimationBuilder();

			if (this.isGliding()) {
				builder.addAnimation("glide_root", ILoopType.EDefaultLoopTypes.LOOP);
			} else {
				return PlayState.STOP;
			}

			controller.setAnimation(builder);

			return PlayState.CONTINUE;
		}));
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	private static Optional<IMaidItemAnimationSetter> getItemAnimation(LittleMaidEntity maid) {
		ItemStack activeStack = maid.getActiveItem();
		if (activeStack.isEmpty()) return Optional.empty();

		if (maid.getItemUseTimeLeft() > 0) {
			UseAction useAction = activeStack.getUseAction();
			return switch (useAction) {
				// TODO: 他のアイテムもやる
				case BOW ->Optional.of(MaidItemAnimationSetter.BOW);
				default -> Optional.empty();
			};
		}

		return Optional.empty();
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
				ModMemories.IS_SITTING,
				ModMemories.SHOULD_EAT,
				ModMemories.SHOULD_SLEEP,
				MemoryModuleType.NEAREST_BED,
				MemoryModuleType.HOME,
				MemoryModuleType.LAST_WOKEN,
				MemoryModuleType.LAST_SLEPT,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleType.ATTACK_COOLING_DOWN,
				ModMemories.HAS_ARROWS,
				ModMemories.ATTRACTABLE_LIVINGS,
				ModMemories.GUARDABLE_LIVING,
				ModMemories.GUARD_AGAINST,
				ModMemories.SHOULD_HEAL,
				ModMemories.FARMABLE_POSES,
				ModMemories.FARM_POS,
				ModMemories.FARM_COOLDOWN,
				ModMemories.THROWN_TRIDENT,
				ModMemories.THROWN_TRIDENT_COOLDOWN,
				ModMemories.SHOULD_BREATH,
				ModMemories.IS_HUNTING,
				ModMemories.ANCHOR,
				ModMemories.DELIVERY_BOXES,
				ModMemories.CANT_REACH_HOME,
				ModMemories.AVAILABLE_BED,
				ModMemories.SLEEP_POS
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				SensorType.HURT_BY,
				ModSensors.SENSOR_HOME_CANDIDATE,
				ModSensors.AVAILABLE_BED,
				ModSensors.SENSOR_MAID_ATTACKABLE,
				ModSensors.SENSOR_MAID_ATTRACTABLE_LIVINGS,
				ModSensors.SENSOR_MAID_GUARDABLE_LIVING,
				ModSensors.SENSOR_MAID_FARMABLE_POSES
		);

		MASTER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		MODE = DataTracker.registerData(LittleMaidEntity.class, MODE_HANDLER);
		JOB = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.MAID_JOB);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		ACTION = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.OPTIONAL_MAID_ACTION);
		FEELING = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.MAID_FEELING);
		IS_VARIABLE_COSTUME = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		COMMITMENT = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.INTEGER);
		HOME = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_GLOBAL_POS);
		ANCHOR = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_GLOBAL_POS);
		DELIVERY_BOXES = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.COLLECTION_GLOBAL_POS);
		ATTACK_TARGET = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.OPTIONAL_INT);
		ATTACK_TYPE = DataTracker.registerData(LittleMaidEntity.class, ModDataHandlers.MAID_ATTACK_TYPE);
	}
}
