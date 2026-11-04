package de.knito.tada;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Entry point for Mod Menu (optional). Only loaded when Mod Menu is installed. */
public final class TadaModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return TadaConfigScreen::new;
	}
}
