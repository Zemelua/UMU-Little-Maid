package io.github.zemelua.umu_little_maid.util;

import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class ModUtils {
	public static boolean isMonster(LivingEntity living) {
		return living.getType().getSpawnGroup() == SpawnGroup.MONSTER;
	}

	public static boolean hasEnchantment(Enchantment enchantment, ItemStack itemStack) {
		return EnchantmentHelper.getLevel(enchantment, itemStack) > 0;
	}

	@Environment(EnvType.CLIENT)
	public static boolean isFirstPersonView() {
		return MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
	}

	@Environment(EnvType.CLIENT)
	public static boolean isThirdPersonView() {
		Perspective perspective = MinecraftClient.getInstance().options.getPerspective();
		return perspective == Perspective.THIRD_PERSON_BACK || perspective.isFrontView();
	}

	public static ItemStack searchInInventory(Inventory inventory, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (predicate.test(itemStack)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}

	public static Optional<BlockPos> getNearestPos(Collection<BlockPos> poses, Entity entity) {
		return poses.stream()
				.min(Comparator.comparingDouble(pos -> entity.squaredDistanceTo(Vec3d.of(pos))));
	}

	public static int getHeightFromGround(World world, Entity entity) {
		BlockPos.Mutable pos = entity.getBlockPos().down().mutableCopy();

		int i;
		for (i = 0; world.getBlockState(pos).isAir() && world.isInBuildLimit(pos); pos.move(Direction.DOWN, 1)) {
			i++;
		}

		return i;
	}

	public static boolean isSameObject(World world, BlockPos pos1, BlockPos pos2) {
		BlockState state1 = world.getBlockState(pos1);
		BlockState state2 = world.getBlockState(pos2);

		if (pos1.equals(pos2)) return true;
		if (!state1.isOf(state2.getBlock())) return false;

		if (state1.contains(Properties.BED_PART) && state2.contains(Properties.BED_PART)
				&& state1.contains(Properties.HORIZONTAL_FACING) && state2.contains(Properties.HORIZONTAL_FACING)) {
			Direction facing = state1.get(Properties.HORIZONTAL_FACING);
			Direction connectTo = state1.get(Properties.BED_PART) == BedPart.HEAD ? facing.getOpposite() : facing;
			BedPart connectWith = state1.get(Properties.BED_PART) == BedPart.HEAD ? BedPart.FOOT : BedPart.HEAD;

			return pos1.offset(connectTo).equals(pos2)
					&& state2.get(Properties.BED_PART) == connectWith
					&& state2.get(Properties.HORIZONTAL_FACING) == facing;
		} else if (state1.contains(Properties.DOUBLE_BLOCK_HALF) && state2.contains(Properties.DOUBLE_BLOCK_HALF)) {
			Direction connectTo = state1.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP;
			DoubleBlockHalf connectWith = state1.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;

			return pos1.offset(connectTo).equals(pos2)
					&& state2.get(Properties.DOUBLE_BLOCK_HALF) == connectWith;
		} else if (state1.contains(Properties.CHEST_TYPE) && state2.contains(Properties.CHEST_TYPE)
				&& state1.contains(Properties.HORIZONTAL_FACING) && state2.contains(Properties.HORIZONTAL_FACING)
		) {
			Direction facing1 = state1.get(Properties.HORIZONTAL_FACING);
			Direction facing2 = state2.get(Properties.HORIZONTAL_FACING);
			ChestType type1 = state1.get(Properties.CHEST_TYPE);
			ChestType type2 = state2.get(Properties.CHEST_TYPE);

			Direction connectTo = type1 == ChestType.LEFT ? facing1.rotateYClockwise() : facing1.rotateYCounterclockwise();
			ChestType connectWith = type1 == ChestType.LEFT ? ChestType.RIGHT : ChestType.LEFT;

			return pos1.offset(connectTo).equals(pos2)
					&& facing2 == facing1
					&& type2 == connectWith;
		}

		return false;
	}

	public static boolean isSameObject(World world, BlockPos pos, GlobalPos globalPos) {
		if (!world.getRegistryKey().equals(globalPos.getDimension())) return false;

		return isSameObject(world, pos, globalPos.getPos());
	}

	public static float lerpAngle(float angle0, float angle1, float magnitude) {
		float f = (magnitude - angle1) % ((float) Math.PI * 2);

		if (f < (float) -Math.PI) {
			f += (float) Math.PI * 2;
		}

		if (f >= (float) Math.PI) {
			f -= (float) Math.PI * 2;
		}

		return angle1 + angle0 * f;
	}

	public static boolean hasHarmfulEffect(LivingEntity living) {
		return living.getStatusEffects().stream()
				.anyMatch(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL);
	}

	public static Optional<Entity> crosshairEntity(PlayerEntity entity) {
		HitResult hitResult = entity.raycast(4.0D, 0.0F, false);
		if (hitResult instanceof EntityHitResult) {
			return Optional.ofNullable(((EntityHitResult) hitResult).getEntity());
		}

		return Optional.empty();
	}

	public static Optional<LittleMaidEntity> crosshairMaid(PlayerEntity entity) {
		return crosshairEntity(entity)
				.filter(e -> e instanceof LittleMaidEntity)
				.map(e -> (LittleMaidEntity) e);
	}

	public static final class Conversions {
		public static final Codec<List<GlobalPos>> GLOBAL_POS_COLLECTION_CODEC = GlobalPos.CODEC.listOf();

		public static NbtElement globalPosToNBT(GlobalPos pos) {
			return GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos)
					.getOrThrow(false, UMULittleMaid.LOGGER::error);
		}

		public static GlobalPos nbtToGlobalPos(NbtElement nbt) {
			return GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt)
					.getOrThrow(false, UMULittleMaid.LOGGER::error);
		}
	}

	public static final class GUIs {
		public static void drawTextWithBackground(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
			GameOptions options = MinecraftClient.getInstance().options;
			int textW = textRenderer.getWidth(text);
			int textH = textRenderer.fontHeight;
			int backgroundColor = (int) (options.getTextBackgroundOpacity().getValue() * 255.0D) << 24 & 0xFF000000;

			InGameHud.fill(matrices, x - 2, y - 2, x + textW + 2, y + textH + 2, backgroundColor);
			DrawableHelper.drawTextWithShadow(matrices, textRenderer, text, x, y, color);
		}
	}

	public static final class Texts {
		public static final ImmutableText DECIDE = new ImmutableText(Text.translatable("message.umu_little_maid.decide").styled(s -> s.withColor(Formatting.GREEN)));
		public static final ImmutableText REMOVE = new ImmutableText(Text.translatable("message.umu_little_maid.remove").styled(s -> s.withColor(Formatting.RED)));

		public static MutableText blockWithPos(BlockState state, BlockPos pos) {
			return Text.translatable(state.getBlock().getTranslationKey())
					.append(pos(pos))
					.styled(style -> style.withColor(Formatting.GREEN));
		}

		public static MutableText pos(BlockPos pos) {
			return net.minecraft.text.Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ()));
		}
	}

	public static final class KeyBinds {
		public static KeyBinding getAttackKey() {
			return MinecraftClient.getInstance().options.attackKey;
		}

		public static KeyBinding getUseKey() {
			return MinecraftClient.getInstance().options.useKey;
		}
	}

	private ModUtils() {}
}
