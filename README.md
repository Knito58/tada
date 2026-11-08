# TADA - Trimmed Abilities Dyeable Armor

Fabric mod for Minecraft 26.3 (Java 25). See [docs/SPEC.md](docs/SPEC.md).

## Status
Step 2 (base): Tadarite armor (4 pieces, crafted from obsidian), crafting recipes, dyeing, trimming, placeholder textures.
Trim abilities and gold/piglin behaviour follow in step 3.

## Loot and wandering trader
- **Loot:** finished TADA pieces (enchanted, usually trimmed, often dyed) can appear in end city, nether fortress, bastion treasure, ancient city, woodland mansion, buried treasure and shipwreck treasure chests, in common overworld chests (dungeons, mineshafts, temples, strongholds, outposts, ruined portals, igloos, big underwater ruins, armorer/weaponsmith/toolsmith, other bastion chests), in trial chamber vaults (normal and ominous) and as fishing treasure. All chances are low by default and set per source in `config/tada.json`.
- **Wandering trader:** can offer a random ability armor trim template (24 emeralds + 2 diamonds) and a finished TADA piece (64 emeralds + 8 diamonds), each rolled once per trader (30% for the piece, 60% for the template by default, set in `config/tada.json`). Prices are in `data/tada/villager_trade/wandering_trader/*.json` and can be changed with a data pack.

## Build
`./gradlew build` with a Java 25 JDK. The jar appears in `build/libs/`.

## Placeholder textures
`tools/gen_placeholder_assets.py` regenerates them from the vanilla leather armor layout (needs Pillow and the extracted vanilla leather textures; paths at the top of the script are machine-specific).

## Authors and license

Authors: Claude AI, Konitor. Licensed under the MIT License, see [LICENSE](LICENSE).
