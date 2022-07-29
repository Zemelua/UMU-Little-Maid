package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.sound.ModSounds;
import io.github.zemelua.umu_little_maid.tag.ModTags;
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
	private final Predicate<LivingEntity> hostile;
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

	public MaidPersonality(Builder builder) {
		this.maxHealth = builder.maxHealth;
		this.movementSpeed = builder.movementSpeed;
		this.attackDamage = builder.attackDamage;
		this.attackKnockback = builder.attackKnockback;
		this.armor = builder.armor;
		this.armorToughness = builder.armorToughness;
		this.knockbackResistance = builder.knockbackResistance;
		this.luck = builder.luck;
		this.hostile = builder.hostile;
		this.ambientSound = builder.ambientSound;
		this.fencerAttackSound = builder.fencerAttackSound;
		this.crackerAttackSound = builder.crackerAttackSound;
		this.archerAttackSound = builder.archerAttackSound;
		this.killedSound = builder.killedSound;
		this.killedBarelySound = builder.killedBarelySound;
		this.hurtSound = builder.hurtSound;
		this.deathSound = builder.deathSound;
		this.eatSound = builder.eatSound;
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

	public boolean isHostile(LivingEntity living) {
		return this.hostile.test(living);
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
		private Predicate<LivingEntity> hostile = (living -> living.getType().isIn(ModTags.MAID_GENERAL_HOSTILES));
		private SoundEvent ambientSound = ModSounds.ENTITY_MAID_GENERAL_AMBIENT;
		private SoundEvent fencerAttackSound = ModSounds.ENTITY_MAID_GENERAL_FENCER_ATTACK;
		private SoundEvent crackerAttackSound = ModSounds.ENTITY_MAID_GENERAL_CRACKER_ATTACK;
		private SoundEvent archerAttackSound = ModSounds.ENTITY_MAID_GENERAL_ARCHER_ATTACK;
		private SoundEvent killedSound = ModSounds.ENTITY_MAID_GENERAL_KILLED;
		private SoundEvent killedBarelySound = ModSounds.ENTITY_MAID_GENERAL_KILLED_BARELY;
		private SoundEvent hurtSound = ModSounds.ENTITY_MAID_GENERAL_HURT;
		private SoundEvent deathSound = ModSounds.ENTITY_MAID_GENERAL_DEATH;
		private SoundEvent eatSound = ModSounds.ENTITY_MAID_GENERAL_EAT;
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

		public Builder setHostile(Predicate<LivingEntity> value) {
			this.hostile = value;

			return this;
		}

		public Builder setAmbientSound(SoundEvent ambientSound) {
			this.ambientSound = ambientSound;

			return this;
		}

		public Builder setFencerAttackSound(SoundEvent fencerAttackSound) {
			this.fencerAttackSound = fencerAttackSound;

			return this;
		}

		public Builder setCrackerAttackSound(SoundEvent crackerAttackSound) {
			this.crackerAttackSound = crackerAttackSound;

			return this;
		}

		public Builder setArcherAttackSound(SoundEvent archerAttackSound) {
			this.archerAttackSound = archerAttackSound;

			return this;
		}

		public Builder setKilledSound(SoundEvent killedSound) {
			this.killedSound = killedSound;

			return this;
		}

		public Builder setKilledBarelySound(SoundEvent killedBarelySound) {
			this.killedBarelySound = killedBarelySound;

			return this;
		}

		public Builder setHurtSound(SoundEvent hurtSound) {
			this.hurtSound = hurtSound;

			return this;
		}

		public Builder setDeathSound(SoundEvent deathSound) {
			this.deathSound = deathSound;

			return this;
		}

		public Builder setEatSound(SoundEvent eatSound) {
			this.eatSound = eatSound;

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
