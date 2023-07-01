package io.github.zemelua.umu_little_maid.entity.maid.feeling;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public enum MaidFeeling implements IMaidFeeling {
	NORMAL(),
	HAPPY("happy");

	private final String textureLiteral;

	MaidFeeling() {
		this.textureLiteral = StringUtils.EMPTY;
	}

	MaidFeeling(String textureLiteral) {
		this.textureLiteral = textureLiteral;
	}

	@Override
	public Optional<String> getTextureLiteral() {
		return this.textureLiteral.isEmpty()
				? Optional.empty()
				: Optional.of(this.textureLiteral);
	}

	public static void init() {
		Registry.register(ModRegistries.MAID_FEELING, UMULittleMaid.identifier("normal"), NORMAL);
		Registry.register(ModRegistries.MAID_FEELING, UMULittleMaid.identifier("happy"), HAPPY);
	}
}
