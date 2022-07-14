package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.sound.ModSounds;
import net.minecraft.sound.SoundEvent;

public class MaidPersonality {
	private final double maxHealth;
	private final double movementSpeed;
	private final double attackDamage;
	private final double attackKnockback;
	private final double armor;
	private final double armorToughness;
	private final double knockbackResistance;
	private final double luck;
	private final SoundEvent contractSound;

	public MaidPersonality(Builder builder) {
		this.maxHealth = builder.maxHealth;
		this.movementSpeed = builder.movementSpeed;
		this.attackDamage = builder.attackDamage;
		this.attackKnockback = builder.attackKnockback;
		this.armor = builder.armor;
		this.armorToughness = builder.armorToughness;
		this.knockbackResistance = builder.knockbackResistance;
		this.luck = builder.luck;
		this.contractSound = builder.contractSound;
	}

	public double getMaxHealth() {
		return this.maxHealth;
	}

	public double getMovementSpeed() {
		return this.movementSpeed;
	}

	public double getAttackDamage() {
		return this.attackDamage;
	}

	public double getAttackKnockback() {
		return this.attackKnockback;
	}

	public double getArmor() {
		return this.armor;
	}

	public double getArmorToughness() {
		return this.armorToughness;
	}

	public double getKnockbackResistance() {
		return this.knockbackResistance;
	}

	public double getLuck() {
		return this.luck;
	}

	public SoundEvent getContractSound() {
		return this.contractSound;
	}

	public static class Builder {
		private double maxHealth = 20.0D;
		private double movementSpeed = 0.3D;
		private double attackDamage = 1.0D;
		private double attackKnockback = 0.0D;
		private double armor = 0.0D;
		private double armorToughness = 0.0D;
		private double knockbackResistance = 0.0D;
		private double luck = 0.0D;
		private SoundEvent contractSound = ModSounds.ENTITY_MAID_GENERAL_CONTRACT;

		public Builder setMaxHealth(double maxHealth) {
			this.maxHealth = maxHealth;

			return this;
		}

		public Builder setMovementSpeed(double movementSpeed) {
			this.movementSpeed = movementSpeed;

			return this;
		}

		public Builder setAttackDamage(double attackDamage) {
			this.attackDamage = attackDamage;

			return this;
		}

		public Builder setAttackKnockback(double attackKnockback) {
			this.attackKnockback = attackKnockback;

			return this;
		}

		public Builder setArmor(double armor) {
			this.armor = armor;

			return this;
		}

		public Builder setArmorToughness(double armorToughness) {
			this.armorToughness = armorToughness;

			return this;
		}

		public Builder setKnockbackResistance(double knockbackResistance) {
			this.knockbackResistance = knockbackResistance;

			return this;
		}

		public Builder setLuck(double luck) {
			this.luck = luck;

			return this;
		}

		public Builder setContractSound(SoundEvent contractSound) {
			this.contractSound = contractSound;

			return this;
		}

		public MaidPersonality build() {
			return new MaidPersonality(this);
		}
	}
}
