package de.knito.tada;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Tells joining clients which armor counts on this server, so the ability hints match the server's rules
 * and not the client's own config file. Clients without TADA never receive it.
 */
public final class TadaNetworking {
	public record ConfigPayload(boolean onlyTadaArmor) implements CustomPacketPayload {
		public static final CustomPacketPayload.Type<ConfigPayload> TYPE = new CustomPacketPayload.Type<>(TadaItems.id("config"));
		public static final StreamCodec<ByteBuf, ConfigPayload> CODEC =
			StreamCodec.composite(ByteBufCodecs.BOOL, ConfigPayload::onlyTadaArmor, ConfigPayload::new);

		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	private TadaNetworking() {
	}

	public static void register() {
		PayloadTypeRegistry.clientboundPlay().register(ConfigPayload.TYPE, ConfigPayload.CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (ServerPlayNetworking.canSend(handler, ConfigPayload.TYPE)) {
				sender.sendPacket(new ConfigPayload(TadaConfig.get().onlyTadaArmor));
			}
		});
	}
}
