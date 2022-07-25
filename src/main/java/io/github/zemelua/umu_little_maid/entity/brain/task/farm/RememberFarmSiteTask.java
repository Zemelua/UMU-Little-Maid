package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

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
import net.minecraft.world.World;

import java.util.Map;

public class RememberFarmSiteTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARM_SITE_CANDIDATE, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_FARM_SITE, MemoryModuleState.VALUE_ABSENT
	);

	public RememberFarmSiteTask() {
		super(RememberFarmSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_FARM_SITE_CANDIDATE).ifPresent(pos -> {
			if (pos.getDimension() == world.getRegistryKey() && RememberFarmSiteTask.isSite(world, pos.getPos())) {
				brain.remember(ModEntities.MEMORY_FARM_SITE, pos);
				RememberFarmSiteTask.playFoundSiteParticles(world, living);
				RememberFarmSiteTask.playFoundSiteParticles(world, living, pos.getPos());
			}
		});
	}

	public static boolean isSite(World world, BlockPos pos) {
		return RememberFarmSiteTask.isComposter(world, pos) || RememberFarmSiteTask.isScarecrow(world, pos);
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
}
