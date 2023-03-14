package mod.azure.hwg.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import mod.azure.hwg.client.ClientInit;
import mod.azure.hwg.util.registry.HWGItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player {

	public AbstractClientPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile, ProfilePublicKey profilePublicKey) {
		super(world, pos, yaw, gameProfile, profilePublicKey);
	}

	@Inject(at = @At("HEAD"), method = "getFieldOfViewModifier", cancellable = true)
	private void render(CallbackInfoReturnable<Float> ci) {
		var itemStack = this.getMainHandItem();
		if (Minecraft.getInstance().options.getCameraType().isFirstPerson())
			if (itemStack.is(HWGItems.SNIPER))
				ci.setReturnValue(ClientInit.scope.isDown() ? 0.1F : 1.0F);
	}
}
