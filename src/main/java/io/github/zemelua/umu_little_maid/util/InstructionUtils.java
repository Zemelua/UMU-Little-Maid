package io.github.zemelua.umu_little_maid.util;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public final class InstructionUtils {
	public static final ImmutableText PASS_ON_BLOCK_MESSAGE = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_pass_on_block"));
	public static final ImmutableText PASS_ON_ENTITY_MESSAGE = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_pass_on_entity"));
	public static final ImmutableText CANCEL = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_cancel"));

	public static IInstructionComponent getComponent(PlayerEntity player) {
		return player.getComponent(Components.INSTRUCTION);
	}

	@Environment(EnvType.CLIENT)
	public static Optional<IInstructionComponent> getComponent(MinecraftClient client) {
		return Optional.ofNullable(client.player).map(InstructionUtils::getComponent);
	}

	@Environment(EnvType.CLIENT)
	public static boolean isInstructing(MinecraftClient client) {
		return getComponent(client).map(IInstructionComponent::isInstructing).orElse(false);
	}

	@Environment(EnvType.CLIENT)
	public static Optional<LittleMaidEntity> getMaid(MinecraftClient client) {
		return getComponent(client).flatMap(IInstructionComponent::getTarget);
	}

	@Environment(EnvType.CLIENT)
	public static void cancelOnClient() {
		ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_INSTRUCTION_CANCEL, PacketByteBufs.create());
	}

	public static <I extends Entity & IInstructable> Text homeMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_home", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> Text anchorMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_anchor", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> Text deliveryBoxMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_delivery_box", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> Text setHomeMessage(BlockState state, BlockPos pos, I owner) {
		return setSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> Text renewHomeMessage(BlockState state, BlockPos pos, I owner) {
		return renewSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> Text removeHomeMessage(BlockState state, BlockPos pos, I owner) {
		return removeSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> Text addDeliveryBoxMessage(BlockState state, BlockPos pos, I owner) {
		return addSiteMessage(state, pos, deliveryBoxMessage(owner));
	}

	public static <I extends Entity & IInstructable> Text removeDeliveryBoxMessage(BlockState state, BlockPos pos, I owner) {
		return removeSiteMessage(state, pos, deliveryBoxMessage(owner));
	}

	public static Text setSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_set_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static Text renewSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_renew_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static Text addSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_add_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static Text removeSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_remove_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static Text guideCancelMessage() {
		KeyBinding attackKey = ModUtils.KeyBinds.getAttackKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_cancel", attackKey.getBoundKeyLocalizedText(), ScreenTexts.CANCEL);
	}

	public static Text guideDecideMessage() {
		KeyBinding useKey = ModUtils.KeyBinds.getUseKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_decide", useKey.getBoundKeyLocalizedText(), ModUtils.Texts.DECIDE);
	}

	public static Text guideRemoveMessage() {
		KeyBinding useKey = ModUtils.KeyBinds.getUseKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_remove", useKey.getBoundKeyLocalizedText(), ModUtils.Texts.REMOVE);
	}

	private InstructionUtils() {}
}
