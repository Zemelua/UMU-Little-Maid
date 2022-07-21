package io.github.zemelua.umu_little_maid.unused;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class MaidContracts {
	public static final Map<PlayerEntity, Map<UUID, NbtCompound>> MAIDS = new HashMap<>();

	public static void addMaid(PlayerEntity player, LittleMaidEntity maid) {
		if (maid.getOwnerUuid() == player.getUuid()) {
			NbtCompound nbt = new NbtCompound();
			NbtCompound nbt1 = new NbtCompound();
			maid.saveNbt(nbt1);
			nbt.put(EntityType.ENTITY_TAG_KEY, nbt1);
			UMULittleMaid.LOGGER.info(nbt.toString());
			MaidContracts.MAIDS.computeIfAbsent(player, playerArg -> new java.util.HashMap<>()).put(maid.getUuid(), nbt);
		}
	}

	public static Map<UUID, NbtCompound> getMaids(PlayerEntity player) {
		return MaidContracts.MAIDS.computeIfAbsent(player, playerArg -> new HashMap<>());
	}
}
