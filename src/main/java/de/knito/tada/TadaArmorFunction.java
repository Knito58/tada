package de.knito.tada;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Replaces the stack with a random finished TADA piece: always enchanted, trimmed and dyed by chance.
 * Whether this happens at all is decided by {@link TadaChanceCondition}, not here.
 */
public final class TadaArmorFunction extends LootItemConditionalFunction {
	public static final MapCodec<TadaArmorFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> commonFields(i).apply(i, TadaArmorFunction::new)
	);

	public TadaArmorFunction(Optional<Holder<LootItemCondition>> condition) {
		super(condition);
	}

	@Override
	public MapCodec<TadaArmorFunction> codec() {
		return MAP_CODEC;
	}

	@Override
	protected ItemStack run(ItemStack ignored, LootContext context) {
		TadaConfig config = TadaConfig.get();
		RandomSource random = context.getRandom();
		RegistryAccess access = context.getLevel().registryAccess();
		Item piece = TadaItems.ALL.get(random.nextInt(TadaItems.ALL.size()));
		ItemStack stack = new ItemStack(piece);

		if (random.nextDouble() < config.lootDyeChance) {
			List<DyeColor> dyes = new ArrayList<>();
			int count = 1 + random.nextInt(2);
			for (int i = 0; i < count; i++) {
				dyes.add(DyeColor.VALUES.get(random.nextInt(DyeColor.VALUES.size())));
			}
			stack = DyedItemColor.applyDyes(stack, dyes);
		}
		if (random.nextDouble() < config.lootTrimChance) {
			ArmorTrim trim = randomTrim(access, random);
			if (trim != null) {
				stack.set(DataComponents.TRIM, trim);
			}
		}
		int levels = config.lootEnchantLevelsMin + random.nextInt(config.lootEnchantLevelsMax - config.lootEnchantLevelsMin + 1);
		HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> enchantments = access.lookupOrThrow(Registries.ENCHANTMENT);
		return EnchantmentHelper.enchantItem(random, stack, levels, access, enchantments.get(EnchantmentTags.ON_RANDOM_LOOT));
	}

	/** A random pattern that has an ability, with a random material. Null if the registries lack them. */
	static ArmorTrim randomTrim(RegistryAccess access, RandomSource random) {
		List<ResourceKey<TrimPattern>> patterns = TadaAbilities.patterns();
		HolderLookup.RegistryLookup<TrimPattern> patternRegistry = access.lookupOrThrow(Registries.TRIM_PATTERN);
		HolderLookup.RegistryLookup<TrimMaterial> materialRegistry = access.lookupOrThrow(Registries.TRIM_MATERIAL);
		List<Holder.Reference<TrimMaterial>> materials = materialRegistry.listElements().toList();
		if (patterns.isEmpty() || materials.isEmpty()) {
			return null;
		}
		Optional<Holder.Reference<TrimPattern>> pattern = patternRegistry.get(patterns.get(random.nextInt(patterns.size())));
		return pattern.map(p -> new ArmorTrim(materials.get(random.nextInt(materials.size())), p)).orElse(null);
	}
}
