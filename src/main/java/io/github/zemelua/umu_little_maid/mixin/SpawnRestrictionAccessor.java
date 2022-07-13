package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnRestriction.class)
public interface SpawnRestrictionAccessor {
	@Invoker
	static <T extends MobEntity> void callRegister(EntityType<T> ignoredType, SpawnRestriction.Location ignoredLocation,
	                                               @SuppressWarnings("unused") Heightmap.Type ignoredMapType, SpawnRestriction.SpawnPredicate<T> ignoredPredicate) {
		throw new UnsupportedOperationException();
	}
}
