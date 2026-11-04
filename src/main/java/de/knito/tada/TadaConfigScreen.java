package de.knito.tada;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * In-game editor for config/tada.json, built only from vanilla widgets (no config library needed).
 * Each setting is one row: label, input (text box for numbers, Yes/No button for flags), Reset button.
 * Label, input and Reset all show the mouse-over description tada.config.<field>.desc.
 * To add a setting: add the field to TadaConfig, one line in addRows(), and two keys to en_us.json.
 */
public final class TadaConfigScreen extends Screen {
	private static final TadaConfig DEFAULTS = new TadaConfig();
	private static final int ROW_HEIGHT = 24;
	private static final int LABEL_WIDTH = 170;
	private static final int FIELD_WIDTH = 110;
	private static final int RESET_WIDTH = 60;
	private static final int GAP = 6;

	private final Screen parent;
	/** Applies the typed values to the config; one entry per row. */
	private final List<Runnable> appliers = new ArrayList<>();
	private int rowY;

	public TadaConfigScreen(Screen parent) {
		super(Component.translatable("tada.config.title"));
		this.parent = parent;
	}

	public static Screen create(Screen parent) {
		return new TadaConfigScreen(parent);
	}

	@Override
	protected void init() {
		appliers.clear();
		addRenderableWidget(centered(title, 12));
		rowY = 36;
		TadaConfig c = TadaConfig.get();
		addRows(c);
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(width / 2 - 100, Math.min(height - 28, rowY + 10), 200, 20).build());
	}

	private void addRows(TadaConfig c) {
		section("tada.config.category.abilities");
		number("checkIntervalTicks", DEFAULTS.checkIntervalTicks, 1, 6000, () -> c.checkIntervalTicks, v -> c.checkIntervalTicks = v);
		number("effectDurationTicks", DEFAULTS.effectDurationTicks, 1, 72000, () -> c.effectDurationTicks, v -> c.effectDurationTicks = v);
		number("strengthLevelsPerPiece", DEFAULTS.strengthLevelsPerPiece, 1, 255, () -> c.strengthLevelsPerPiece, v -> c.strengthLevelsPerPiece = v);
		number("strengthMaxLevel", DEFAULTS.strengthMaxLevel, 1, 255, () -> c.strengthMaxLevel, v -> c.strengthMaxLevel = v);
		flag("onlyTadaArmor", DEFAULTS.onlyTadaArmor, () -> c.onlyTadaArmor, v -> c.onlyTadaArmor = v);
		section("tada.config.category.piglins");
		flag("goldTrimCountsForPiglins", DEFAULTS.goldTrimCountsForPiglins, () -> c.goldTrimCountsForPiglins, v -> c.goldTrimCountsForPiglins = v);
	}

	private void section(String key) {
		addRenderableWidget(centered(Component.translatable(key), rowY + 4));
		rowY += 18;
	}

	private StringWidget centered(Component text, int y) {
		int w = font.width(text);
		return new StringWidget((width - w) / 2, y, w, 12, text, font);
	}

	private int left() {
		return (width - (LABEL_WIDTH + FIELD_WIDTH + RESET_WIDTH + 2 * GAP)) / 2;
	}

	private StringWidget label(String key, Tooltip tip) {
		// Width = text width, so the result does not depend on the widget's text alignment.
		Component text = Component.translatable("tada.config." + key);
		StringWidget label = new StringWidget(left(), rowY + 5, font.width(text), 12, text, font);
		label.setTooltip(tip);
		return addRenderableWidget(label);
	}

	private void number(String key, int def, int min, int max, IntSupplier get, IntConsumer set) {
		Tooltip tip = Tooltip.create(Component.translatable("tada.config." + key + ".desc"));
		label(key, tip);
		EditBox box = new EditBox(font, left() + LABEL_WIDTH + GAP, rowY, FIELD_WIDTH, 20, Component.translatable("tada.config." + key));
		box.setMaxLength(6);
		box.setValue(Integer.toString(get.getAsInt()));
		box.setTooltip(tip);
		addRenderableWidget(box);
		addReset(key, def, () -> box.setValue(Integer.toString(def)));
		appliers.add(() -> {
			try {
				set.accept(Math.max(min, Math.min(max, Integer.parseInt(box.getValue().trim()))));
			} catch (NumberFormatException e) {
				// Unreadable text: keep the previous value.
			}
		});
		rowY += ROW_HEIGHT;
	}

	private void flag(String key, boolean def, BooleanSupplier get, Consumer<Boolean> set) {
		Tooltip tip = Tooltip.create(Component.translatable("tada.config." + key + ".desc"));
		label(key, tip);
		CycleButton<Boolean> button = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF, get.getAsBoolean())
			.withTooltip(v -> tip)
			.create(left() + LABEL_WIDTH + GAP, rowY, FIELD_WIDTH, 20, Component.translatable("tada.config." + key));
		addRenderableWidget(button);
		addReset(key, def, () -> button.setValue(def));
		appliers.add(() -> set.accept(button.getValue()));
		rowY += ROW_HEIGHT;
	}

	private void addReset(String key, Object def, Runnable action) {
		String shown = def instanceof Boolean b ? (b ? "Yes" : "No") : String.valueOf(def);
		addRenderableWidget(Button.builder(Component.translatable("tada.config.reset"), b -> action.run())
			.bounds(left() + LABEL_WIDTH + FIELD_WIDTH + 2 * GAP, rowY, RESET_WIDTH, 20)
			.tooltip(Tooltip.create(Component.translatable("tada.config.reset.desc", shown)))
			.build());
	}

	@Override
	public void onClose() {
		appliers.forEach(Runnable::run);
		TadaConfig.save();
		minecraft.setScreenAndShow(parent);
	}
}
