package mod.azure.hwg.network;

import io.netty.buffer.Unpooled;
import mod.azure.hwg.HWGMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HWGEntityPacket {

	public static final Identifier ID = new Identifier(HWGMod.MODID, "spawn_entity");

	public static Packet<ClientPlayPacketListener> createPacket(Entity entity) {
		PacketByteBuf buf = createBuffer();
		buf.writeVarInt(Registries.ENTITY_TYPE.getRawId(entity.getType()));
		buf.writeUuid(entity.getUuid());
		buf.writeVarInt(entity.getId());
		buf.writeDouble(entity.getX());
		buf.writeDouble(entity.getY());
		buf.writeDouble(entity.getZ());
		buf.writeByte(MathHelper.floor((entity.getPitch() * 360) / 256f));
		buf.writeByte(MathHelper.floor((entity.getYaw() * 360) / 256f));
		buf.writeFloat(entity.getPitch());
		buf.writeFloat(entity.getYaw());
		return ServerPlayNetworking.createS2CPacket(ID, buf);
	}

	private static PacketByteBuf createBuffer() {
		return new PacketByteBuf(Unpooled.buffer());
	}
}