package io.github.zemelua.umu_little_maid.util;

import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.headpatting.IHeadpattingComponent;
import io.github.zemelua.umu_little_maid.client.UMULittleMaidClient;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Optional;

public final class HeadpatManager {
	@Environment(EnvType.CLIENT)
	public static void clientTick(MinecraftClient client, IHeadpattingComponent component) {
		Optional<LittleMaidEntity> crosshairMaid = Optional.ofNullable(client.crosshairTarget)
				.filter(h -> h instanceof EntityHitResult)
				.map(h -> ((EntityHitResult) h).getEntity())
				.filter(e -> e instanceof LittleMaidEntity)
				.map(e -> (LittleMaidEntity) e);

		if (UMULittleMaidClient.KEY_HEADPAT.isPressed()) {
			if (crosshairMaid.isPresent()) {
				if (component.getTarget().isPresent()) {
					if (!component.getTarget().get().equals(crosshairMaid.get())) {
						sendFinishHeadpatting();
					}
				} else {
					sendStartHeadpatting(crosshairMaid.get());
				}
			} else {
				if (component.getTarget().isPresent()) {
					sendFinishHeadpatting();
				}
			}
		} else {
			if (component.getTarget().isPresent()) {
				sendFinishHeadpatting();
			}
		}
	}

	public static IHeadpattingComponent getHeadpattingComponent(PlayerEntity player) {
		return player.getComponent(Components.HEADPATTING);
	}

	@Environment(EnvType.CLIENT)
	public static Optional<IHeadpattingComponent> getHeadpattingComponent(MinecraftClient client) {
		return Optional.ofNullable(client.player).map(HeadpatManager::getHeadpattingComponent);
	}

	public static boolean isHeadpatting(PlayerEntity player) {
		return getHeadpattingComponent(player).isHeadpatting();
	}

	public static boolean isHeadpattingWith(PlayerEntity player, Entity entity) {
		return getHeadpattingComponent(player).isHeadpattingWith(entity);
	}

	public static <T extends Entity & IHeadpattable> boolean isHeadpatted(T pet) {
		return pet.isHeadpatted();
	}

	@Environment(EnvType.CLIENT)
	public static void sendStartHeadpatting(LittleMaidEntity maid) {
		PacketByteBuf packet = PacketByteBufs.create();
		packet.writeInt(maid.getId());

		ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_HEADPATTING_START, packet);
	}

	@Environment(EnvType.CLIENT)
	public static void sendFinishHeadpatting() {
		ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_HEADPATTING_FINISH, PacketByteBufs.create());
	}

	private HeadpatManager() {}
}
