package io.github.zemelua.umu_little_maid.entity.maid;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.LittleMaidBrain;
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
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class LittleMaidEntity extends PathAwareEntity implements Tameable, InventoryOwner, RangedAttackMob {
	public static final EquipmentSlot[] ARMORS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.FEET};
	public static final Item[] CROPS = new Item[]{Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS};
	public static final Item[] PRODUCTS = new Item[]{Items.WHEAT, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS};
	public static final float LEFT_HAND_CHANCE = 0.15F;

	private static final TrackedData<Optional<UUID>> OWNER;
	private static final TrackedData<Boolean> IS_SITTING;
	private static final TrackedData<MaidPersonality> PERSONALITY;
	private static final TrackedData<MaidPose> POSE;

	private static final ImmutableList<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;
	private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;

	private final SimpleInventory inventory = new SimpleInventory(15);
	private final AnimationState eatAnimation = new AnimationState();

	private int eatingTicks;
	private boolean damageBlocked;

	public LittleMaidEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);

		this.eatingTicks = 0;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
	                             @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		Random random = world.getRandom();

		MaidPersonality personality = MaidSpawnHandler.randomPersonality(random, world.getBiome(this.getBlockPos()));
		this.setPersonality(personality);

		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(personality.getMaxHealth());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(personality.getMovementSpeed());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(personality.getAttackDamage());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(personality.getAttackKnockback());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(personality.getArmor());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(personality.getArmorToughness());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(personality.getKnockbackResistance());
		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_LUCK)).setBaseValue(personality.getLuck());

		Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE))
				.addPersistentModifier(new EntityAttributeModifier(
						"Random spawn bonus", random.nextTriangular(0.0, 0.11485000000000001),
						EntityAttributeModifier.Operation.MULTIPLY_BASE));

		this.setLeftHanded(random.nextDouble() < LittleMaidEntity.LEFT_HAND_CHANCE);

		this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.OFFHAND, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
		this.setEquipmentDropChance(EquipmentSlot.FEET, 2.0F);

		this.setCanPickUpLoot(true);

		return entityData;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		this.dataTracker.startTracking(LittleMaidEntity.OWNER, Optional.empty());
		this.dataTracker.startTracking(LittleMaidEntity.IS_SITTING, false);
		this.dataTracker.startTracking(LittleMaidEntity.PERSONALITY, ModEntities.PERSONALITY_BRAVERY);
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
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
				.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D)
				.add(EntityAttributes.GENERIC_ARMOR, 0.0D)
				.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.0D)
				.add(EntityAttributes.GENERIC_LUCK, 0.0D);
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

		UMULittleMaid.LOGGER.info(ModRegistries.MAID_PERSONALITY.getId(this.getPersonality()));

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
					this.playContractSound();

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
		this.playArcherAttackSound();

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

		this.damageBlocked = false;

		if (!this.world.isClient() && this.isSitting()) {
			this.setSitting(false);
		}

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
		if (Arrays.stream(LittleMaidEntity.CROPS).anyMatch(this.getOffHandStack()::isOf)) {
			return this.getOffHandStack();
		}

		for (int i = 0; i < this.inventory.size(); i++) {
			ItemStack itemStack = this.inventory.getStack(i);

			if (Arrays.stream(LittleMaidEntity.CROPS).anyMatch(itemStack::isOf)) {
				return itemStack;
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
			return Arrays.stream(LittleMaidEntity.CROPS).anyMatch(itemStack::isOf)
					|| Arrays.stream(LittleMaidEntity.PRODUCTS).anyMatch(itemStack::isOf);
		}

		return false;
	}

	public static boolean canSpawn(EntityType<LittleMaidEntity> ignoredType, ServerWorldAccess world, SpawnReason ignoredReason, BlockPos pos, Random ignoredRandom) {
		return world.getBlockState(pos.down()).isIn(BlockTags.RABBITS_SPAWNABLE_ON) && world.getBaseLightLevel(pos, 0) > 8;
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

	public void setPersonality(MaidPersonality value) {
		this.dataTracker.set(LittleMaidEntity.PERSONALITY, value);
	}

	public int getEatingTicks() {
		return this.eatingTicks;
	}

	public void setEatingTicks(int value) {
		this.eatingTicks = value;
	}

	public boolean hasAttackTarget() {
		return this.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET);
	}

	public boolean hasGuardTarget() {
		return this.getBrain().hasMemoryModule(ModEntities.MEMORY_GUARD_TARGET);
	}

	public boolean isPanicking() {
		return this.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
	}

	public MaidPose getAnimationPose() {
		return this.dataTracker.get(LittleMaidEntity.POSE);
	}

	public void setAnimationPose(MaidPose value) {
		this.dataTracker.set(LittleMaidEntity.POSE, value);
	}

	@SuppressWarnings("unused")
	public double getIntimacy() {
		return 0;
	}

	public AnimationState getEatAnimation() {
		return this.eatAnimation;
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
		if (!this.hasAttackTarget() && !this.hasGuardTarget() && !this.isPanicking()) {
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

	private static final String KEY_INVENTORY = "Inventory";
	private static final String KEY_SLOT = "Slot";
	private static final String KEY_OWNER = "Owner";
	private static final String KEY_IS_SITTING = "IsSitting";
	private static final String KEY_PERSONALITY = "Personality";

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

		@Nullable Identifier personality = ModRegistries.MAID_PERSONALITY.getId(this.getPersonality());
		if (personality != null) {
			nbt.putString(LittleMaidEntity.KEY_PERSONALITY, personality.toString());
		}
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

		if (nbt.contains(LittleMaidEntity.KEY_PERSONALITY)) {
			this.setPersonality(ModRegistries.MAID_PERSONALITY.get(Identifier.tryParse(nbt.getString(LittleMaidEntity.KEY_PERSONALITY))));
		}
	}

	static {
		OWNER = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
		IS_SITTING = DataTracker.registerData(LittleMaidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
		PERSONALITY = DataTracker.registerData(LittleMaidEntity.class, ModEntities.PERSONALITY_HANDLER);
		POSE = DataTracker.registerData(LittleMaidEntity.class, ModEntities.DATA_MAID_POSE);

		SENSORS = ImmutableList.of(
				SensorType.HURT_BY,
				SensorType.NEAREST_LIVING_ENTITIES,
				SensorType.NEAREST_PLAYERS,
				ModEntities.SENSOR_MAID_ATTACKABLE,
				ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS,
				ModEntities.SENSOR_MAID_GUARDABLE_LIVING,
				ModEntities.SENSOR_MAID_FARMABLE_POSES,
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
				ModEntities.MEMORY_GUARD_TARGET,
				ModEntities.MEMORY_FARMABLE_POSES,
				ModEntities.MEMORY_FARM_POS,
				ModEntities.MEMORY_FARM_COOLDOWN
		);
	}
}
