package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.text.Text;

import java.util.Arrays;

public enum MaidMode {
	FOLLOW(0, Text.translatable("message.umu_little_maid.maid_follow")),
	WAIT(1, Text.translatable("message.umu_little_maid.maid_wait")),
	FREE(2, Text.translatable("message.umu_little_maid.maid_free"));

	private final int id;
	private final Text message;

	MaidMode(int id, Text message) {
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return this.id;
	}

	public Text getMessage() {
		return this.message;
	}

	public MaidMode getNext() {
		MaidMode[] values = values();
		return values[(this.ordinal() + 1) % values.length];
	}

	public static MaidMode fromId(int id) {
		return Arrays.stream(values())
				.filter(value -> value.getId() == id)
				.findFirst()
				.orElse(FOLLOW);
	}
}
