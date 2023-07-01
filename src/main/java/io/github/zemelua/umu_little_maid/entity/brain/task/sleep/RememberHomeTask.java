package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * {@code MemoryModuleType.NEAREST_BED} をもとにして {@code MemoryModuleType.HOME} の値を設定するよ！
 */
public class RememberHomeTask<E extends LivingEntity> extends MultiTickTask<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.NEAREST_BED, MemoryModuleState.VALUE_PRESENT,
			MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT
	);

	public RememberHomeTask() {
		super(RememberHomeTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<BlockPos> pos = brain.getOptionalMemory(MemoryModuleType.NEAREST_BED);

		pos.ifPresent(posObject -> {
			if (RememberHomeTask.isHome(world.getBlockState(pos.get()))) {
				brain.remember(MemoryModuleType.HOME, GlobalPos.create(world.getRegistryKey(), posObject));
			}
		});
	}

	public static boolean isHome(BlockState blockState) {
		return Objects.requireNonNull(Registries.POINT_OF_INTEREST_TYPE.get(PointOfInterestTypes.HOME)).contains(blockState);
	}
}
