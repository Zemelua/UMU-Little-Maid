package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(SensorType.class)
public interface SensorTypeAccessor {
	@Invoker("<init>")
	static <U extends Sensor<?>> SensorType<U> constructor(Supplier<U> ignoredFactory) {
		throw new UnsupportedOperationException();
	}
}
