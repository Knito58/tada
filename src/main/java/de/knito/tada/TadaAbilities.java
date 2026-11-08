package de.knito.tada;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

/** Trim pattern -> potion effect. One pattern gives exactly one ability. */
public final class TadaAbilities {
	public enum Ability {
		FIRE_RESISTANCE(MobEffects.FIRE_RESISTANCE, 0),
		CONDUIT_POWER(MobEffects.CONDUIT_POWER, 0),
		SLOW_FALLING(MobEffects.SLOW_FALLING, 0),
		NIGHT_VISION(MobEffects.NIGHT_VISION, 0),
		JUMP_BOOST(MobEffects.JUMP_BOOST, 1),
		STRENGTH(MobEffects.STRENGTH, 0);

		final Holder<MobEffect> effect;
		/** Fixed amplifier (0 = level I). Strength ignores it and scales with the piece count. */
		final int amplifier;

		Ability(Holder<MobEffect> effect, int amplifier) {
			this.effect = effect;
			this.amplifier = amplifier;
		}
	}

	private static final Map<ResourceKey<TrimPattern>, Ability> BY_PATTERN = new HashMap<>();

	static {
		map(Ability.FIRE_RESISTANCE, TrimPatterns.RIB, TrimPatterns.SNOUT, TrimPatterns.HOST);
		map(Ability.CONDUIT_POWER, TrimPatterns.TIDE, TrimPatterns.COAST, TrimPatterns.SENTRY);
		map(Ability.SLOW_FALLING, TrimPatterns.VEX, TrimPatterns.FLOW, TrimPatterns.RAISER);
		map(Ability.NIGHT_VISION, TrimPatterns.EYE, TrimPatterns.WAYFINDER, TrimPatterns.WARD);
		map(Ability.JUMP_BOOST, TrimPatterns.SPIRE, TrimPatterns.WILD);
		map(Ability.STRENGTH, TrimPatterns.BOLT, TrimPatterns.SILENCE);
		// No ability (reserved): DUNE, SHAPER.
	}

	@SafeVarargs
	private static void map(Ability ability, ResourceKey<TrimPattern>... patterns) {
		for (ResourceKey<TrimPattern> pattern : patterns) {
			BY_PATTERN.put(pattern, ability);
		}
	}

	/** The ability a trim pattern grants, or null for patterns without one. */
	public static Ability forPattern(ResourceKey<TrimPattern> pattern) {
		return BY_PATTERN.get(pattern);
	}

	/** All patterns that have an ability, in a fixed order. */
	public static java.util.List<ResourceKey<TrimPattern>> patterns() {
		return BY_PATTERN.keySet().stream().sorted(java.util.Comparator.comparing(k -> k.identifier().toString())).toList();
	}

	private TadaAbilities() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			TadaConfig config = TadaConfig.get();
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				// Per-player phase: spreads the work over ticks and applies at once after joining.
				if (player.isAlive() && player.tickCount % config.checkIntervalTicks == 0) {
					apply(player, config);
				}
			}
		});
	}

	private static void apply(ServerPlayer player, TadaConfig config) {
		Map<Ability, Integer> pieces = new EnumMap<>(Ability.class);
		for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (config.onlyTadaArmor && !TadaItems.ALL.contains(stack.getItem())) {
				continue;
			}
			ArmorTrim trim = stack.get(DataComponents.TRIM);
			if (trim == null) {
				continue;
			}
			Optional<ResourceKey<TrimPattern>> pattern = trim.pattern().unwrapKey();
			Ability ability = pattern.map(BY_PATTERN::get).orElse(null);
			if (ability != null) {
				pieces.merge(ability, 1, Integer::sum);
			}
		}
		pieces.forEach((ability, count) -> {
			int amplifier = ability.amplifier;
			if (ability == Ability.STRENGTH) {
				int level = Math.min(config.strengthMaxLevel, count * config.strengthLevelsPerPiece);
				amplifier = level - 1;
			}
			// ambient=false, visible=false: no particles. showIcon defaults to true, so icon and timer stay.
			player.addEffect(new MobEffectInstance(ability.effect, config.effectDurationTicks, amplifier, false, false, true));
		});
	}
}
