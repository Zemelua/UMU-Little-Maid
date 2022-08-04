package io.github.zemelua.umu_little_maid.entity.brain.task.engage;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class RememberJobSiteTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_JOB_SITE_CANDIDATE, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_JOB_SITE, MemoryModuleState.VALUE_ABSENT
	);

	public RememberJobSiteTask() {
		super(RememberJobSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.isEngaging();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_JOB_SITE_CANDIDATE).ifPresent(pos -> {
			if (pos.getDimension() == world.getRegistryKey() && this.isSite(world, pos.getPos())) {
				brain.remember(ModEntities.MEMORY_JOB_SITE, pos);
				RememberJobSiteTask.playFoundSiteParticles(world, maid);
				RememberJobSiteTask.playFoundSiteParticles(world, maid, pos.getPos());
			}
		});
	}

	protected boolean isSite(World world, BlockPos pos) {
		return true;
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
