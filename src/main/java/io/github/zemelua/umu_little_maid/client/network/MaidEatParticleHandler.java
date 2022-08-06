package io.github.zemelua.umu_little_maid.client.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.util.ItemParticleScaleManager;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class MaidEatParticleHandler {
	public static void handle(World world, ItemStack item, double xPos, double yPos, double zPos, double xDelta, double yDelta, double zDelta, float scale) {
		ItemParticleScaleManager.setSize(scale);

		final ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);
		world.addParticle(particle, xPos, yPos, zPos, xDelta, yDelta, zDelta);

		ItemParticleScaleManager.setSize(1.0F);
	}

	@Deprecated
	private MaidEatParticleHandler() {
		UMULittleMaid.LOGGER.warn(ClientNetworkHandler.MARKER, "MaidEatParticleHandler をインスタンス化しないで！");
	}
}
