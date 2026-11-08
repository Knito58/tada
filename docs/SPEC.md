# TADA - Trimmed Abilities Dyeable Armor: Spec v0.1

Target: Minecraft 26.3, Fabric Loader, Java 25. Publishing: CurseForge only (CURSEFORGE_TOKEN as GitHub Actions secret).
Replaces: Obsidian Armor, Everlasting Abilities, Distracting Trims.

## 1. Armor
- Four pieces (helmet, chestplate, leggings, boots) made of obsidian, stats above netherite.
- Look: dark grey base with amethyst and gold decorations on each piece. Textures drawn by Claude as a first draft.
- Dyeable via vanilla dyed_color component. If this proves impossible, the title becomes "Trimmed Abilities Distracting Armor".
- Trimmable with all vanilla smithing templates. Amplifier and levels are configurable.

## 2. Recipes (O = obsidian block, A = amethyst shard, G = gold ingot)
Helmet: OAO / G_G
Chestplate: O_O / OAO / GOG
Leggings: OAO / O_O / G_G
Boots: O_O / G_A

## 3. Trim -> ability (each template gives exactly one ability)
| Ability | Templates |
|---|---|
| Fire Resistance | Rib, Snout, Host |
| Conduit Power I | Tide, Coast, Sentry |
| Slow Falling | Vex, Flow, Raiser |
| Night Vision | Eye, Wayfinder, Ward |
| Jump Boost II | Spire, Wild |
| Strength | Bolt, Silence |
No ability yet: Dune, Shaper (reserved, open for later).
Knockback Resistance removed (attribute, not a potion effect).

## 4. Mechanics
- Every 5 s a wear test runs per player. For each worn trimmed piece, the matching effect is applied for 16 s (320 ticks). Vanilla blinks the HUD icon and flickers Night Vision while 200 ticks or less remain, so the remaining time must stay above 200 between refreshes: duration at least interval + 201. Side effect: an effect lingers up to 16 s after the piece is taken off.
- Effect particles hidden, effect icon and timer visible (vanilla behaviour).
- Strength: +2 levels per Strength-trimmed piece. 1 piece = Strength II, 4 pieces = Strength VIII. Configurable cap and step.
- Same ability from several pieces does not stack, except Strength as above.
- Four pieces with four different trims give four abilities.

## 5. Gold and piglins
- A piece with a gold trim material counts as golden armor for piglins, which removes the need for Distracting Trims.
- Verified in 26.3: `PiglinAi.isWearingSafeArmor` checks only the item tag `piglin_safe_armor` (golden armor), nothing about trims. Implemented as a mixin (`PiglinAiMixin`) that also returns true for any worn piece with a gold trim material. Option `goldTrimCountsForPiglins`.

## 6. Open items
- Checked: 26.3 has the same 18 trim patterns, no new templates.
- Hang glider mod compatibility: Slow Falling test needed.
- Mod id, name and license to be set when the repo exists.

## 7. Config (config/tada.json)
(See section 8 for the loot, trader and hint options.)
checkIntervalTicks 100, effectDurationTicks 320 (raised on load to at least interval + 201), strengthLevelsPerPiece 2, strengthMaxLevel 8, onlyTadaArmor false (any trimmed armor gives abilities), goldTrimCountsForPiglins true.

## 8. Loot and wandering trader (step 5)
- Loot: `LootTableEvents.MODIFY` adds one extra pool to chosen vanilla tables. The pool is guarded by the condition `tada:chance` (reads the config at generation time) and uses the function `tada:random_armor`: random TADA piece, enchanted with 15-35 levels from the random-loot enchantment pool, trimmed with chance 0.6 (random pattern that has an ability, random material), dyed with chance 0.5 (one or two random dyes). Tables: end_city_treasure 12%, bastion_treasure 10%, buried_treasure 8%, nether_bridge 6%, ancient_city 6%, woodland_mansion 6%, trial_chambers/reward 5%, reward_ominous 12%, shipwreck_treasure 4%, fishing treasure 3%, plus one shared `overworld` group at 4% per chest: simple_dungeon, abandoned_mineshaft, desert_pyramid, jungle_temple, stronghold_*, pillager_outpost, ruined_portal, igloo_chest, underwater_ruin_big, village armorer/weaponsmith/toolsmith, bastion_other/bridge/hoglin_stable.
- Trader: in 26.3 trades are data driven. Trades sitting in the vanilla `uncommon` pool (19 entries, 2 picked) showed up only ~10% of the time, so they now live in two own trade sets, `tada:wandering_trader/templates` and `tada:wandering_trader/armor` (amount 1 each), which `WanderingTraderMixin` rolls after vanilla's three sets. The chance per trader comes from the config (`traderTemplateChance` 60%, `traderArmorChance` 30%) through the `tada:chance` merchant predicate; `tada:random_template` picks a template with an ability.

## 9. Steps
1. This spec (done once approved).
2. Fabric project, armor, recipes, textures, dyeing.
3. Trim abilities and gold/piglin behaviour.
4. Test, build, publish to CurseForge via GitHub Actions.
