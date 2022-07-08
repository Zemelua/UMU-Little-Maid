package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.ai.brain.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Activity.class)
public interface ActivityAccessor {
	@Invoker("<init>")
	static Activity createActivity(String id) {
		throw new UnsupportedOperationException();
	}
}
