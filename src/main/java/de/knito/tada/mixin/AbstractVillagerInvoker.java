package de.knito.tada.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Opens the protected trade-set method of the villager base class. */
@Mixin(AbstractVillager.class)
public interface AbstractVillagerInvoker {
	@Invoker("addOffersFromTradeSet")
	void tada$addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> key);
}
