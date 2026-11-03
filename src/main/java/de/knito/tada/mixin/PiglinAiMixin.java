package de.knito.tada.mixin;

import de.knito.tada.TadaConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla checks the item tag piglin_safe_armor, which cannot see trims.
 * Add: any worn piece with a gold trim material also counts as golden armor.
 */
@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
	@Inject(method = "isWearingSafeArmor", at = @At("HEAD"), cancellable = true)
	private static void tada$goldTrimIsSafe(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!TadaConfig.get().goldTrimCountsForPiglins) {
			return;
		}
		for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
			ArmorTrim trim = entity.getItemBySlot(slot).get(DataComponents.TRIM);
			if (trim != null && trim.material().is(TrimMaterials.GOLD)) {
				cir.setReturnValue(true);
				return;
			}
		}
	}
}
