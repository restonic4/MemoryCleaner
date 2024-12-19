package com.restonic4.memory_cleaner;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

import static net.minecraft.commands.Commands.literal;

public class MemoryCleaner implements ModInitializer {
    private static final double MEMORY_PERCENTAGE_THRESHOLD = 90;
    private static long lastTimeAutomaticCleanUp;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("cleanup")
                .requires(source -> source.hasPermission(2) || !(source.getEntity() instanceof ServerPlayer))
                .executes(context -> {
                    String beforeMessage = getCurrentMemoryStatsMessage();

                    System.out.println(getUsedMemoryPercentage());

                    cleanup();

                    String afterMessage = getCurrentMemoryStatsMessage();

                    context.getSource().sendSuccess(() -> Component.literal("Before: " + beforeMessage + "\n" + "After: " + afterMessage), true);

                    return 1;
                })
        ));

        ServerTickEvents.END_SERVER_TICK.register((minecraftServer) -> {
            if (System.currentTimeMillis() >= lastTimeAutomaticCleanUp + 120000) {
                lastTimeAutomaticCleanUp = System.currentTimeMillis();

                if (!isMemorySafe()) {
                    String beforeMessage = getCurrentMemoryStatsMessage();

                    cleanup();

                    String afterMessage = getCurrentMemoryStatsMessage();

                    System.out.println("MEMORY CLEANUP TIME");
                    System.out.println("Before: " + beforeMessage);
                    System.out.println("Now: " + afterMessage);
                }
            }
        });
    }

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return bytesToMegabytes(usedMemory);
    }

    public static long getUsedMemoryPercentage() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory =runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return usedMemory * 100L / maxMemory;
    }

    public static long getMaxMemory() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory =runtime.maxMemory();

        return bytesToMegabytes(maxMemory);
    }

    public static String getCurrentMemoryStatsMessage() {
        return String.format(Locale.ROOT, "Mem: % 2d%% %03d/%03dMB", getUsedMemoryPercentage(), getUsedMemory(), getMaxMemory());
    }

    public static boolean isMemorySafe() {
        return getUsedMemoryPercentage() >= MEMORY_PERCENTAGE_THRESHOLD;
    }

    private static long bytesToMegabytes(long l) {
        return l / 1024L / 1024L;
    }

    public static void cleanup() {
        System.gc();
    }
}
