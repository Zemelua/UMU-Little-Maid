package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.function.Function;

public enum MaidMode {
	FOLLOW(0, m -> Text.translatable("message.umu_little_maid.maid_follow", m.getDisplayName())),
	WAIT(1, m -> Text.translatable("message.umu_little_maid.maid_wait", m.getDisplayName())),
	FREE(2, m -> Text.translatable("message.umu_little_maid.maid_free", m.getDisplayName()));

	private final int id;
	private final Function<LittleMaidEntity, Text> messageFactory;

	MaidMode(int id, Function<LittleMaidEntity, Text> messageFactory) {
		this.id = id;
		this.messageFactory = messageFactory;
	}

	public int getId() {
		return this.id;
	}

	public Text getMessage(LittleMaidEntity maid) {
		return this.messageFactory.apply(maid);
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
