package com.restonic4.memory_cleaner;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class MemoryCleaner implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("cleanup")
                .requires(source -> source.hasPermission(2) || !(source.getEntity() instanceof ServerPlayer))
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal("RAM cleaned"), true);

                    cleanup();

                    return 1;
                })
        ));
    }

    public static void cleanup() {
        System.gc();
    }
}
