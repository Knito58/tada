package de.knito.tada;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/**
 * Config screen for Mod Menu. Built from vanilla option widgets, so it needs no extra library.
 * Every option has a tooltip (hover with the mouse). Values are applied at once and written to config/tada.json on close.
 */
public final class TadaConfigScreen extends OptionsSubScreen {
	private static final String PREFIX = "tada.config.";

	public TadaConfigScreen(Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable(PREFIX + "title"));
	}

	@Override
	protected void addOptions() {
		TadaConfig c = TadaConfig.get();

		list.addHeader(Component.translatable(PREFIX + "section.abilities"));
		list.addBig(bool("onlyTadaArmor", c.onlyTadaArmor, v -> c.onlyTadaArmor = v));
		list.addBig(bool("goldTrimCountsForPiglins", c.goldTrimCountsForPiglins, v -> c.goldTrimCountsForPiglins = v));
		list.addBig(integer("strengthLevelsPerPiece", 1, 8, c.strengthLevelsPerPiece, v -> c.strengthLevelsPerPiece = v, Integer::toString));
		list.addBig(integer("strengthMaxLevel", 1, 32, c.strengthMaxLevel, v -> c.strengthMaxLevel = v, Integer::toString));
		list.addBig(integer("checkIntervalTicks", 20, 600, c.checkIntervalTicks, v -> c.checkIntervalTicks = v, TadaConfigScreen::ticks));
		list.addBig(integer("effectDurationTicks", 221, 1200, c.effectDurationTicks, v -> c.effectDurationTicks = v, TadaConfigScreen::ticks));

		list.addHeader(Component.translatable(PREFIX + "section.hints"));
		list.addBig(bool("showAbilityHints", c.showAbilityHints, v -> c.showAbilityHints = v));

		list.addHeader(Component.translatable(PREFIX + "section.loot"));
		list.addBig(percent("lootChanceEndCity", c.lootChanceEndCity, v -> c.lootChanceEndCity = v));
		list.addBig(percent("lootChanceNetherFortress", c.lootChanceNetherFortress, v -> c.lootChanceNetherFortress = v));
		list.addBig(percent("lootChanceBastion", c.lootChanceBastion, v -> c.lootChanceBastion = v));
		list.addBig(percent("lootChanceAncientCity", c.lootChanceAncientCity, v -> c.lootChanceAncientCity = v));
		list.addBig(percent("lootChanceWoodlandMansion", c.lootChanceWoodlandMansion, v -> c.lootChanceWoodlandMansion = v));
		list.addBig(percent("lootChanceTrialVault", c.lootChanceTrialVault, v -> c.lootChanceTrialVault = v));
		list.addBig(percent("lootChanceOminousVault", c.lootChanceOminousVault, v -> c.lootChanceOminousVault = v));
		list.addBig(percent("lootChanceBuriedTreasure", c.lootChanceBuriedTreasure, v -> c.lootChanceBuriedTreasure = v));
		list.addBig(percent("lootChanceShipwreck", c.lootChanceShipwreck, v -> c.lootChanceShipwreck = v));
		list.addBig(percent("lootChanceFishing", c.lootChanceFishing, v -> c.lootChanceFishing = v));
		list.addBig(percent("lootTrimChance", c.lootTrimChance, v -> c.lootTrimChance = v));
		list.addBig(percent("lootDyeChance", c.lootDyeChance, v -> c.lootDyeChance = v));
		list.addBig(integer("lootEnchantLevelsMin", 1, 39, c.lootEnchantLevelsMin, v -> c.lootEnchantLevelsMin = v, Integer::toString));
		list.addBig(integer("lootEnchantLevelsMax", 1, 39, c.lootEnchantLevelsMax, v -> c.lootEnchantLevelsMax = v, Integer::toString));

		list.addHeader(Component.translatable(PREFIX + "section.trader"));
		list.addBig(bool("traderSellsTemplates", c.traderSellsTemplates, v -> c.traderSellsTemplates = v));
		list.addBig(bool("traderSellsArmor", c.traderSellsArmor, v -> c.traderSellsArmor = v));
	}

	@Override
	public void removed() {
		super.removed();
		TadaConfig.save();
	}

	private static OptionInstance<Boolean> bool(String key, boolean initial, Consumer<Boolean> setter) {
		return OptionInstance.createBoolean(PREFIX + key, tip(key), initial, setter::accept);
	}

	private static OptionInstance<Integer> percent(String key, double initial, DoubleConsumer setter) {
		int start = (int) Math.round(initial * 100);
		return new OptionInstance<>(PREFIX + key, tip(key),
			(caption, value) -> Component.translatable("options.percent_value", caption, value),
			new OptionInstance.IntRange(0, 100), start, value -> setter.accept(value / 100.0));
	}

	private static OptionInstance<Integer> integer(String key, int min, int max, int initial, Consumer<Integer> setter,
			java.util.function.IntFunction<String> format) {
		int start = Math.max(min, Math.min(max, initial));
		return new OptionInstance<>(PREFIX + key, tip(key),
			(caption, value) -> Component.translatable("options.generic_value", caption, Component.literal(format.apply(value))),
			new OptionInstance.IntRange(min, max), start, setter::accept);
	}

	private static <T> OptionInstance.TooltipSupplier<T> tip(String key) {
		return OptionInstance.cachedConstantTooltip(Component.translatable(PREFIX + key + ".tooltip"));
	}

	private static String ticks(int ticks) {
		return ticks + " (" + (ticks % 20 == 0 ? Integer.toString(ticks / 20) : String.format("%.1f", ticks / 20.0)) + " s)";
	}
}
