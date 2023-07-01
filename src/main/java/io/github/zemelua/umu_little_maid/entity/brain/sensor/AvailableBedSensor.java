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

import java.util.*;
import java.util.stream.Collectors;

public class AvailableBedSensor extends Sensor<MobEntity> {
	private final Map<BlockPos, Long> pos2ExpiryTime = new HashMap<>();
	private int tries;
	private long expiryTime;

	@Override
	protected void sense(ServerWorld world, MobEntity mob) {
		this.tries = 0;
		this.expiryTime = world.getTime() + world.getRandom().nextInt(20);
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois = poiStorage.getTypesAndPositions(poi -> poi.matchesKey(PointOfInterestTypes.HOME), pos -> {
			if (this.pos2ExpiryTime.containsKey(pos)) return false;
			if (++this.tries >= 5) return false;

			Collection<BlockPos> homes = mob.getBrain().getOptionalMemory(MemoryModuleType.MOBS)
					.orElse(new ArrayList<>()).stream()
					.filter(living -> living instanceof LittleMaidEntity)
					.map(living -> (LittleMaidEntity) living)
					.map(LittleMaidEntity::getHome)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.map(GlobalPos::getPos)
					.toList();

			if (homes.contains(pos)) return false;

			this.pos2ExpiryTime.put(pos, this.expiryTime + 40L);
			return true;
		}, mob.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE).collect(Collectors.toSet());
		Path path = FindPointOfInterestTask.findPathToPoi(mob, pois);
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			if (ModWorldUtils.isAnyPoi(poiStorage, blockPos)) {
				mob.getBrain().remember(ModMemories.AVAILABLE_BED, blockPos, 100);
			}
		} else if (this.tries < 5) {
			this.pos2ExpiryTime.entrySet().removeIf(entry -> entry.getValue() < this.expiryTime);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModMemories.AVAILABLE_BED);
	}
}
