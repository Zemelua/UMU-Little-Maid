package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.text.Text;

public enum MaidMode {
	FOLLOW(Text.translatable("message.actionbar.umu_little_maid.maid_follow")),
	WAIT(Text.translatable("message.actionbar.umu_little_maid.maid_wait")),
	FREE(Text.translatable("message.actionbar.umu_little_maid.maid_free"));

	private final Text message;

	MaidMode(Text message) {
		this.message = message;
	}

	public MaidMode getNext() {
		MaidMode[] values = values();
		return values[(this.ordinal() + 1) % values.length];
	}

	public Text getMessage() {
		return this.message;
	}
}
