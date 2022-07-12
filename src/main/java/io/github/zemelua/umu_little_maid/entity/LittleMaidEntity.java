package io.github.zemelua.umu_little_maid.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.zemelua.umu_little_maid.entity.brain.LittleMaidBrain;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPose;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandlerFactory;
import io.github.zemelua.umu_little_maid.mixin.MobEntityAccessor;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner, RangedAttackMob {
	public static final EquipmentSlot[] ARMORS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.FEET};
	public static final float LEFT_HAND_CHANCE = 0.15F;

	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_SITTING;
	private static final TrackedData<MaidPersonality> PERSONALITY;
	private static final TrackedData<Integer> EATING_TICKS;
	private static final TrackedData<MaidPose> POSE;

	private static final ImmutableList<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;

	private final SimpleInventory inventory = new SimpleInventory(15);
	private final AnimationState eatAnimation = new AnimationState();

	private boolean blockedDamage;

	protected LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
	                             @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		Random random = world.getRandom();
		EntityAttributeInstance followRange = this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);

		if (followRange != null) {
			followRange.addPersistentModifier(new EntityAttributeModifier(
					"Random spawn bonus", random.nextTriangular(0.0, 0.11485000000000001),
					EntityAttributeModifier.Operation.MULTIPLY_BASE
			));
		}

		this.setLeftHanded(random.nextDouble() < LittleMaidEntity.LEFT_HAND_CHANCE);

		return entityData;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		this.dataTracker.startTracking(LittleMaidEntity.OWNER, Optional.empty());
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, false);
		this.dataTracker.startTracking(LittleMaidEntity.PERSONALITY, ModEntities.BRAVERY);
		this.dataTracker.startTracking(LittleMaidEntity.EATING_TICKS, 0);
		this.dataTracker.startTracking(LittleMaidEntity.POSE, MaidPose.NONE);
	}

	@Override
	protected Brain.Profile<LittleMaidEntity> createBrainProfile() {
		return Brain.createProfile(LittleMaidEntity.MEMORY_MODULES, LittleMaidEntity.SENSORS);
	}

	@Override
	protected Brain<LittleMaidEntity> deserializeBrain(Dynamic<?> dynamic) {
		return LittleMaidBrain.create(this.createBrainProfile().deserialize(dynamic));
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D);
	}

	@Override
	protected void mobTick() {
		this.getBrain().tick((ServerWorld) this.world, this);
		LittleMaidBrain.updateActivities(this);

		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		this.tickHandSwing();
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
				} else {
					if (!this.world.isClient()) {
						this.setSitting(!this.isSitting());
					}
				}

				return ActionResult.success(this.world.isClient());
			}
		} else {
			if (interactItem.isOf(Items.CAKE)) {
				if (!this.world.isClient()) {
					this.setOwner(player);
					this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);

					if (!player.getAbilities().creativeMode) {
						interactItem.decrement(1);
					}
				}

				return ActionResult.success(this.world.isClient());
			}
		}

		return super.interactMob(player, hand);
	}

	/**
	 * 継承元との差異: 攻撃したときにアイテムの耐久値が減少するよ！
	 */
	@Override
	public boolean tryAttack(Entity target) {
		double damage = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		double knockback = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
		int fireLevel = EnchantmentHelper.getFireAspect(this);

		if (target instanceof LivingEntity living) {
			damage += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), living.getGroup());
			knockback += EnchantmentHelper.getKnockback(this);
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
			}
		}

		return damagePassed;
	}

	/**
	 * 弓で攻撃するとき呼ばれるよ！ {@code PersistentProjectileEntity} を生成して発射してるよ
	 * @param target 攻撃対象の {@code LivingEntity}
	 * @param pullProgress どれだけ弓を引いた状態で撃ったか
	 */
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

		this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		itemStack.damage(1, this, entity -> {});
		if (!consumeArrow) {
			arrow.decrement(1);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		@Nullable UUID owner = this.getOwnerUuid();

		if (owner != null && source.getAttacker() instanceof PlayerEntity player) {
			return this.getOwnerUuid().equals(player.getUuid());
		}

		return super.isInvulnerableTo(source);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean result = super.damage(source, amount);

		this.blockedDamage = false;

		return result;
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

		this.blockedDamage = true;

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
	protected void playHurtSound(DamageSource source) {
		if (!blockedDamage) {
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

	public void playEatingAnimation() {
		if (!this.world.isClient() && this.getEatingTicks() % 5 == 0) {
			this.playSound(SoundEvents.ENTITY_CAT_EAT, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

			for (int i = 0; i < 6; i++) {
				double d = -this.random.nextDouble() * 0.6D - 0.3D;
				Vec3d pos = new Vec3d((this.random.nextDouble() - 0.5D) * 0.3D, d, 0.6D);
				pos = pos.rotateX(-this.getPitch() * ((float) Math.PI / 180));
				pos = pos.rotateY(-this.getYaw() * ((float) Math.PI / 180));
				pos = pos.add(this.getX(), this.getEyeY(), this.getZ());

				Vec3d delta = new Vec3d((this.random.nextDouble() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				delta = delta.rotateX(-this.getPitch() * ((float) Math.PI / 180));
				delta = delta.rotateY(-this.getYaw() * ((float) Math.PI / 180));

				((ServerWorld) this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getEquippedStack(EquipmentSlot.OFFHAND)), pos.x, pos.y, pos.z, 0, delta.x, delta.y + 0.05, delta.z, 1.0);
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
	public void onTrackedDataSet(TrackedData<?> data) {
		if (data.equals(LittleMaidEntity.POSE)) {
			MaidPose pose = this.getAnimationPose();
			if (pose == MaidPose.EAT) {
				this.eatAnimation.start(this.age);
			} else {
				this.eatAnimation.stop();
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

	private void setOwner(PlayerEntity player) {
		this.setOwnerUuid(player.getUuid());
	}

	public boolean isTamed() {
		return this.getOwnerUuid() != null;
	}

	public boolean isSitting() {
		return this.dataTracker.get(LittleMaidEntity.IS_SITTING);
	}

	private void setSitting(boolean sitting) {
		this.dataTracker.set(LittleMaidEntity.IS_SITTING, sitting);

		if (sitting) {
			brain.remember(ModEntities.MEMORY_IS_SITTING, Unit.INSTANCE);
		} else {
			brain.forget(ModEntities.MEMORY_IS_SITTING);
		}
	}

	public MaidJob getJob() {
		for (MaidJob job : ModRegistries.MAID_JOB.stream().toList()) {
			if (job.canApply(this)) {
				return job;
			}
		}

		return ModEntities.JOB_NONE;
	}

	@SuppressWarnings("unused")
	public MaidPersonality getPersonality() {
		return this.dataTracker.get(LittleMaidEntity.PERSONALITY);
	}

	public int getEatingTicks() {
		return this.dataTracker.get(LittleMaidEntity.EATING_TICKS);
	}

	public void setEatingTicks(int value) {
		this.dataTracker.set(LittleMaidEntity.EATING_TICKS, value);
	}

	public MaidPose getAnimationPose() {
		return this.dataTracker.get(LittleMaidEntity.POSE);
	}

	public void setAnimationPose(MaidPose value) {
		this.dataTracker.set(LittleMaidEntity.POSE, value);
	}

	public double getIntimacy() {
		return 0;
	}

	public AnimationState getEatAnimation() {
		return this.eatAnimation;
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
		this.setOwnerUuid(uuid);

		this.setSitting(nbt.getBoolean(LittleMaidEntity.KEY_IS_SITTING));
	}

	static {
		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		EATING_TICKS = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.INTEGER);
		POSE = DataTracker.registerData(LittleMaidEntity.class, ModEntities.DATA_MAID_POSE);

		SENSORS = ImmutableList.of(
				SensorType.HURT_BY,
				SensorType.NEAREST_LIVING_ENTITIES,
				SensorType.NEAREST_PLAYERS,
				ModEntities.SENSOR_MAID_ATTACKABLE,
				ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS,
				ModEntities.SENSOR_MAID_GUARDABLE_LIVING,
				ModEntities.SENSOR_SHOULD_EAT
		);

		MEMORY_MODULES = ImmutableList.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleType.HURT_BY,
				MemoryModuleType.HURT_BY_ENTITY,
				MemoryModuleType.IS_PANICKING,
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleType.PATH,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
				MemoryModuleType.ATTACK_COOLING_DOWN,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleType.MOBS,
				ModEntities.MEMORY_OWNER,
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_ATTRACTABLE_LIVINGS,
				ModEntities.MEMORY_GUARDABLE_LIVING,
				ModEntities.MEMORY_ATTRACT_TARGETS,
				ModEntities.MEMORY_GUARD_TARGET
		);
	}
}
