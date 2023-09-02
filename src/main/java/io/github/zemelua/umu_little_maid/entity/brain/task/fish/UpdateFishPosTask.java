package io.github.zemelua.umu_little_maid.entity.brain.task.fish;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateFishPosTask<E extends PathAwareEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E mob, long time) {
		if (time % 10 != 0) return;

		Brain<?> brain = mob.getBrain();
		Optional<Collection<BlockPos>> fishableWaters = brain.getOptionalRegisteredMemory(ModMemories.FISHABLE_WATERS);
		Optional<Pair<BlockPos, BlockPos>> fishPos = brain.getOptionalRegisteredMemory(ModMemories.FISH_POS);

		if (fishableWaters.isPresent() && fishPos.isEmpty()) {
			Box scope = Box.of(mob.getSteppingPos().toCenterPos(), 5.0D, 2.0D, 5.0D);
			List<BlockPos> fishablePoses = BlockPos.stream(scope)
					.map(p -> {

						float pen = mob.getPathfindingPenalty(PathNodeType.WATER_BORDER);
						mob.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
						@Nullable Vec3d pos = NoWaterTargeting.find(mob, 7, 3, 0, p.toCenterPos().subtract(mob.getPos()), 2.0D);
						mob.setPathfindingPenalty(PathNodeType.WATER_BORDER, pen);

						if (pos == null || world.getBlockState(BlockPos.ofFloored(pos).down()).isAir()
								|| world.getFluidState(BlockPos.ofFloored(pos).down()).isIn(FluidTags.WATER)
								|| !isWaterBorder(world, BlockPos.ofFloored(pos).down())) return null;

						return world.getFluidState(BlockPos.ofFloored(pos)).isEmpty() ? BlockPos.ofFloored(pos) : null;

					}).filter(Objects::nonNull)
					.collect(Collectors.toList());
			Collections.shuffle(fishablePoses);
			List<BlockPos> newFishableWaters = new ArrayList<>(fishableWaters.get());
			Collections.shuffle(newFishableWaters);
			for (BlockPos pos : fishablePoses) {
				Optional<BlockPos> throwableToWater = searchThrowableToWater(world, newFishableWaters.stream(), pos);
				throwableToWater.ifPresent(p -> {
					brain.remember(ModMemories.FISH_POS, Pair.of(pos, p), 150L);
				});
			}
		}
	}

	public static boolean isWaterBorder(World world, BlockPos pos) {
		for (Direction dir: Direction.Type.HORIZONTAL.stream().collect(Collectors.toSet())) {
			if (world.getBlockState(pos.offset(dir)).isOf(Blocks.WATER)) return true;
		}

		return false;
	}

	public static Optional<BlockPos> searchThrowableToWater(World world, Stream<BlockPos> fishableWaters, BlockPos from) {
		return fishableWaters.filter(p -> p.isWithinDistance(from, 4.0D))
				.findAny();
	}
}
