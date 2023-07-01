package io.github.zemelua.umu_little_maid.util;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public final class ModWorldUtils {
	/**
	 * {@link BlockView#raycast(RaycastContext)} にフィルターを追加したもの。
	 */
	public static BlockHitResult raycast(BlockView world, RaycastContext context, Predicate<BlockPos> filter) {
		return BlockView.raycast(context.getStart(), context.getEnd(), context, (bContext, pos) -> {
			BlockState blockState = world.getBlockState(pos);
			FluidState fluidState = world.getFluidState(pos);
			Vec3d from = bContext.getStart();
			Vec3d to = bContext.getEnd();
			VoxelShape blockShape = bContext.getBlockShape(blockState, world, pos);
			@Nullable BlockHitResult blockHit = filter.test(pos)
					? world.raycastBlock(from, to, pos, blockShape, blockState)
					: null;
			VoxelShape fluidShape = bContext.getFluidShape(fluidState, world, pos);
			@Nullable BlockHitResult fluidHit = fluidShape.raycast(from, to, pos);
			double blockDist = blockHit == null ? Double.MAX_VALUE : bContext.getStart().squaredDistanceTo(blockHit.getPos());
			double fluidDist = fluidHit == null ? Double.MAX_VALUE : bContext.getStart().squaredDistanceTo(fluidHit.getPos());
			return blockDist <= fluidDist ? blockHit : fluidHit;
		}, mContext -> {
			Vec3d raycastVec = mContext.getStart().subtract(mContext.getEnd());
			return BlockHitResult.createMissed(mContext.getEnd(), Direction.getFacing(raycastVec.getX(), raycastVec.getY(), raycastVec.getZ()), BlockPos.ofFloored(mContext.getEnd()));
		});
	}

	public static Box box(Vec3d centerPos, double radius) {
		return new Box(centerPos, centerPos).expand(radius);
	}

	public static boolean isAnyPoi(PointOfInterestStorage storage, BlockPos pos) {
		return storage.getType(pos).isPresent();
	}
}
