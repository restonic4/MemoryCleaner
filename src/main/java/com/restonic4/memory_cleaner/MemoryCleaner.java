package com.restonic4.memory_cleaner;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class MemoryCleaner implements ModInitializer {
    private static final double MEMORY_THRESHOLD = 0.85;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("cleanup")
                .requires(source -> source.hasPermission(2) || !(source.getEntity() instanceof ServerPlayer))
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal("RAM cleaned"), true);

                    isMemorySafe();
                    cleanup();

                    return 1;
                })
        ));
    }

    public static boolean isMemorySafe() {
        Runtime runtime = Runtime.getRuntime();



        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double usage = (double) usedMemory / maxMemory;

        double maxMemoryGB = maxMemory / (1024.0 * 1024.0 * 1024.0);
        double totalMemoryGB = totalMemory / (1024.0 * 1024.0 * 1024.0);
        double freeMemoryGB = freeMemory / (1024.0 * 1024.0 * 1024.0);
        double usedMemoryGB = usedMemory / (1024.0 * 1024.0 * 1024.0);

        System.out.println(maxMemoryGB + ", " + totalMemoryGB + ", " + freeMemoryGB + ", " + usedMemoryGB + ", " + usage);

        return usage >= MEMORY_THRESHOLD;
    }

    public static void cleanup() {
        System.gc();
    }
}
