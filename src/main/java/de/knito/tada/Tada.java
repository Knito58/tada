package de.knito.tada;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tada implements ModInitializer {
	public static final String MOD_ID = "tada";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		TadaConfig.load();
		TadaItems.register();
		TadaAbilities.register();
		TadaLoot.register();
		// Tell each joining client whether abilities need TADA armor, so tooltips match the server.
		PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			sender.sendPacket(new ConfigSyncPayload(TadaConfig.get().onlyTadaArmor)));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
			for (var item : TadaItems.ALL) {
				entries.accept(item);
			}
		});
		LOGGER.info("TADA loaded");
	}
}
