package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.util.ItemParticleScaleManager;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrackParticle.class)
public abstract class CrackParticleMixin extends SpriteBillboardParticle {
	protected CrackParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Inject(method = "<init>*", at = @At("RETURN"))
	private void constructor(ClientWorld world, double x, double y, double z, ItemStack item, CallbackInfo callback) {
		this.scale(ItemParticleScaleManager.getSize());
	}
}
