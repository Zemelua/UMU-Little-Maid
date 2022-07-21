package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;

public class UpdateFarmSiteTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	public UpdateFarmSiteTask() {
		super(UpdateFarmSiteTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (brain.hasMemoryModule(ModEntities.MEMORY_FARM_SITE)) {
			Optional<GlobalPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_SITE);

			if (pos.isPresent() && pos.get().getDimension() == world.getRegistryKey() && !UpdateFarmSiteTask.isSite(world, pos.get().getPos())) {
				brain.forget(ModEntities.MEMORY_FARM_SITE);
				UpdateFarmSiteTask.playLostSiteParticles(world, living);
			}
		}

		if (brain.hasMemoryModule(ModEntities.MEMORY_FARM_SITE_CANDIDATE) && !brain.hasMemoryModule(ModEntities.MEMORY_FARM_SITE)) {
			Optional<GlobalPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_SITE_CANDIDATE);

			if (pos.isPresent() && pos.get().getDimension() == world.getRegistryKey() && UpdateFarmSiteTask.isSite(world, pos.get().getPos())) {
				brain.remember(ModEntities.MEMORY_FARM_SITE, pos);
				UpdateFarmSiteTask.playFoundSiteParticles(world, living);
				UpdateFarmSiteTask.playFoundSiteParticles(world, living, pos.get().getPos());
			}
		}
	}

	public static boolean isSite(World world, BlockPos pos) {
		return UpdateFarmSiteTask.isComposter(world, pos) || UpdateFarmSiteTask.isScarecrow(world, pos);
	}

	public static boolean isComposter(World world, BlockPos pos) {
		return world.getBlockState(pos).isOf(Blocks.COMPOSTER);
	}

	public static boolean isScarecrow(World world, BlockPos pos) {
		return world.getBlockState(pos).isIn(BlockTags.WOODEN_FENCES)
				&& world.getBlockState(pos.up()).isOf(Blocks.HAY_BLOCK)
				&& world.getBlockState(pos.up(2)).isIn(ModTags.BLOCK_SCARECROW_HEAD);
	}

	public static <E extends LivingEntity> void playFoundSiteParticles(ServerWorld world, E living) {
		for (int i = 0; i < 5; i++) {
			double posX = living.getParticleX(1.0);
			double posY = living.getRandomBodyY() + 1.0;
			double posZ = living.getParticleZ(1.0);
			double deltaX = living.getRandom().nextGaussian() * 0.02;
			double deltaY = living.getRandom().nextGaussian() * 0.02;
			double deltaZ = living.getRandom().nextGaussian() * 0.02;

			world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
		}
	}

	public static <E extends LivingEntity> void playFoundSiteParticles(ServerWorld world, E living, BlockPos pos) {
		for (int i = 0; i < 5; i++) {
			double posX = pos.getX() + (2.0 * living.getRandom().nextDouble() - 1.0D);
			double posY = pos.getY() + living.getRandom().nextDouble();
			double posZ = pos.getZ() + (2.0 * living.getRandom().nextDouble() - 1.0D);
			double deltaX = living.getRandom().nextGaussian() * 0.02;
			double deltaY = living.getRandom().nextGaussian() * 0.02;
			double deltaZ = living.getRandom().nextGaussian() * 0.02;

			world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
		}
	}

	public static <E extends LivingEntity> void playLostSiteParticles(ServerWorld world, E living) {
		for (int i = 0; i < 5; i++) {
			double posX = living.getParticleX(1.0);
			double posY = living.getRandomBodyY() + 1.0;
			double posZ = living.getParticleZ(1.0);
			double deltaX = living.getRandom().nextGaussian() * 0.02;
			double deltaY = living.getRandom().nextGaussian() * 0.02;
			double deltaZ = living.getRandom().nextGaussian() * 0.02;

			world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
		}
	}
}
