package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModWorldUtils;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AvailableBedSensor extends Sensor<MobEntity> {
	@Override
	protected void sense(ServerWorld world, MobEntity mob) {
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();

		Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois = poiStorage.getTypesAndPositions(poi -> poi.matchesKey(PointOfInterestTypes.HOME), pos -> {

			Collection<BlockPos> homes = mob.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS)
					.orElse(new ArrayList<>()).stream()
					.filter(living -> living instanceof LittleMaidEntity)
					.map(living -> (LittleMaidEntity) living)
					.map(LittleMaidEntity::getHome)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.map(GlobalPos::getPos)
					.toList();

			return !homes.contains(pos);
		}, mob.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toSet());


		Path path = FindPointOfInterestTask.findPathToPoi(mob, pois);
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			if (ModWorldUtils.isAnyPoi(poiStorage, blockPos)) {
				mob.getBrain().remember(ModMemories.AVAILABLE_BED, blockPos, 100);
			}
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModMemories.AVAILABLE_BED);
	}
}
