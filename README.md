# TADA - Trimmed Abilities Dyeable Armor

Fabric mod for Minecraft 26.3 (Java 25). See [docs/SPEC.md](docs/SPEC.md).

## Status
Step 2 (base): obsidian armor (4 pieces), crafting recipes, dyeing, trimming, placeholder textures.
Trim abilities and gold/piglin behaviour follow in step 3.

## Hints, loot, trader and settings
- **Hints:** smithing templates and trimmed armor show "Ability: X" in the tooltip. A piece only gets the hint if it really gives the ability (pattern has one, and with "Only TADA armor" the piece must be TADA armor). It is calculated from the trim, so it also works on pieces you already own. On a multiplayer server the client follows the server's "Only TADA armor" setting.
- **Loot:** finished TADA pieces (enchanted, usually trimmed, often dyed) can appear in end city, nether fortress, bastion treasure, ancient city, woodland mansion, buried treasure and shipwreck treasure chests, in trial chamber vaults (normal and ominous) and as fishing treasure. All chances are low by default and can be set per source.
- **Wandering trader:** can offer a random ability armor trim template (24 emeralds + 2 diamonds) and a finished TADA piece (64 emeralds + 8 diamonds). They are part of his rare trade pool, so he offers them only on some visits. The prices are in `data/tada/villager_trade/wandering_trader/*.json` and can be changed with a data pack.
- **Mod Menu:** with [Mod Menu](https://modrinth.com/mod/modmenu) installed, all settings can be changed in game (Mods > TADA > settings icon). Every setting has a tooltip. On a dedicated server the "server-side" settings come from the server's `config/tada.json`.

## Build
`./gradlew build` with a Java 25 JDK. The jar appears in `build/libs/`.

## Placeholder textures
`tools/gen_placeholder_assets.py` regenerates them from the vanilla leather armor layout (needs Pillow and the extracted vanilla leather textures; paths at the top of the script are machine-specific).

## Authors and license

Authors: Claude AI, Konitor. Licensed under the MIT License, see [LICENSE](LICENSE).
