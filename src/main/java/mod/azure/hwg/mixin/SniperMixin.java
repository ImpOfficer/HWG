package mod.azure.hwg.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import mod.azure.hwg.item.weapons.SniperItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(InGameHud.class)
public abstract class SniperMixin extends DrawableHelper {

	private static final Identifier SNIPER = new Identifier("hwg", "textures/gui/pumpkinblur.png");
	@Shadow
	private final MinecraftClient client;
	@Shadow
	private int scaledWidth;
	@Shadow
	private int scaledHeight;
	private boolean scoped = true;

	public SniperMixin(MinecraftClient client) {
		this.client = client;
	}

	@Inject(at = @At("TAIL"), method = "render")
	private void render(CallbackInfo info) {
		ItemStack itemStack = this.client.player.inventory.getMainHandStack();
		if (this.client.options.getPerspective().isFirstPerson() && itemStack.getItem() instanceof SniperItem) {
			if (this.client.options.keySneak.isPressed()) {
				if (this.scoped == true) {
					this.client.options.fov = this.client.options.fov - 60;
					this.scoped = false;
				}
				this.renderSniperOverlay();
			} else {
				if (!this.scoped) {
					this.client.options.fov = this.client.options.fov + 60;
					this.scoped = true;
				}
			}
		}
	}

	private void renderSniperOverlay() {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableAlphaTest();
		this.client.getTextureManager().bindTexture(SNIPER);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0D, (double) this.scaledHeight, -90.0D).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double) this.scaledWidth, (double) this.scaledHeight, -90.0D).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double) this.scaledWidth, 0.0D, -90.0D).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
