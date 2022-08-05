package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

import java.util.function.Predicate;

public class MaidPersonality {
	private final double maxHealth;
	private final double movementSpeed;
	private final double attackDamage;
	private final double attackKnockback;
	private final double armor;
	private final double armorToughness;
	private final double knockbackResistance;
	private final double luck;
	private final Predicate<LivingEntity> hostiles;
	private final SoundEvent ambientSound;
	private final SoundEvent fencerAttackSound;
	private final SoundEvent crackerAttackSound;
	private final SoundEvent archerAttackSound;
	private final SoundEvent killedSound;
	private final SoundEvent killedBarelySound;
	private final SoundEvent hurtSound;
	private final SoundEvent deathSound;
	private final SoundEvent eatSound;
	private final SoundEvent contractSound;
	private final SoundEvent sitSound;
	private final SoundEvent engageSound;

	public MaidPersonality(Builder builder,
	                       SoundEvent ambientSound,
	                       SoundEvent fencerAttackSound,
	                       SoundEvent crackerAttackSound,
	                       SoundEvent archerAttackSound,
	                       SoundEvent killedSound,
	                       SoundEvent killedBarelySound,
	                       SoundEvent hurtSound,
	                       SoundEvent deathSound,
	                       SoundEvent eatSound,
	                       SoundEvent contractSound,
	                       SoundEvent sitSound,
	                       SoundEvent engageSound) {
		this.maxHealth = builder.maxHealth;
		this.movementSpeed = builder.movementSpeed;
		this.attackDamage = builder.attackDamage;
		this.attackKnockback = builder.attackKnockback;
		this.armor = builder.armor;
		this.armorToughness = builder.armorToughness;
		this.knockbackResistance = builder.knockbackResistance;
		this.luck = builder.luck;
		this.hostiles = builder.hostiles;
		this.ambientSound = ambientSound;
		this.fencerAttackSound = fencerAttackSound;
		this.crackerAttackSound = crackerAttackSound;
		this.archerAttackSound = archerAttackSound;
		this.killedSound = killedSound;
		this.killedBarelySound = killedBarelySound;
		this.hurtSound = hurtSound;
		this.deathSound = deathSound;
		this.eatSound = eatSound;
		this.contractSound = contractSound;
		this.sitSound = sitSound;
		this.engageSound = engageSound;
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

	public boolean isHostile(LivingEntity living) {
		return this.hostiles.test(living);
	}

	public SoundEvent getAmbientSound() {
		return this.ambientSound;
	}

	public SoundEvent getFencerAttackSound() {
		return this.fencerAttackSound;
	}

	public SoundEvent getCrackerAttackSound() {
		return this.crackerAttackSound;
	}

	public SoundEvent getArcherAttackSound() {
		return this.archerAttackSound;
	}

	public SoundEvent getKilledSound() {
		return this.killedSound;
	}

	public SoundEvent getKilledBarelySound() {
		return this.killedBarelySound;
	}

	public SoundEvent getHurtSound() {
		return this.hurtSound;
	}

	public SoundEvent getDeathSound() {
		return this.deathSound;
	}

	public SoundEvent getEatSound() {
		return this.eatSound;
	}

	public SoundEvent getContractSound() {
		return this.contractSound;
	}

	public SoundEvent getSitSound() {
		return this.sitSound;
	}

	public SoundEvent getEngageSound() {
		return this.engageSound;
	}

	@SuppressWarnings("unused")
	public static class Builder {
		private double maxHealth = 20.0D;
		private double movementSpeed = 0.3D;
		private double attackDamage = 1.0D;
		private double attackKnockback = 0.0D;
		private double armor = 0.0D;
		private double armorToughness = 0.0D;
		private double knockbackResistance = 0.0D;
		private double luck = 0.0D;
		private Predicate<LivingEntity> hostiles = (living -> living.getType().isIn(ModTags.ENTITY_MAID_GENERAL_HOSTILES));

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

		public Builder setHostiles(Predicate<LivingEntity> value) {
			this.hostiles = value;

			return this;
		}

		public MaidPersonality build(SoundEvent ambientSound,
		                             SoundEvent fencerAttackSound,
		                             SoundEvent crackerAttackSound,
		                             SoundEvent archerAttackSound,
		                             SoundEvent killedSound,
		                             SoundEvent killedBarelySound,
		                             SoundEvent hurtSound,
		                             SoundEvent deathSound,
		                             SoundEvent eatSound,
		                             SoundEvent contractSound,
		                             SoundEvent sitSound,
		                             SoundEvent engageSound) {
			return new MaidPersonality(this,
					ambientSound,
					fencerAttackSound,
					crackerAttackSound,
					archerAttackSound,
					killedSound,
					killedBarelySound,
					hurtSound,
					deathSound,
					eatSound,
					contractSound,
					sitSound,
					engageSound
			);
		}
	}
}
