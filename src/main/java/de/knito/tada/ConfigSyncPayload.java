package de.knito.tada;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Server -> client, sent on join: the server config value the client needs to show correct tooltips. */
public record ConfigSyncPayload(boolean onlyTadaArmor) implements CustomPacketPayload {
	public static final Type<ConfigSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Tada.MOD_ID, "config_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC =
		ByteBufCodecs.BOOL.map(ConfigSyncPayload::new, ConfigSyncPayload::onlyTadaArmor).cast();

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
