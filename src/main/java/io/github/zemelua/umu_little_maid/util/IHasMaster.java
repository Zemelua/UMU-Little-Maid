package io.github.zemelua.umu_little_maid.util;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public interface IHasMaster {
	Optional<PlayerEntity> getMaster();
}
