package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JobSiteCandidateSensor extends Sensor<LittleMaidEntity> {
	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois = poiStorage.getTypesAndPositions(
						registryEntry -> maid.getJob().isJobSite(registryEntry),
						pos -> true,
						maid.getBlockPos(), 20, PointOfInterestStorage.OccupationStatus.ANY)
				.collect(Collectors.toSet());

		Brain<?> brain = maid.getBrain();
		@Nullable Path path = FindPointOfInterestTask.findPathToPoi(maid, pois);
		if (path != null && path.reachesTarget()) {
			BlockPos pos = path.getTarget();
			Optional<RegistryEntry<PointOfInterestType>> type = poiStorage.getType(pos);
			if (type.isPresent()) {
				brain.remember(ModEntities.MEMORY_JOB_SITE_CANDIDATE, GlobalPos.create(world.getRegistryKey(), pos));

				return;
			}
		}

		brain.forget(ModEntities.MEMORY_JOB_SITE_CANDIDATE);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModEntities.MEMORY_JOB_SITE_CANDIDATE);
	}
}
