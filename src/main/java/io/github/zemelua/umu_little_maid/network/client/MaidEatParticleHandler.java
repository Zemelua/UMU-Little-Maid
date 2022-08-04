package io.github.zemelua.umu_little_maid.network.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import io.github.zemelua.umu_little_maid.util.ItemParticleScaleManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class MaidEatParticleHandler implements ClientPlayNetworking.PlayChannelHandler {
	public static ClientPlayNetworking.PlayChannelHandler INSTANCE = new MaidEatParticleHandler();

	private static boolean instantiated = false;
	private MaidEatParticleHandler() {
		if (MaidEatParticleHandler.instantiated) {
			UMULittleMaid.LOGGER.warn(NetworkHandler.MARKER, "MaidEatParticleHandler#INSTANCE を使ってね！");
		}

		MaidEatParticleHandler.instantiated = true;
	}

	@Override
	public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf packet, PacketSender sender) {
		final ItemStack item = packet.readItemStack();
		final double x = packet.readDouble();
		final double y = packet.readDouble();
		final double z = packet.readDouble();
		final double xDelta = packet.readDouble();
		final double yDelta = packet.readDouble();
		final double zDelta = packet.readDouble();

		if (client.world != null) {
			ItemParticleScaleManager.setSize(0.73F);

			final ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);
			client.world.addParticle(particle, x, y, z, xDelta, yDelta, zDelta);

			ItemParticleScaleManager.setSize(1.0F);
		}
	}
}
