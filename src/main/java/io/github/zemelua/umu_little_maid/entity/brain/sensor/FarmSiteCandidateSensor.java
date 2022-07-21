package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
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

public class FarmSiteCandidateSensor extends Sensor<MobEntity> {
	@Override
	protected void sense(ServerWorld world, MobEntity mob) {
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois = poiStorage.getTypesAndPositions(
						registryEntry -> registryEntry.isIn(ModTags.POI_FARMER),
						pos -> true,
						mob.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY)
				.collect(Collectors.toSet());

		@Nullable Path path = FindPointOfInterestTask.findPathToPoi(mob, pois);
		if (path != null && path.reachesTarget()) {
			BlockPos pos = path.getTarget();
			Optional<RegistryEntry<PointOfInterestType>> type = poiStorage.getType(pos);
			if (type.isPresent()) {
				Brain<?> brain = mob.getBrain();
				brain.remember(ModEntities.MEMORY_FARM_SITE_CANDIDATE, GlobalPos.create(world.getRegistryKey(), pos));
			}
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModEntities.MEMORY_FARM_SITE_CANDIDATE);
	}
}
