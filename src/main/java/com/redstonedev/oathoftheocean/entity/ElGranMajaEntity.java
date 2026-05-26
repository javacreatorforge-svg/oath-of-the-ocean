package com.redstonedev.oathoftheocean.entity;

import com.redstonedev.oathoftheocean.init.ModSounds;
import com.redstonedev.oathoftheocean.util.DeepWaterCheck;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ElGranMajaEntity extends Monster implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int soundCooldown;
    private int blockBreakCooldown = 60;

    public ElGranMajaEntity(EntityType<? extends ElGranMajaEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;
        this.soundCooldown = 200 + this.random.nextInt(400);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100000.0D)
                .add(Attributes.ATTACK_DAMAGE, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        // No attack goal - El Gran Maja doesn't attack the player directly per spec.
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;

        if (soundCooldown > 0) soundCooldown--;
        if (blockBreakCooldown > 0) blockBreakCooldown--;

        // Gated idle sounds: only audible if a player nearby has deep ocean within range.
        if (soundCooldown <= 0) {
            Player nearest = this.level.getNearestPlayer(this, 64.0D);
            if (nearest != null && DeepWaterCheck.isPlayerNearDeepOcean(nearest)) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.EL_GRAN_MAJA_IDLE.get(), SoundSource.HOSTILE, 1.4F, 1.0F);
            }
            soundCooldown = 400 + this.random.nextInt(800);
        }

        // Annoys the player by destroying nearby blocks rather than fighting.
        if (blockBreakCooldown <= 0) {
            tryBreakNearbyBlock();
            blockBreakCooldown = 100 + this.random.nextInt(140);
        }
    }

    private void tryBreakNearbyBlock() {
        BlockPos center = this.blockPosition();
        BlockPos target = null;
        scan:
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -1; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    BlockState bs = this.level.getBlockState(p);
                    if (canBreak(bs, p)) {
                        target = p;
                        break scan;
                    }
                }
            }
        }
        if (target != null) {
            this.level.destroyBlock(target, true, this);
        }
    }

    private boolean canBreak(BlockState bs, BlockPos p) {
        if (bs.isAir()) return false;
        if (bs.getDestroySpeed(this.level, p) < 0) return false;
        Block b = bs.getBlock();
        return b != Blocks.BEDROCK && b != Blocks.BARRIER && b != Blocks.COMMAND_BLOCK
                && b != Blocks.STRUCTURE_BLOCK && b != Blocks.JIGSAW && b != Blocks.LIGHT
                && b != Blocks.END_PORTAL_FRAME && b != Blocks.END_PORTAL
                && b != Blocks.NETHER_PORTAL && b != Blocks.VOID_AIR;
    }

    // === Animations ===========================================================

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(
                new AnimationBuilder().loop("animation.sm_elgranmaja.swim_1"));
        return PlayState.CONTINUE;
    }

    @Override public AnimationFactory getFactory() { return factory; }

    @Override protected SoundEvent getHurtSound(DamageSource s) { return ModSounds.EL_GRAN_MAJA_IDLE.get(); }
    @Override protected SoundEvent getDeathSound()              { return ModSounds.EL_GRAN_MAJA_IDLE.get(); }

    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public boolean isPushedByFluid()     { return false; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }
}
