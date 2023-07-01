package io.github.zemelua.umu_little_maid.mixin;

import com.google.common.collect.ImmutableList;
import io.github.zemelua.umu_little_maid.entity.maid.MaidSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements CommandOutput, AutoCloseable {
	public MinecraftServerMixin(String name) {
		super(name);
	}

	@ModifyVariable(method = "createWorlds",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/registry/Registry;get(Lnet/minecraft/registry/RegistryKey;)Ljava/lang/Object;"))
	@SuppressWarnings("SpellCheckingInspection")
	private List<Spawner> createWorlds(List<Spawner> spawners) {
		return ImmutableList.<Spawner>builder().addAll(spawners).add(new MaidSpawner()).build();
	}
}
