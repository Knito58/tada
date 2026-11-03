package de.knito.tada;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Settings in config/tada.json. Written with defaults on first start; invalid values are clamped. */
public final class TadaConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static TadaConfig instance = new TadaConfig();

	/** How often (ticks) the worn armor is checked per player. 100 = 5 s. */
	public int checkIntervalTicks = 100;
	/** Duration (ticks) of each applied effect. 140 = 7 s. Must exceed the interval, or there are gaps. */
	public int effectDurationTicks = 140;
	/** Strength levels added per Strength-trimmed piece. 2: one piece = Strength II. */
	public int strengthLevelsPerPiece = 2;
	/** Highest Strength level that can be reached. 8 = Strength VIII (4 pieces at 2 levels). */
	public int strengthMaxLevel = 8;
	/** If true, only TADA armor gives trim abilities; if false, any trimmed armor does. */
	public boolean onlyTadaArmor = false;
	/** If true, a piece with a gold trim material counts as golden armor for piglins. */
	public boolean goldTrimCountsForPiglins = true;

	public static TadaConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("tada.json");
		TadaConfig loaded = null;
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				loaded = GSON.fromJson(reader, TadaConfig.class);
			} catch (Exception e) {
				Tada.LOGGER.warn("Could not read {}, using defaults", path, e);
			}
		}
		if (loaded == null) {
			loaded = new TadaConfig();
		}
		loaded.sanitize();
		instance = loaded;
		// Always rewrite, so new options appear in old files and clamped values are visible.
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(loaded, writer);
		} catch (IOException e) {
			Tada.LOGGER.warn("Could not write {}", path, e);
		}
	}

	private void sanitize() {
		checkIntervalTicks = Math.max(1, checkIntervalTicks);
		effectDurationTicks = Math.max(checkIntervalTicks + 20, effectDurationTicks);
		strengthLevelsPerPiece = Math.max(1, strengthLevelsPerPiece);
		strengthMaxLevel = Math.max(1, Math.min(255, strengthMaxLevel));
	}
}
