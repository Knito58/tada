package de.knito.tada;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/** Client side only: a tooltip line on armor trim smithing templates that names the ability. */
public final class TadaClient implements ClientModInitializer {
	private static final String TEMPLATE_SUFFIX = "_armor_trim_smithing_template";

	/** onlyTadaArmor as reported by the server; null = unknown (server without TADA), then no armor hint is shown. */
	private static volatile Boolean serverOnlyTadaArmor;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE,
			(payload, context) -> serverOnlyTadaArmor = payload.onlyTadaArmor());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> serverOnlyTadaArmor = null);
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			ArmorTrim trim = stack.get(DataComponents.TRIM);
			if (trim != null) {
				addArmorHint(stack, trim, lines);
				return;
			}
			Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
			String path = id.getPath();
			if (!id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) || !path.endsWith(TEMPLATE_SUFFIX)) {
				return;
			}
			String pattern = path.substring(0, path.length() - TEMPLATE_SUFFIX.length());
			ResourceKey<TrimPattern> key = ResourceKey.create(Registries.TRIM_PATTERN, Identifier.withDefaultNamespace(pattern));
			TadaAbilities.Ability ability = TadaAbilities.forPattern(key);
			if (ability != null) {
				lines.add(hint(ability).withStyle(ChatFormatting.GRAY));
			}
		});
	}

	/** Hint on trimmed armor, only if this piece really gives the ability on the server. */
	private static void addArmorHint(net.minecraft.world.item.ItemStack stack, ArmorTrim trim, java.util.List<Component> lines) {
		Boolean onlyTada = serverOnlyTadaArmor;
		if (onlyTada == null || (onlyTada && !TadaItems.ALL.contains(stack.getItem()))) {
			return;
		}
		TadaAbilities.Ability ability = trim.pattern().unwrapKey().map(TadaAbilities::forPattern).orElse(null);
		if (ability != null) {
			lines.add(hint(ability).withStyle(ChatFormatting.GRAY));
		}
	}

	private static MutableComponent hint(TadaAbilities.Ability ability) {
		MutableComponent name = ability.effect.value().getDisplayName().copy();
		if (ability == TadaAbilities.Ability.STRENGTH) {
			return Component.translatable("tada.tooltip.ability_stacking", name);
		}
		if (ability.amplifier > 0) {
			name = Component.translatable("potion.withAmplifier", name, Component.translatable("potion.potency." + ability.amplifier));
		}
		return Component.translatable("tada.tooltip.ability", name);
	}
}
