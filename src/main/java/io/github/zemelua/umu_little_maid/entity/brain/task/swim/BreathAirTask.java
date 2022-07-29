package io.github.zemelua.umu_little_maid.entity.brain.task.swim;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

import java.util.Map;

public class BreathAirTask<E extends MobEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	public BreathAirTask() {
		super(BreathAirTask.REQUIRED_MEMORIES, 20);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E mob, long time) {
		return mob.getAir() < mob.getMaxAir();
	}

	@Override
	protected void run(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		double x = mob.getX();
		double y = mob.getY();
		double z = mob.getZ();
		Iterable<BlockPos> iterable = BlockPos.iterate(
				MathHelper.floor(x - 1.0), mob.getBlockY(), MathHelper.floor(z - 1.0),
				MathHelper.floor(x + 1.0), MathHelper.floor(y + 8.0), MathHelper.floor(z + 1.0)
		);

		for (BlockPos pos : iterable) {
			if (BreathAirTask.isAirPos(world, pos)) {
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 0));

				return;
			}
		}

		brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(mob.getBlockPos().up(8), 1.0F, 0));
	}

	@Override
	protected void keepRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		if (world.getBlockState(mob.getBlockPos()).isAir() || world.getBlockState(mob.getBlockPos().up()).isAir()) {
			if (mob.getRandom().nextFloat() < 0.8F) {
				mob.getJumpControl().setActive();

				if (brain.hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
					brain.forget(MemoryModuleType.WALK_TARGET);
				}
			}
		}
	}

	public static boolean isAirPos(WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);

		return (world.getFluidState(pos).isEmpty() || blockState.isOf(Blocks.BUBBLE_COLUMN)) && blockState.canPathfindThrough(world, pos, NavigationType.LAND);
	}
}
