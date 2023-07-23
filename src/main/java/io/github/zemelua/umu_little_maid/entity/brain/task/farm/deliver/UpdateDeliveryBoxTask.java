package io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdateDeliveryBoxTask<M extends LivingEntity & ILittleMaidEntity> extends EveryTickTask<M> {
	@Override
	public void tick(ServerWorld world, M maid, long time) {
		Brain<?> brain = maid.getBrain();

		Collection<GlobalPos> instructedBoxes = maid.getDeliveryBoxes();
		Optional<List<Pair<GlobalPos, Long>>> undeliverableBoxes = brain.getOptionalRegisteredMemory(ModMemories.UNDELIVERABLE_BOXES);
		Optional<BlockPos> box = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);

		brain.remember(ModMemories.UNDELIVERABLE_BOXES, undeliverableBoxes.orElse(List.of()).stream()
				.filter(p -> p.getSecond() >= time)
				.collect(Collectors.toList()));

		if (box.isEmpty()) {
			Optional<BlockPos> nearestInstructedBox = instructedBoxes.stream()
					.filter(maid::isDeliverableBox)
					.map(GlobalPos::getPos)
					.filter(p -> {
						@Nullable Path path = maid.getNavigation().findPathTo(p, 0);

						return path != null && path.reachesTarget();
					}).min(Comparator.comparingDouble(g -> maid.squaredDistanceTo(Vec3d.of(g))));

			nearestInstructedBox.ifPresent(blockPos -> brain.remember(ModMemories.DELIVERY_BOX, blockPos));
		} else if (!maid.isDelivering()) {
			@Nullable Path path = maid.getNavigation().findPathTo(box.get(), 0);

			if (path == null
					|| !path.reachesTarget()
					|| !maid.isSettableAsDeliveryBox(world, box.get())
					|| !maid.isDeliverableBox(GlobalPos.create(world.getRegistryKey(), box.get()))) {
				brain.forget(ModMemories.DELIVERY_BOX);
			}
		}
	}
}
