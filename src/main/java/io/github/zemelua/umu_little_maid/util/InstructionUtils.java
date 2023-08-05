package io.github.zemelua.umu_little_maid.util;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public final class InstructionUtils {
	public static final ImmutableText PASS_ON_BLOCK_MESSAGE = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_pass_on_block"));
	public static final ImmutableText PASS_ON_ENTITY_MESSAGE = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_pass_on_entity"));
	public static final ImmutableText PASS_ON_ANCHOR_MESSAGE = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_pass_on_anchor"));
	public static final ImmutableText FINISH = new ImmutableText(Text.translatable("message.umu_little_maid.instruction_finish"));
	public static final ImmutableText HOME_TOOLTIP = new ImmutableText(Text.translatable("tooltip.umu_little_maid.home"));
	public static final ImmutableText ANCHOR_TOOLTIP = new ImmutableText(Text.translatable("tooltip.umu_little_maid.anchor"));
	public static final ImmutableText DELIVERY_BOX_TOOLTIP = new ImmutableText(Text.translatable("tooltip.umu_little_maid.delivery_box"));

	public static IInstructionComponent getComponent(PlayerEntity player) {
		return player.getComponent(Components.INSTRUCTION);
	}

	@Environment(EnvType.CLIENT)
	public static Optional<IInstructionComponent> getComponent(MinecraftClient client) {
		return Optional.ofNullable(client.player).map(InstructionUtils::getComponent);
	}

	@Environment(EnvType.CLIENT)
	public static Optional<LittleMaidEntity> getMaid(MinecraftClient client) {
		return getComponent(client).flatMap(IInstructionComponent::getTarget);
	}

	@Environment(EnvType.CLIENT)
	public static void finishOnClient() {
		ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_INSTRUCTION_FINISH, PacketByteBufs.create());
	}

	public static <I extends Entity & IInstructable> MutableText homeMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_home", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> MutableText anchorMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_anchor", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> MutableText deliveryBoxMessage(I owner) {
		return Text.translatable("message.umu_little_maid.instruction_delivery_box", owner.getDisplayName());
	}

	public static <I extends Entity & IInstructable> MutableText setHomeMessage(BlockState state, BlockPos pos, I owner) {
		return setSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> MutableText renewHomeMessage(BlockState state, BlockPos pos, I owner) {
		return renewSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> MutableText removeHomeMessage(BlockState state, BlockPos pos, I owner) {
		return removeSiteMessage(state, pos, homeMessage(owner));
	}

	public static <I extends Entity & IInstructable> MutableText addDeliveryBoxMessage(BlockState state, BlockPos pos, I owner) {
		return addSiteMessage(state, pos, deliveryBoxMessage(owner));
	}

	public static <I extends Entity & IInstructable> MutableText removeDeliveryBoxMessage(BlockState state, BlockPos pos, I owner) {
		return removeSiteMessage(state, pos, deliveryBoxMessage(owner));
	}

	public static MutableText setSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_set_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static MutableText renewSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_renew_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static MutableText addSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_add_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static MutableText removeSiteMessage(BlockState state, BlockPos pos, Text site) {
		return Text.translatable("message.umu_little_maid.instruction_remove_site", ModUtils.Texts.blockWithPos(state, pos), site);
	}

	public static MutableText guideFinishMessage() {
		KeyBinding attackKey = ModUtils.KeyBinds.getAttackKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_finish", attackKey.getBoundKeyLocalizedText(), ScreenTexts.DONE);
	}

	public static MutableText guideDecideMessage() {
		KeyBinding useKey = ModUtils.KeyBinds.getUseKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_decide", useKey.getBoundKeyLocalizedText(), ModUtils.Texts.DECIDE);
	}

	public static MutableText guideRemoveMessage() {
		KeyBinding useKey = ModUtils.KeyBinds.getUseKey();

		return Text.translatable("message.umu_little_maid.instruction_guide_remove", useKey.getBoundKeyLocalizedText(), ModUtils.Texts.REMOVE);
	}

	private InstructionUtils() {}
}
