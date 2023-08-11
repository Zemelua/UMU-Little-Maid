package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.client.render.Frustum;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Frustum.class)
public interface AccessorFrustum {
	@Accessor Vector4f getRecession();
}
