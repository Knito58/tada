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
	/**
	 * Duration (ticks) of each applied effect. 320 = 16 s.
	 * Vanilla blinks the HUD icon and flickers Night Vision brightness while an effect has 200 ticks or less left.
	 * Refreshing every 100 ticks keeps the remaining time between 220 and 320, so nothing blinks.
	 * Values below checkIntervalTicks + 201 are raised to that minimum on load.
	 */
	public int effectDurationTicks = 320;
	/** Strength levels added per Strength-trimmed piece. 2: one piece = Strength II. */
	public int strengthLevelsPerPiece = 2;
	/** Highest Strength level that can be reached. 8 = Strength VIII (4 pieces at 2 levels). */
	public int strengthMaxLevel = 8;
	/**
	 * If true, only TADA armor gives trim abilities; if false, any trimmed armor does.
	 * The "Ability: X" tooltip on trimmed armor follows this value, taken from the server on join (also in
	 * multiplayer). It is computed when the tooltip is shown, so it also appears on pieces trimmed earlier.
	 * On a server without TADA no armor hint is shown.
	 */
	public boolean onlyTadaArmor = false;
	/** If true, a piece with a gold trim material counts as golden armor for piglins. */
	public boolean goldTrimCountsForPiglins = true;

	// Loot: chance that one finished TADA piece (enchanted, usually trimmed and dyed) is added to the container or catch.
	public double lootChanceEndCity = 0.12;
	public double lootChanceNetherFortress = 0.06;
	public double lootChanceBastion = 0.10;
	public double lootChanceAncientCity = 0.06;
	public double lootChanceWoodlandMansion = 0.06;
	public double lootChanceTrialVault = 0.05;
	public double lootChanceOminousVault = 0.12;
	public double lootChanceBuriedTreasure = 0.08;
	public double lootChanceShipwreck = 0.04;
	/** Per treasure catch (a treasure catch itself is rare). */
	public double lootChanceFishing = 0.03;
	/** Common chests: dungeons, mineshafts, temples, strongholds, outposts, ruined portals, igloos, big ruins, some village chests. */
	public double lootChanceOverworld = 0.04;
	/** Chance that a found piece carries a trim. */
	public double lootTrimChance = 0.6;
	/** Chance that a found piece is dyed. */
	public double lootDyeChance = 0.5;
	/** Enchantment levels used for found pieces (like an enchanting table level, 30 is max in the table). */
	public int lootEnchantLevelsMin = 15;
	public int lootEnchantLevelsMax = 35;

	/** Wandering trader: chance per trader that he offers a random armor trim template with an ability. 0 = never. */
	public double traderTemplateChance = 0.6;
	/** Wandering trader: chance per trader that he offers a finished (enchanted, trimmed, dyed) TADA piece. 0 = never. */
	public double traderArmorChance = 0.3;

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
		write(loaded);
	}

	/** Clamps the current values and writes config/tada.json. Used by the config screen. */
	public static void save() {
		instance.sanitize();
		write(instance);
	}

	private static void write(TadaConfig config) {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("tada.json");
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(config, writer);
		} catch (IOException e) {
			Tada.LOGGER.warn("Could not write {}", path, e);
		}
	}

	private void sanitize() {
		checkIntervalTicks = Math.max(1, checkIntervalTicks);
		// Remaining time must never fall to 200 ticks, or the HUD icon blinks and Night Vision flickers.
		effectDurationTicks = Math.max(checkIntervalTicks + 201, effectDurationTicks);
		strengthLevelsPerPiece = Math.max(1, strengthLevelsPerPiece);
		strengthMaxLevel = Math.max(1, Math.min(255, strengthMaxLevel));
		lootChanceEndCity = chance(lootChanceEndCity);
		lootChanceNetherFortress = chance(lootChanceNetherFortress);
		lootChanceBastion = chance(lootChanceBastion);
		lootChanceAncientCity = chance(lootChanceAncientCity);
		lootChanceWoodlandMansion = chance(lootChanceWoodlandMansion);
		lootChanceTrialVault = chance(lootChanceTrialVault);
		lootChanceOminousVault = chance(lootChanceOminousVault);
		lootChanceBuriedTreasure = chance(lootChanceBuriedTreasure);
		lootChanceShipwreck = chance(lootChanceShipwreck);
		lootChanceFishing = chance(lootChanceFishing);
		lootChanceOverworld = chance(lootChanceOverworld);
		traderTemplateChance = chance(traderTemplateChance);
		traderArmorChance = chance(traderArmorChance);
		lootTrimChance = chance(lootTrimChance);
		lootDyeChance = chance(lootDyeChance);
		lootEnchantLevelsMin = Math.max(1, Math.min(60, lootEnchantLevelsMin));
		lootEnchantLevelsMax = Math.max(lootEnchantLevelsMin, Math.min(60, lootEnchantLevelsMax));
	}

	private static double chance(double value) {
		return Double.isNaN(value) ? 0 : Math.max(0, Math.min(1, value));
	}
}
