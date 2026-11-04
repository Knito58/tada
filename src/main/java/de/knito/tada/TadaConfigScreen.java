package de.knito.tada;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game editor for config/tada.json, built with YACL (optional dependency, see TadaModMenu).
 * Every option has a name and a mouse-over description from the language file:
 * tada.config.<field> and tada.config.<field>.desc.
 * To add a setting: add the field to TadaConfig, add one line here and two keys to en_us.json.
 */
public final class TadaConfigScreen {
	private static final TadaConfig DEFAULTS = new TadaConfig();

	private TadaConfigScreen() {
	}

	public static Screen create(Screen parent) {
		TadaConfig c = TadaConfig.get();
		return YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("tada.config.title"))
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("tada.config.category.abilities"))
				.option(number("checkIntervalTicks", DEFAULTS.checkIntervalTicks, 1, 6000,
					() -> c.checkIntervalTicks, v -> c.checkIntervalTicks = v))
				.option(number("effectDurationTicks", DEFAULTS.effectDurationTicks, 1, 72000,
					() -> c.effectDurationTicks, v -> c.effectDurationTicks = v))
				.option(number("strengthLevelsPerPiece", DEFAULTS.strengthLevelsPerPiece, 1, 255,
					() -> c.strengthLevelsPerPiece, v -> c.strengthLevelsPerPiece = v))
				.option(number("strengthMaxLevel", DEFAULTS.strengthMaxLevel, 1, 255,
					() -> c.strengthMaxLevel, v -> c.strengthMaxLevel = v))
				.option(bool("onlyTadaArmor", DEFAULTS.onlyTadaArmor,
					() -> c.onlyTadaArmor, v -> c.onlyTadaArmor = v))
				.build())
			.category(ConfigCategory.createBuilder()
				.name(Component.translatable("tada.config.category.piglins"))
				.option(bool("goldTrimCountsForPiglins", DEFAULTS.goldTrimCountsForPiglins,
					() -> c.goldTrimCountsForPiglins, v -> c.goldTrimCountsForPiglins = v))
				.build())
			.save(TadaConfig::save)
			.build()
			.generateScreen(parent);
	}

	private static Option<Integer> number(String key, int def, int min, int max, Supplier<Integer> get, Consumer<Integer> set) {
		return Option.<Integer>createBuilder()
			.name(Component.translatable("tada.config." + key))
			.description(OptionDescription.of(Component.translatable("tada.config." + key + ".desc")))
			.binding(def, get, set)
			.controller(o -> IntegerFieldControllerBuilder.create(o).range(min, max))
			.build();
	}

	private static Option<Boolean> bool(String key, boolean def, Supplier<Boolean> get, Consumer<Boolean> set) {
		return Option.<Boolean>createBuilder()
			.name(Component.translatable("tada.config." + key))
			.description(OptionDescription.of(Component.translatable("tada.config." + key + ".desc")))
			.binding(def, get, set)
			.controller(o -> BooleanControllerBuilder.create(o).yesNoFormatter())
			.build();
	}
}
