# TADA - Trimmed Abilities Dyeable Armor

Fabric mod for Minecraft 26.3 (Java 25). See [docs/SPEC.md](docs/SPEC.md).

## Status
Step 2 (base): obsidian armor (4 pieces), crafting recipes, dyeing, trimming, placeholder textures.
Trim abilities and gold/piglin behaviour follow in step 3.

## Build
`./gradlew build` with a Java 25 JDK. The jar appears in `build/libs/`.

## Placeholder textures
`tools/gen_placeholder_assets.py` regenerates them from the vanilla leather armor layout (needs Pillow and the extracted vanilla leather textures; paths at the top of the script are machine-specific).

## Authors and license

Authors: Konitor, Claude AI. Licensed under the MIT License, see [LICENSE](LICENSE).
