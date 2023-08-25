package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.api.IFisher;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * {@link net.minecraft.entity.projectile.FishingBobberEntity} をプレイヤー以外のエンティティが使用できるようにしたもの。
 * 魚がかかると自動的に縄が引かれます。
 */
public class ModFishingBobberEntity extends ProjectileEntity {
	private int outOfOpenWaterTicks;
	private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(ModFishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CAUGHT_FISH = DataTracker.registerData(ModFishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int hookCountdown;
	private int waitCountdown;
	private int fishTravelCountdown;
	private boolean inOpenWater = true;
	private FishingBobberEntity.State state = FishingBobberEntity.State.FLYING;
	private final int luckLevel;
	private final int lureLevel;

	private boolean caughtFish = false;

	public <E extends LivingEntity & IFisher> ModFishingBobberEntity(World world, E thrower, int luckLevel, int lureLevel) {
		this(ModEntities.SIMPLE_FISHING_BOBBER, world, luckLevel, lureLevel);

		this.setOwner(thrower);
		float pitch = thrower.getPitch();
		float yaw = thrower.getYaw();
		float h = MathHelper.cos(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
		float i = MathHelper.sin(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
		float j = -MathHelper.cos(-pitch * ((float)Math.PI / 180));
		float k = MathHelper.sin(-pitch * ((float)Math.PI / 180));
		double d = thrower.getX() - (double)i * 0.3;
		double e = thrower.getEyeY();
		double l = thrower.getZ() - (double)h * 0.3;
		this.refreshPositionAndAngles(d, e, l, yaw, pitch);
		Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0f, 5.0f), -h);
		double m = vec3d.length();
		vec3d = vec3d.multiply(0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365));
		this.setVelocity(vec3d);
		this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
		this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875));
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();

