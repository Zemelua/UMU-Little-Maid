package io.github.zemelua.umu_little_maid.util;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.List;

public class ImmutableText implements Text {
	private final Text content;

	public ImmutableText(Text content) {
		this.content = content;
	}

	@Override
	public Style getStyle() {
		return this.content.getStyle();
	}

	@Override
	public TextContent getContent() {
		return this.content.getContent();
	}

	@Override
	public List<Text> getSiblings() {
		return this.content.getSiblings();
	}

	@Override
	public OrderedText asOrderedText() {
		return this.content.asOrderedText();
	}
}
