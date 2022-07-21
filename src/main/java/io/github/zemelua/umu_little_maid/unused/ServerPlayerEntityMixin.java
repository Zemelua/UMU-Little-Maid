package io.github.zemelua.umu_little_maid.unused;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
@SuppressWarnings("UnusedMixin")
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	private static final String MAIDS_KEY = "Maids";
	private static final String ID_KEY = "Id";
	private static final String MAID_KEY = "Maid";

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo callback) {
		if (MaidContracts.MAIDS.containsKey(this)) {
			NbtList list = new NbtList();

			for (Map.Entry<UUID, NbtCompound> entry : MaidContracts.MAIDS.get(this).entrySet()) {
				NbtCompound maidNbt = new NbtCompound();
				maidNbt.putUuid(ServerPlayerEntityMixin.ID_KEY, entry.getKey());
				maidNbt.put(ServerPlayerEntityMixin.MAID_KEY, entry.getValue());

				list.add(maidNbt);
			}

			nbt.put(ServerPlayerEntityMixin.MAIDS_KEY, list);
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataNbt(NbtCompound nbt, CallbackInfo callback) {
		if (nbt.contains(ServerPlayerEntityMixin.MAIDS_KEY)) {
			NbtList list = nbt.getList(ServerPlayerEntityMixin.MAIDS_KEY, NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < list.size(); i++) {
				NbtCompound maidNbt = list.getCompound(i);
				MaidContracts.MAIDS.computeIfAbsent(this, player -> new HashMap<>())
						.put(maidNbt.getUuid(ServerPlayerEntityMixin.ID_KEY), maidNbt.getCompound(ServerPlayerEntityMixin.MAID_KEY));
			}
		}
	}

	@Deprecated
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) throws IllegalAccessException {
		super(world, pos, yaw, gameProfile, publicKey);

		throw new IllegalAccessException();
	}
}