		this.ignoreCameraFrustum = true;
	}

	protected ModFishingBobberEntity(EntityType<? extends ModFishingBobberEntity> entityType, World world) {
		this(entityType, world, 0, 0);
	}

	private ModFishingBobberEntity(EntityType<? extends ModFishingBobberEntity> entityType, World world, int luckLevel, int lureLevel) {
		super(entityType, world);

		this.luckLevel = Math.max(0, luckLevel);
		this.lureLevel = Math.max(0, lureLevel);
	}

	@Override
	protected void initDataTracker() {

	}

	public void pull(ItemStack fishingRod) {
		if (this.getWorld().isClient()) return;
		int i = 0;

		if (this.hookCountdown > 0) {
			LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld)this.getWorld())
					.add(LootContextParameters.ORIGIN, this.getPos())
					.add(LootContextParameters.TOOL, fishingRod)
					.add(LootContextParameters.THIS_ENTITY, this)
					.luck((float)this.luckLevel)
					.build(LootContextTypes.FISHING);
			LootTable lootTable = this.getWorld().getServer().getLootManager().getLootTable(LootTables.FISHING_GAMEPLAY);
			ObjectArrayList<ItemStack> list = lootTable.generateLoot(lootContextParameterSet);
			for (ItemStack itemStack : list) {
				ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), itemStack);
				double d = this.getOwner().getX() - this.getX();
				double e = this.getOwner().getY() - this.getY();
				double f = this.getOwner().getZ() - this.getZ();
				double g = 0.1;
				itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
				this.getWorld().spawnEntity(itemEntity);
				this.getOwner().getWorld().spawnEntity(new ExperienceOrbEntity(this.getOwner().getWorld(), this.getOwner().getX(), this.getOwner().getY() + 0.5, this.getOwner().getZ() + 0.5, this.random.nextInt(6) + 1));
			}
			i = 1;
		}

		this.discard();
	}

	@Override
	public void tick() {
		UMULittleMaid.LOGGER.info(this.shouldRemove());
		super.tick();
		if (!this.getWorld().isClient() && this.shouldRemove()) {
			this.discard();
			return;
		}

		float fluidHeight = 0.0f;
		BlockPos blockPos = this.getBlockPos();
		FluidState fluidState = this.getWorld().getFluidState(blockPos);
		if (fluidState.isIn(FluidTags.WATER)) {
			fluidHeight = fluidState.getHeight(this.getWorld(), blockPos);
		}
		boolean bl2 = fluidHeight > 0.0F;
		if (this.state == FishingBobberEntity.State.FLYING) {
			// if (fluidHeight > 0.0F) {
			if (true) {
				this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
				this.state = FishingBobberEntity.State.BOBBING;
				return;
			}
			this.onCollision(ProjectileUtil.getCollision(this, e -> false));
		} else {
			if (this.state == FishingBobberEntity.State.BOBBING) {
				Vec3d vec3d = this.getVelocity();
				double d = this.getY() + vec3d.y - (double)blockPos.getY() - (double)fluidHeight;
				if (Math.abs(d) < 0.01) {
					d += Math.signum(d) * 0.1;
				}
				this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);
				this.inOpenWater = this.fishTravelCountdown <= 0 || this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(blockPos);
				// if (bl2) {
				if (true) {
					this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
					if (this.caughtFish) {
						this.setVelocity(this.getVelocity().add(0.0, -0.1 * (double)this.random.nextFloat() * (double)this.random.nextFloat(), 0.0));
					}
					if (!this.getWorld().isClient) {
						this.tickFishingLogic(blockPos);
					}
				} else {
					this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
				}
			}
		}
		if (!fluidState.isIn(FluidTags.WATER)) {
			this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
		}
		this.move(MovementType.SELF, this.getVelocity());
		this.updateRotation();
		if (this.state == FishingBobberEntity.State.FLYING && (this.isOnGround() || this.horizontalCollision)) {
			this.setVelocity(Vec3d.ZERO);
		}
		double e = 0.92;
		this.setVelocity(this.getVelocity().multiply(0.92));
		this.refreshPosition();
	}

	private void tickFishingLogic(BlockPos pos) {

		ServerWorld serverWorld = (ServerWorld)this.getWorld();
		int i = 1;
		BlockPos upPos = pos.up();
		if (this.getWorld().hasRain(upPos) && this.random.nextFloat() < 0.25F) {
			i++;
		}
		if (!this.getWorld().isSkyVisible(upPos) && this.random.nextFloat() < 0.5F) {
			i--;
		}
		if (this.hookCountdown > 0) {
			--this.hookCountdown;
			if (this.hookCountdown <= 0) {
				this.discard();
			}
		} else if (this.fishTravelCountdown > 0) {
			this.fishTravelCountdown -= i;
			if (this.fishTravelCountdown <= 0) {
				this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
				double m = this.getY() + 0.5;
				serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
				serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
				this.hookCountdown = MathHelper.nextInt(this.random, 9999, 9999);
				((IFisher) Objects.requireNonNull(this.getOwner())).onHitFish();
				this.caughtFish = true;
			}
		} else if (this.waitCountdown > 0) {
			this.waitCountdown -= i;
			float f = 0.15f;
			if (this.waitCountdown < 20) {
				f += (float)(20 - this.waitCountdown) * 0.05f;
			} else if (this.waitCountdown < 40) {
				f += (float)(40 - this.waitCountdown) * 0.02f;
			} else if (this.waitCountdown < 60) {
				f += (float)(60 - this.waitCountdown) * 0.01f;
			}
			if (this.random.nextFloat() < f) {
				double j;
				double e;
				float g = MathHelper.nextFloat(this.random, 0.0f, 360.0f) * ((float)Math.PI / 180);
				float h = MathHelper.nextFloat(this.random, 25.0f, 60.0f);
				double d = this.getX() + (double)(MathHelper.sin(g) * h) * 0.1;
				BlockState blockState = serverWorld.getBlockState(BlockPos.ofFloored(d, (e = (double)((float)MathHelper.floor(this.getY()) + 1.0f)) - 1.0, j = this.getZ() + (double)(MathHelper.cos(g) * h) * 0.1));
				if (blockState.isOf(Blocks.WATER)) {
					serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1f, 0.0, 0.1f, 0.0);
				}
			}
			if (this.waitCountdown <= 0) {
				this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
			}
		} else {
			this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
			this.waitCountdown -= this.lureLevel * 20 * 5;
		}
	}

	private boolean isOpenOrWaterAround(BlockPos pos) {
		FishingBobberEntity.PositionType positionType = FishingBobberEntity.PositionType.INVALID;
		for (int i = -1; i <= 2; ++i) {
			FishingBobberEntity.PositionType positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2));
			switch (positionType2) {
				case INVALID -> {
					return false;
				}
				case ABOVE_WATER -> {
					if (positionType != FishingBobberEntity.PositionType.INVALID) break;
					return false;
				}
				case INSIDE_WATER -> {
					if (positionType != FishingBobberEntity.PositionType.ABOVE_WATER) break;
					return false;
				}
			}
			positionType = positionType2;
		}
		return true;
	}

	private FishingBobberEntity.PositionType getPositionType(BlockPos start, BlockPos end) {
		return BlockPos.stream(start, end).map(this::getPositionType).reduce((positionType, positionType2) -> positionType == positionType2 ? positionType : FishingBobberEntity.PositionType.INVALID).orElse(FishingBobberEntity.PositionType.INVALID);
	}

	private FishingBobberEntity.PositionType getPositionType(BlockPos pos) {
		BlockState blockState = this.getWorld().getBlockState(pos);
		if (blockState.isAir() || blockState.isOf(Blocks.LILY_PAD)) {
			return FishingBobberEntity.PositionType.ABOVE_WATER;
		}
		FluidState fluidState = blockState.getFluidState();
		if (fluidState.isIn(FluidTags.WATER) && fluidState.isStill() && blockState.getCollisionShape(this.getWorld(), pos).isEmpty()) {
			return FishingBobberEntity.PositionType.INSIDE_WATER;
		}
		return FishingBobberEntity.PositionType.INVALID;
	}

	private boolean shouldRemove() {
		if (this.isOnGround() || this.getOwner() == null) return true;

		if (this.getOwner() instanceof LivingEntity ownerLiving) {
			return !ownerLiving.getMainHandStack().isOf(Items.FISHING_ROD);
		}

		return this.getOwner().isRemoved()
				|| !this.getOwner().isAlive()
				|| this.getOwner().distanceTo(this) < 32.0D
				|| !(this.getOwner() instanceof IFisher);
	}
}
