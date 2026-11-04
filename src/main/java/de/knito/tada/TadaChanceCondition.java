package de.knito.tada;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/** True with the chance the config gives for the source. Read at generation time, so config changes apply at once. */
public record TadaChanceCondition(TadaLoot.Source source) implements LootItemCondition {
	public static final MapCodec<TadaChanceCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> i.group(TadaLoot.Source.CODEC.fieldOf("source").forGetter(TadaChanceCondition::source)).apply(i, TadaChanceCondition::new)
	);

	@Override
	public MapCodec<TadaChanceCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(LootContext context) {
		return context.getRandom().nextDouble() < source.chance(TadaConfig.get());
	}
}
