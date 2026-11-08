package de.knito.tada.mixin;

import de.knito.tada.TadaItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla fills the wandering trader from three trade sets. Our trades used to sit in the "uncommon" pool next to
 * 16 others, so a trader showed one only about once in ten visits. Now the trader additionally rolls two small trade
 * sets of our own (data pack JSON, chance from the config via the merchant predicate).
 */
@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin {
	@Inject(method = "updateTrades", at = @At("RETURN"))
	private void tada$addTadaTrades(ServerLevel level, CallbackInfo ci) {
		AbstractVillager self = (AbstractVillager) (Object) this;
		MerchantOffers offers = self.getOffers();
		AbstractVillagerInvoker invoker = (AbstractVillagerInvoker) self;
		invoker.tada$addOffersFromTradeSet(level, offers, ResourceKey.create(Registries.TRADE_SET, TadaItems.id("wandering_trader/templates")));
		invoker.tada$addOffersFromTradeSet(level, offers, ResourceKey.create(Registries.TRADE_SET, TadaItems.id("wandering_trader/armor")));
	}
}
