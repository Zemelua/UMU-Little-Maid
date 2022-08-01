package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class ForgetFarmSiteTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARM_SITE, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetFarmSiteTask() {
		super(ForgetFarmSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_FARM_SITE).ifPresent(pos -> {
			if (pos.getDimension() != world.getRegistryKey() || !RememberFarmSiteTask.isSite(world, pos.getPos())) {
				brain.forget(ModEntities.MEMORY_FARM_SITE);
				ForgetFarmSiteTask.playLostSiteParticles(world, living);
			}
		});
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
