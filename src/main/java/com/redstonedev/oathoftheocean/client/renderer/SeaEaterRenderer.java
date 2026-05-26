package com.redstonedev.oathoftheocean.client.renderer;

import com.redstonedev.oathoftheocean.client.model.SeaEaterModel;
import com.redstonedev.oathoftheocean.entity.SeaEaterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class SeaEaterRenderer extends GeoEntityRenderer<SeaEaterEntity> {
    public SeaEaterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SeaEaterModel());
        this.shadowRadius = 1.8F;
        // Sea Eater is MASSIVE - a towering humanoid that walks on water. 2.5x both axes.
        this.widthScale  = 2.5F;
        this.heightScale = 2.5F;
    }
}
