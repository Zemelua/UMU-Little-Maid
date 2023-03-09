package io.github.zemelua.umu_little_maid.util;

import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public final class InstructionUtils {
	public static IInstructionComponent getComponent(PlayerEntity player) {
		return player.getComponent(Components.INSTRUCTION);
	}

	@Environment(EnvType.CLIENT)
	public static void cancelOnClient() {
		ClientPlayNetworking.send(NetworkHandler.CHANNEL_CLIENT_INSTRUCTION_CANCEL, PacketByteBufs.create());
	}

	public static Text setHomeMessage(Block block, BlockPos pos) {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_set_home",
				Text.translatable(block.getTranslationKey()).styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
				Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).styled(style -> style.withColor(Formatting.GREEN))
		);
	}

	public static Text replaceHomeMessage(Block block, BlockPos pos) {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_replace_home",
				Text.translatable(block.getTranslationKey()).styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
				Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).styled(style -> style.withColor(Formatting.GREEN))
		);
	}

	public static Text removeHomeMessage() {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_remove_home");
	}

	public static Text addDeliveryBoxMessage(Block block, BlockPos pos) {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_add_delivery_box",
				Text.translatable(block.getTranslationKey()).styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
				Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).styled(style -> style.withColor(Formatting.GREEN))
		);
	}

	public static Text removeDeliveryBoxMessage(Block block, BlockPos pos) {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_remove_delivery_box",
				Text.translatable(block.getTranslationKey()).styled(style -> style.withBold(true).withColor(Formatting.GREEN)),
				Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).styled(style -> style.withColor(Formatting.GREEN))
		);
	}

	public static Text passOnBlockMessage() {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_pass_on_block");
	}

	public static Text passOnEntityMessage() {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_pass_on_entity");
	}

	public static Text cancelMessage() {
		return Text.translatable("message.actionbar.umu_little_maid.instruction_cancel");
	}

	private InstructionUtils() {}
}
