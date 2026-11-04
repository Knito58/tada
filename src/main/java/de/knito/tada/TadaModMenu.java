package de.knito.tada;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/** Mod Menu entry point. The config button only appears when YACL is installed as well. */
public final class TadaModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
			return parent -> null;
		}
		// TadaConfigScreen is only touched here, so a missing YACL never causes a class loading error.
		return TadaConfigScreen::create;
	}
}
