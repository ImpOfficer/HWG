package mod.azure.hwg.client.render.projectiles;

import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.azure.hwg.client.models.projectiles.FlameFiringModel;
import mod.azure.hwg.entity.projectiles.FlameFiring;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class FlameFiringRender extends GeoProjectilesRenderer<FlameFiring> {

	public FlameFiringRender(EntityRendererFactory.Context renderManagerIn) {
		super(renderManagerIn, new FlameFiringModel());
	}

	protected int getBlockLight(FlameFiring entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public RenderLayer getRenderType(FlameFiring animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}

}