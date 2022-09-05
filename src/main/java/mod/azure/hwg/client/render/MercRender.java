package mod.azure.hwg.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.azure.hwg.client.models.MercModel;
import mod.azure.hwg.entity.MercEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class MercRender extends GeoEntityRenderer<MercEntity> {

	public MercRender(EntityRendererFactory.Context renderManagerIn) {
		super(renderManagerIn, new MercModel());
		this.shadowRadius = 0.7F;
	}

	@Override
	public RenderLayer getRenderType(MercEntity animatable, float partialTicks, MatrixStack stack,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("rArmRuff")) {
			stack.push();
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-75));
			stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0));
			stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0));
			stack.translate(0.4D, 0.3D, 0.7D);
			stack.scale(1.0f, 1.0f, 1.0f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(mainHand, Mode.THIRD_PERSON_RIGHT_HAND,
					packedLightIn, packedOverlayIn, stack, this.rtb, 0);
			stack.pop();
			bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(whTexture));
		}
		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

}