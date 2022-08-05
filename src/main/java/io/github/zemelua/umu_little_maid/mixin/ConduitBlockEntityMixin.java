package io.github.zemelua.umu_little_maid.mixin;

import com.google.common.collect.ImmutableList;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin extends BlockEntity {
	@Redirect(method = "serverTick",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/block/entity/ConduitBlockEntity;givePlayersEffects(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;)V"))
	@SuppressWarnings("SpellCheckingInspection")
	private static void serverTick(World world, BlockPos pos, List<BlockPos> activatingPoses) {
		int range = activatingPoses.size() / 7 * 16;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Box box = new Box(x, y, z, x + 1, y, z).expand(range).stretch(0.0, world.getHeight(), 0.0);
		List<LivingEntity> targets = ImmutableList.<LivingEntity>builder()
				.addAll(world.getNonSpectatingEntities(PlayerEntity.class, box))
				.addAll(world.getNonSpectatingEntities(LittleMaidEntity.class, box)).build();

		for (LivingEntity target : targets) {
			if (pos.isWithinDistance(target.getBlockPos(), range) && target.isTouchingWaterOrRain()) {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
			}
		}
	}

	@Deprecated
	public ConduitBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		throw new UnsupportedOperationException();
	}
}
