package io.github.zemelua.umu_little_maid.entity.brain.task.fish;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
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
			Collection<BlockPos> fishablePoses = BlockPos.stream(scope)
					.map(p -> {

						@Nullable Vec3d pos = NoWaterTargeting.find(mob, 7, 3, 0, p.toCenterPos().subtract(mob.getPos()), 2.0D);


						if (pos == null || world.getBlockState(BlockPos.ofFloored(pos).down()).isAir()
								|| world.getFluidState(BlockPos.ofFloored(pos).down()).isIn(FluidTags.WATER)) return null;

						return world.getFluidState(BlockPos.ofFloored(pos)).isEmpty() ? BlockPos.ofFloored(pos) : null;

					}).filter(Objects::nonNull)
					.collect(Collectors.toSet());
			for (BlockPos pos : fishablePoses) {
				Optional<BlockPos> throwableToWater = searchThrowableToWater(world, fishableWaters.get().stream(), pos);
				throwableToWater.ifPresent(p -> {
					brain.remember(ModMemories.FISH_POS, Pair.of(pos, p), 150L);
				});
			}
		}
	}

	public static Optional<BlockPos> searchThrowableToWater(World world, Stream<BlockPos> fishableWaters, BlockPos from) {
		return fishableWaters.filter(p -> p.isWithinDistance(from, 4.0D))
				.findAny();
	}
}
