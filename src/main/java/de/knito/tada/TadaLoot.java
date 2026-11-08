package de.knito.tada;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/** Adds finished TADA armor to vanilla loot tables and registers the loot functions used by loot and trades. */
public final class TadaLoot {
	/** Where a finished piece can come from. The chance is read from the config each time loot is generated. */
	public enum Source implements StringRepresentable {
		END_CITY("end_city", c -> c.lootChanceEndCity, "chests/end_city_treasure"),
		NETHER_FORTRESS("nether_fortress", c -> c.lootChanceNetherFortress, "chests/nether_bridge"),
		BASTION("bastion", c -> c.lootChanceBastion, "chests/bastion_treasure"),
		ANCIENT_CITY("ancient_city", c -> c.lootChanceAncientCity, "chests/ancient_city"),
		WOODLAND_MANSION("woodland_mansion", c -> c.lootChanceWoodlandMansion, "chests/woodland_mansion"),
		TRIAL_VAULT("trial_vault", c -> c.lootChanceTrialVault, "chests/trial_chambers/reward"),
		OMINOUS_VAULT("ominous_vault", c -> c.lootChanceOminousVault, "chests/trial_chambers/reward_ominous"),
		BURIED_TREASURE("buried_treasure", c -> c.lootChanceBuriedTreasure, "chests/buried_treasure"),
		SHIPWRECK("shipwreck", c -> c.lootChanceShipwreck, "chests/shipwreck_treasure"),
		OVERWORLD("overworld", c -> c.lootChanceOverworld,
			"chests/simple_dungeon", "chests/abandoned_mineshaft", "chests/desert_pyramid", "chests/jungle_temple",
			"chests/stronghold_corridor", "chests/stronghold_crossing", "chests/stronghold_library",
			"chests/pillager_outpost", "chests/ruined_portal", "chests/igloo_chest", "chests/underwater_ruin_big",
			"chests/village/village_armorer", "chests/village/village_weaponsmith", "chests/village/village_toolsmith",
			"chests/bastion_other", "chests/bastion_bridge", "chests/bastion_hoglin_stable"),
		FISHING("fishing", c -> c.lootChanceFishing, "gameplay/fishing/treasure"),
		/** Wandering trader trades (data pack JSON). No loot table. */
		TRADER_ARMOR("trader_armor", c -> c.traderArmorChance),
		TRADER_TEMPLATES("trader_templates", c -> c.traderTemplateChance);

		public static final Codec<Source> CODEC = StringRepresentable.fromEnum(Source::values);

		private final String name;
		private final ToDoubleFunction<TadaConfig> chance;
		private final String[] tables;

		Source(String name, ToDoubleFunction<TadaConfig> chance, String... tables) {
			this.name = name;
			this.chance = chance;
			this.tables = tables;
		}

		public double chance(TadaConfig config) {
			return chance.applyAsDouble(config);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	private TadaLoot() {
	}

	public static void registerFunctionTypes() {
		Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, TadaItems.id("random_armor"), TadaArmorFunction.MAP_CODEC);
		Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, TadaItems.id("random_template"), TadaTemplateFunction.MAP_CODEC);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, TadaItems.id("chance"), TadaChanceCondition.MAP_CODEC);
	}

	public static void register() {
		registerFunctionTypes();

		Map<Identifier, Source> byTable = new HashMap<>();
		for (Source source : Source.values()) {
			for (String table : source.tables) {
				byTable.put(Identifier.withDefaultNamespace(table), source);
			}
		}
		LootTableEvents.MODIFY.register((key, table, lootSource, registries) -> {
			// Only change the vanilla tables, never a data pack's replacement of them.
			Source source = lootSource.isBuiltin() ? byTable.get(key.identifier()) : null;
			if (source == null) {
				return;
			}
			table.pool(LootPool.lootPool()
				.setRolls(ContextIntProviders.exactly(1))
				.when(() -> new TadaChanceCondition(source))
				.add(LootItem.lootTableItem(TadaItems.OBSIDIAN_CHESTPLATE)
					.apply(() -> new TadaArmorFunction(Optional.empty())))
				.build());
		});
	}
}
