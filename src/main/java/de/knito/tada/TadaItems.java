package de.knito.tada;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvents;

public final class TadaItems {
	public static final ResourceKey<EquipmentAsset> OBSIDIAN_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, id("obsidian"));
	public static final TagKey<Item> REPAIRS_OBSIDIAN_ARMOR =
		TagKey.create(Registries.ITEM, id("repairs_obsidian_armor"));

	// Above netherite (37 / 3-6-8-3 / 3.0 / 0.1). Durability 73 gives helmet 803, boots 949, leggings 1095, chestplate 1168.
	public static final ArmorMaterial OBSIDIAN = new ArmorMaterial(
		73,
		ArmorMaterials.makeDefense(5, 8, 10, 5, 22),
		18,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		4.0F,
		0.15F,
		REPAIRS_OBSIDIAN_ARMOR,
		OBSIDIAN_ASSET
	);

	public static Item OBSIDIAN_HELMET;
	public static Item OBSIDIAN_CHESTPLATE;
	public static Item OBSIDIAN_LEGGINGS;
	public static Item OBSIDIAN_BOOTS;
	public static List<Item> ALL = List.of();

	private TadaItems() {}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Tada.MOD_ID, path);
	}

	private static Item armor(String name, ArmorType type) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(name));
		Item.Properties props = new Item.Properties().humanoidArmor(OBSIDIAN, type).fireResistant().setId(key);
		return Registry.register(BuiltInRegistries.ITEM, key, new Item(props));
	}

	public static void register() {
		OBSIDIAN_HELMET = armor("obsidian_helmet", ArmorType.HELMET);
		OBSIDIAN_CHESTPLATE = armor("obsidian_chestplate", ArmorType.CHESTPLATE);
		OBSIDIAN_LEGGINGS = armor("obsidian_leggings", ArmorType.LEGGINGS);
		OBSIDIAN_BOOTS = armor("obsidian_boots", ArmorType.BOOTS);
		ALL = List.of(OBSIDIAN_HELMET, OBSIDIAN_CHESTPLATE, OBSIDIAN_LEGGINGS, OBSIDIAN_BOOTS);
	}
}
