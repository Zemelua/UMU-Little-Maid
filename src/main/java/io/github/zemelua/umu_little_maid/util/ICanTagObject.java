package io.github.zemelua.umu_little_maid.util;

import net.minecraft.registry.tag.TagKey;

public interface ICanTagObject<T> {
	boolean isIn(TagKey<T> tag);
}
