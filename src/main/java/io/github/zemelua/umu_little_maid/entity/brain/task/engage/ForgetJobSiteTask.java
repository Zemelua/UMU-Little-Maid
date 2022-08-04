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
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Map;
import java.util.Optional;

public class ForgetJobSiteTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_JOB_SITE, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetJobSiteTask() {
		super(ForgetJobSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_JOB_SITE).ifPresent(pos -> {
			if (pos.getDimension() != world.getRegistryKey() || this.isNotSite(world, pos.getPos(), maid) || !maid.isEngaging()) {
				brain.forget(ModEntities.MEMORY_JOB_SITE);
				brain.forget(ModEntities.MEMORY_JOB_SITE_CANDIDATE);
				ForgetJobSiteTask.playLostSiteParticles(world, maid);
			}
		});
	}

	protected boolean isNotSite(ServerWorld world, BlockPos pos, LittleMaidEntity maid) {
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Optional<RegistryEntry<PointOfInterestType>> poi = poiStorage.getType(pos);

		return poi
				.map(poiObject -> !maid.getJob().isJobSite(poiObject))
				.orElse(true);
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
