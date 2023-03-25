package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawableHelper.class)
public interface DrawableHelperAccessor {
	@Invoker
	static void callFillGradient(
			@SuppressWarnings("unused") MatrixStack matrices,
			@SuppressWarnings("unused") int startX,
			@SuppressWarnings("unused") int startY,
			@SuppressWarnings("unused") int endX,
			@SuppressWarnings("unused") int endY,
			@SuppressWarnings("unused") int colorStart,
			@SuppressWarnings("unused") int colorEnd,
			@SuppressWarnings("unused") int z
	) {
		throw new UnsupportedOperationException();
	}
}
