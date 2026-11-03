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
| Conduit Power I | Tide, Coast |
| Slow Falling | Vex, Flow, Raiser |
| Night Vision | Eye, Wayfinder, Ward |
| Strength | Bolt, Silence |
No ability yet: Sentry, Dune, Wild, Spire, Shaper (reserved, open for later).
Knockback Resistance removed (attribute, not a potion effect).

## 4. Mechanics
- Every 5 s a wear test runs per player. For each worn trimmed piece, the matching effect is applied for 7 s, so there is no gap.
- Effect particles hidden, effect icon and timer visible (vanilla behaviour).
- Strength: +2 levels per Strength-trimmed piece. 1 piece = Strength II, 4 pieces = Strength VIII. Configurable cap and step.
- Same ability from several pieces does not stack, except Strength as above.
- Four pieces with four different trims give four abilities.

## 5. Gold and piglins
- A piece with a gold trim material counts as golden armor for piglins, which removes the need for Distracting Trims.
- Plan: mixin into the piglin gold-armor check. To verify in 26.3 source (the check may be tag-based).

## 6. Open items
- Check the 26.3 template list for new templates.
- Hang glider mod compatibility: Slow Falling test needed.
- Mod id, name and license to be set when the repo exists.

## 7. Steps
1. This spec (done once approved).
2. Fabric project, armor, recipes, textures, dyeing.
3. Trim abilities and gold/piglin behaviour.
4. Test, build, publish to CurseForge via GitHub Actions.
