package com.redstonedev.oathoftheocean.entity;

import com.redstonedev.oathoftheocean.init.ModSounds;
import com.redstonedev.oathoftheocean.util.DeepWaterCheck;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class TheBloopEntity extends Monster implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int soundCooldown;

    public TheBloopEntity(EntityType<? extends TheBloopEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 0;
        this.soundCooldown = 200 + this.random.nextInt(400);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100000.0D)
                .add(Attributes.ATTACK_DAMAGE, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 96.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;

        if (soundCooldown > 0) soundCooldown--;
        if (soundCooldown <= 0) {
            Player nearest = this.level.getNearestPlayer(this, 64.0D);
            if (nearest != null && DeepWaterCheck.isPlayerNearDeepOcean(nearest)) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.THE_BLOOP_IDLE.get(), SoundSource.HOSTILE, 1.5F, 1.0F);
            }
            soundCooldown = 400 + this.random.nextInt(800);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(
                new AnimationBuilder().loop("animation.sm_bloop.swim_1"));
        return PlayState.CONTINUE;
    }

    @Override public AnimationFactory getFactory() { return factory; }

    @Override protected SoundEvent getHurtSound(DamageSource s) { return ModSounds.THE_BLOOP_IDLE.get(); }
    @Override protected SoundEvent getDeathSound()              { return ModSounds.THE_BLOOP_IDLE.get(); }

    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public boolean isPushedByFluid()     { return false; }
}
