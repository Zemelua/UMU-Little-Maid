package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeCandidateSensor extends Sensor<MobEntity> {
	private static final Set<MemoryModuleType<?>> OUTPUT_MEMORIES = ImmutableSet.of(
			MemoryModuleType.NEAREST_BED
	);

	@Override
	protected void sense(ServerWorld world, MobEntity mob) {
		PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();
		Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois = poiStorage.getTypesAndPositions(
						registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME),
						pos -> {
							BlockState blockState =  world.getBlockState(pos);
							if (blockState.isIn(BlockTags.BEDS)) {
								return !blockState.get(BedBlock.OCCUPIED);
							}

							return false;
						}, mob.getBlockPos(), 30, PointOfInterestStorage.OccupationStatus.ANY)
				.collect(Collectors.toSet());

		@Nullable Path path = FindPointOfInterestTask.findPathToPoi(mob, pois);
		if (path != null && path.reachesTarget()) {
			BlockPos pos = path.getTarget();
			Optional<RegistryEntry<PointOfInterestType>> type = poiStorage.getType(pos);
			if (type.isPresent()) {
				Brain<?> brain = mob.getBrain();
				brain.remember(MemoryModuleType.NEAREST_BED, pos);
			}
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return HomeCandidateSensor.OUTPUT_MEMORIES;
	}
}
