package de.knito.tada;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/** Replaces the stack with a random armor trim smithing template whose pattern has a TADA ability. */
public final class TadaTemplateFunction extends LootItemConditionalFunction {
	public static final MapCodec<TadaTemplateFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> commonFields(i).apply(i, TadaTemplateFunction::new)
	);

	private TadaTemplateFunction(Optional<Holder<LootItemCondition>> condition) {
		super(condition);
	}

	@Override
	public MapCodec<TadaTemplateFunction> codec() {
		return MAP_CODEC;
	}

	@Override
	protected ItemStack run(ItemStack ignored, LootContext context) {
		List<ResourceKey<TrimPattern>> patterns = TadaAbilities.patterns();
		ResourceKey<TrimPattern> pattern = patterns.get(context.getRandom().nextInt(patterns.size()));
		Item template = BuiltInRegistries.ITEM.getValue(
			Identifier.withDefaultNamespace(pattern.identifier().getPath() + "_armor_trim_smithing_template"));
		return template == Items.AIR ? ItemStack.EMPTY : new ItemStack(template);
	}
}
