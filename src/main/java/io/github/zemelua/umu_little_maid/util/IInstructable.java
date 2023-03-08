package io.github.zemelua.umu_little_maid.util;

import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public interface IInstructable {
	Optional<GlobalPos> getHome();

	void setHome(GlobalPos value);

	void removeHome();
}
