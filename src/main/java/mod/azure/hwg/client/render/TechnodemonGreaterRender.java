package mod.azure.hwg.client.render;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import mod.azure.hwg.client.models.TechnodemonGreaterModel;
import mod.azure.hwg.entity.TechnodemonGreaterEntity;
import mod.azure.hwg.item.weapons.BrimstoneItem;
import mod.azure.hwg.item.weapons.Minigun;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class TechnodemonGreaterRender extends GeoEntityRenderer<TechnodemonGreaterEntity> {

	public TechnodemonGreaterRender(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new TechnodemonGreaterModel());
		this.shadowRadius = 0.7F;
		addRenderLayer(new BlockAndItemGeoLayer<>(this) {
			@Nullable
			@Override
			protected ItemStack getStackForBone(GeoBone bone, TechnodemonGreaterEntity animatable) {
				return switch (bone.getName()) {
				case "rightHand" -> animatable.getItemBySlot(EquipmentSlot.MAINHAND);
				default -> null;
				};
			}

			@Override
			protected ItemTransforms.TransformType getTransformTypeForStack(GeoBone bone, ItemStack stack,
					TechnodemonGreaterEntity animatable) {
				return switch (bone.getName()) {
				default -> ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
				};
			}

			@Override
			protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack,
					TechnodemonGreaterEntity animatable, MultiBufferSource bufferSource, float partialTick,
					int packedLight, int packedOverlay) {
				poseStack.mulPose(Vector3f.XP
						.rotationDegrees(animatable.getMainHandItem().getItem() instanceof Minigun ? -15 : -90));
				poseStack.mulPose(Vector3f.YP
						.rotationDegrees(animatable.getMainHandItem().getItem() instanceof Minigun ? -15 : 0));
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(0f));
				poseStack.translate(animatable.getMainHandItem().getItem() instanceof Minigun ? 0.10D : 0.1D,
						animatable.getMainHandItem().getItem() instanceof Minigun ? 0.1D : 0.15D,
						animatable.getMainHandItem().getItem() instanceof Minigun ? -0.5D : -0.1D);
				if (animatable.getMainHandItem().getItem() instanceof BrimstoneItem)
					poseStack.scale(1.1F, 1.1F, 1.1F);
				super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight,
						packedOverlay);
			}
		});
	}

}