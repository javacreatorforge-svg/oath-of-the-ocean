package com.redstonedev.oathoftheocean.init;

import com.redstonedev.oathoftheocean.OathOfTheOcean;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OathOfTheOcean.MODID);

    public static final RegistryObject<SoundEvent> EL_GRAN_MAJA_IDLE = register("el_gran_maja_idle");
    public static final RegistryObject<SoundEvent> SEA_EATER_IDLE    = register("sea_eater_idle");
    public static final RegistryObject<SoundEvent> THE_BLOOP_IDLE    = register("the_bloop_idle");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> new SoundEvent(new ResourceLocation(OathOfTheOcean.MODID, name)));
    }
}
