package de.knito.tada;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
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
	/** A widget in the scrolling list: y is its position in content space, before scrolling. */
	private record Scrolled(AbstractWidget widget, int y) {
	}

	private final List<Scrolled> scrolled = new ArrayList<>();
	private int rowY;
	private int scroll;

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
		scrolled.clear();
		addRenderableWidget(centered(title, 12));
		rowY = 0;
		addRows(TadaConfig.get());
		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
			.bounds(width / 2 - 100, height - 28, 200, 20).build());
		scroll = Math.max(0, Math.min(scroll, maxScroll()));
		applyScroll();
	}

	private int viewTop() {
		return 30;
	}

	private int viewBottom() {
		return height - 34;
	}

	private int maxScroll() {
		return Math.max(0, rowY - (viewBottom() - viewTop()));
	}

	/** Moves every list widget to its scrolled position and hides those outside the visible area. */
	private void applyScroll() {
		for (Scrolled s : scrolled) {
			int y = viewTop() + s.y() - scroll;
			s.widget().setY(y);
			s.widget().visible = y >= viewTop() - 2 && y + s.widget().getHeight() <= viewBottom() + 2;
		}
	}

	private <T extends AbstractWidget> T addScrolled(T widget) {
		scrolled.add(new Scrolled(widget, widget.getY()));
		return addRenderableWidget(widget);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		int old = scroll;
		scroll = Math.max(0, Math.min(maxScroll(), scroll - (int) Math.signum(scrollY) * ROW_HEIGHT));
		if (scroll != old) {
			applyScroll();
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		int max = maxScroll();
		if (max > 0) {
			// Thin scroll bar at the right edge.
			int track = viewBottom() - viewTop();
			int bar = Math.max(20, track * track / (track + max));
			int y = viewTop() + (track - bar) * scroll / max;
			graphics.fill(width - 6, viewTop(), width - 2, viewBottom(), 0x80000000);
			graphics.fill(width - 6, y, width - 2, y + bar, 0xFFA0A0A0);
		}
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
		section("tada.config.category.lootChances");
		percent("lootChanceEndCity", DEFAULTS.lootChanceEndCity, () -> c.lootChanceEndCity, v -> c.lootChanceEndCity = v);
		percent("lootChanceNetherFortress", DEFAULTS.lootChanceNetherFortress, () -> c.lootChanceNetherFortress, v -> c.lootChanceNetherFortress = v);
		percent("lootChanceBastion", DEFAULTS.lootChanceBastion, () -> c.lootChanceBastion, v -> c.lootChanceBastion = v);
		percent("lootChanceAncientCity", DEFAULTS.lootChanceAncientCity, () -> c.lootChanceAncientCity, v -> c.lootChanceAncientCity = v);
		percent("lootChanceWoodlandMansion", DEFAULTS.lootChanceWoodlandMansion, () -> c.lootChanceWoodlandMansion, v -> c.lootChanceWoodlandMansion = v);
		percent("lootChanceTrialVault", DEFAULTS.lootChanceTrialVault, () -> c.lootChanceTrialVault, v -> c.lootChanceTrialVault = v);
		percent("lootChanceOminousVault", DEFAULTS.lootChanceOminousVault, () -> c.lootChanceOminousVault, v -> c.lootChanceOminousVault = v);
		percent("lootChanceBuriedTreasure", DEFAULTS.lootChanceBuriedTreasure, () -> c.lootChanceBuriedTreasure, v -> c.lootChanceBuriedTreasure = v);
		percent("lootChanceShipwreck", DEFAULTS.lootChanceShipwreck, () -> c.lootChanceShipwreck, v -> c.lootChanceShipwreck = v);
		percent("lootChanceOverworld", DEFAULTS.lootChanceOverworld, () -> c.lootChanceOverworld, v -> c.lootChanceOverworld = v);
		percent("lootChanceFishing", DEFAULTS.lootChanceFishing, () -> c.lootChanceFishing, v -> c.lootChanceFishing = v);
		section("tada.config.category.lootPieces");
		percent("lootTrimChance", DEFAULTS.lootTrimChance, () -> c.lootTrimChance, v -> c.lootTrimChance = v);
		percent("lootDyeChance", DEFAULTS.lootDyeChance, () -> c.lootDyeChance, v -> c.lootDyeChance = v);
		number("lootEnchantLevelsMin", DEFAULTS.lootEnchantLevelsMin, 1, 60, () -> c.lootEnchantLevelsMin, v -> c.lootEnchantLevelsMin = v);
		number("lootEnchantLevelsMax", DEFAULTS.lootEnchantLevelsMax, 1, 60, () -> c.lootEnchantLevelsMax, v -> c.lootEnchantLevelsMax = v);
		section("tada.config.category.trader");
		percent("traderTemplateChance", DEFAULTS.traderTemplateChance, () -> c.traderTemplateChance, v -> c.traderTemplateChance = v);
		percent("traderArmorChance", DEFAULTS.traderArmorChance, () -> c.traderArmorChance, v -> c.traderArmorChance = v);
	}

	private void section(String key) {
		addScrolled(centered(Component.translatable(key), rowY + 4));
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
		return addScrolled(label);
	}

	private void number(String key, int def, int min, int max, IntSupplier get, IntConsumer set) {
		Tooltip tip = Tooltip.create(Component.translatable("tada.config." + key + ".desc"));
		label(key, tip);
		EditBox box = new EditBox(font, left() + LABEL_WIDTH + GAP, rowY, FIELD_WIDTH, 20, Component.translatable("tada.config." + key));
		box.setMaxLength(6);
		box.setValue(Integer.toString(get.getAsInt()));
		box.setTooltip(tip);
		addScrolled(box);
		addReset(Integer.toString(def), () -> box.setValue(Integer.toString(def)));
		appliers.add(() -> {
			try {
				set.accept(Math.max(min, Math.min(max, Integer.parseInt(box.getValue().trim()))));
			} catch (NumberFormatException e) {
				// Unreadable text: keep the previous value.
			}
		});
		rowY += ROW_HEIGHT;
	}

	/** A chance stored as 0..1 in the config but typed as a percentage (0 to 100). */
	private void percent(String key, double def, DoubleSupplier get, DoubleConsumer set) {
		Tooltip tip = Tooltip.create(Component.translatable("tada.config." + key + ".desc"));
		label(key, tip);
		EditBox box = new EditBox(font, left() + LABEL_WIDTH + GAP, rowY, FIELD_WIDTH, 20, Component.translatable("tada.config." + key));
		box.setMaxLength(7);
		box.setValue(formatPercent(get.getAsDouble()));
		box.setTooltip(tip);
		addScrolled(box);
		addReset(formatPercent(def) + " %", () -> box.setValue(formatPercent(def)));
		appliers.add(() -> {
			try {
				double percent = Double.parseDouble(box.getValue().trim().replace(',', '.').replace("%", ""));
				set.accept(Math.max(0, Math.min(100, percent)) / 100.0);
			} catch (NumberFormatException e) {
				// Unreadable text: keep the previous value.
			}
		});
		rowY += ROW_HEIGHT;
	}

	private static String formatPercent(double chance) {
		double percent = Math.round(chance * 1000.0) / 10.0;
		return percent == Math.rint(percent) ? Long.toString((long) percent) : Double.toString(percent);
	}

	private void flag(String key, boolean def, BooleanSupplier get, Consumer<Boolean> set) {
		Tooltip tip = Tooltip.create(Component.translatable("tada.config." + key + ".desc"));
		label(key, tip);
		CycleButton<Boolean> button = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF, get.getAsBoolean())
			.withTooltip(v -> tip)
			.create(left() + LABEL_WIDTH + GAP, rowY, FIELD_WIDTH, 20, Component.translatable("tada.config." + key));
		addScrolled(button);
		addReset(def ? "ON" : "OFF", () -> button.setValue(def));
		appliers.add(() -> set.accept(button.getValue()));
		rowY += ROW_HEIGHT;
	}

	private void addReset(String shown, Runnable action) {
		addScrolled(Button.builder(Component.translatable("tada.config.reset"), b -> action.run())
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
