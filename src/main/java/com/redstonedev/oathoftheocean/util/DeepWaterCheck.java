package com.redstonedev.oathoftheocean.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Helper to determine whether a player is "near deep ocean" - used by all three entities
 * to gate their idle sounds. Per spec: sounds only play if the player is in or near a deep
 * ocean (including being on an island in deep ocean). They do NOT play if the player is
 * near shallow water, on a land biome, or generally not near deep water.
 */
public final class DeepWaterCheck {
    private DeepWaterCheck() {}

    private static final int SCAN_RADIUS = 32;
    private static final int SCAN_STEP   = 8;

    /** Samples a 65x65 grid (9 steps each axis = 81 samples) around the player. True if any
     *  sample lies in a deep-ocean biome. */
    public static boolean isPlayerNearDeepOcean(Player player) {
        Level level = player.level;
        BlockPos origin = player.blockPosition();
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx += SCAN_STEP) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz += SCAN_STEP) {
                BlockPos check = origin.offset(dx, 0, dz);
                Holder<Biome> biome = level.getBiome(check);
                if (biome.is(BiomeTags.IS_DEEP_OCEAN)) return true;
            }
        }
        return false;
    }

    /** Convenience overload for when only the entity's own position is relevant. */
    public static boolean isEntityInDeepOcean(Entity e) {
        Holder<Biome> biome = e.level.getBiome(e.blockPosition());
        return biome.is(BiomeTags.IS_DEEP_OCEAN);
    }
}
