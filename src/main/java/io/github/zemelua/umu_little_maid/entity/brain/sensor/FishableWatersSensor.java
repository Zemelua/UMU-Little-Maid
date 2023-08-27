package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Set;
import java.util.stream.Collectors;

public class FishableWatersSensor extends Sensor<LivingEntity> {
	@Override
	protected void sense(ServerWorld world, LivingEntity living) {
		Brain<?> brain = living.getBrain();
		Box scope = Box.of(living.getPos(), 8.0D, 4.0D, 8.0D);
		Set<BlockPos> fishableWaters = BlockPos.stream(scope)
				.filter(p -> {
					BlockState blockState = world.getBlockState(p);
					BlockState uBlockState = world.getBlockState(p.up());
					return blockState.isOf(Blocks.WATER) && (uBlockState.isAir() || uBlockState.isOf(Blocks.LILY_PAD));
				}).collect(Collectors.toSet());
		Set<BlockPos> fishableOpenWaters = fishableWaters.stream()
				.filter(p -> ModUtils.isOpenWater(world, p))
				.collect(Collectors.toSet());

		brain.remember(ModMemories.FISHABLE_WATERS, fishableWaters);
		brain.remember(ModMemories.FISHABLE_OPEN_WATERS, fishableOpenWaters);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(
				ModMemories.FISHABLE_WATERS,
				ModMemories.FISHABLE_OPEN_WATERS
		);
	}
}
