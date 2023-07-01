package io.github.zemelua.umu_little_maid.entity.brain.task.look;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.entity.ai.brain.MemoryModuleState.*;
import static net.minecraft.entity.ai.brain.MemoryModuleType.*;

public class LookAtBlockTask<E extends LivingEntity> extends MultiTickTask<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			LOOK_TARGET, VALUE_ABSENT,
			WALK_TARGET, VALUE_ABSENT
	);

	private final RegistryKey<PointOfInterestType> poi;

	public LookAtBlockTask(RegistryKey<PointOfInterestType> poi) {
		super(REQUIRED_MEMORIES, 60);

		this.poi = poi;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		PointOfInterestStorage pois = world.getPointOfInterestStorage();
		Optional<BlockPos> pos = pois.getNearestPosition(poi -> poi.matchesKey(this.poi), living.getBlockPos(), 10, OccupationStatus.ANY);

		pos.ifPresent(targetValue -> brain.remember(LOOK_TARGET, new BlockPosLookTarget(targetValue.add(0, 1, 0))));
	}
}
