package io.github.zemelua.umu_little_maid.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Optional;

public interface IInstructable {
	Optional<GlobalPos> getHome();
	void setHome(GlobalPos value);
	void removeHome();
	boolean isSettableAsHome(World world, BlockPos pos);

	default boolean isHome(World world, BlockPos pos) {
		return this.getHome().filter(h -> ModUtils.isSameObject(world, pos, h)).isPresent();
	}

	Collection<GlobalPos> getDeliveryBoxes();
	void addDeliveryBox(GlobalPos value);
	void removeDeliveryBox(GlobalPos value);
	boolean isSettableAsDeliveryBox(World world, BlockPos pos);

	default boolean isDeliveryBox(World world, BlockPos pos) {
		return this.getDeliveryBoxes().stream().anyMatch(b -> ModUtils.isSameObject(world, pos, b));
	}

	default boolean isSettableAsAnySite(World world, BlockPos pos) {
		return this.isSettableAsHome(world, pos) || this.isSettableAsDeliveryBox(world, pos);
	}

	default boolean isAnySite(World world, BlockPos pos) {
		return this.isHome(world, pos) || this.isDeliveryBox(world, pos);
	}
}
