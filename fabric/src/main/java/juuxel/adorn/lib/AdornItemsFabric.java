package juuxel.adorn.lib;

import juuxel.adorn.component.AdornComponentTypes;
import juuxel.adorn.item.FuelData;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public final class AdornItemsFabric {
    public static void init() {

        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> true, (builder, lookup, item) -> {
                for (var fuelData : FuelData.FUEL_DATA) {
                    boolean matches = switch (fuelData) {
                        case FuelData.ForItem(var registered, int burnTime) ->
                            registered.get().asItem() == item;

                        case FuelData.ForTag(var tag, int burnTime) ->
                            lookup.lookupOrThrow(Registries.ITEM)
                                .getOrThrow(tag)
                                .stream()
                                .anyMatch(holder -> holder.value() == item);
                    };

                    if (matches) {
                        builder.set(
                            DataComponents.COOKING_FUEL,
                            new CookingFuel(
                                new ResolvableInt.Constant(fuelData.burnTime()),
                                ResolvableFloat.fromKey(
                                    ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)
                            )
                        );
                    }
                }
            });
        });

        for (var component : AdornComponentTypes.getTooltipComponents()) {
            ItemComponentTooltipProviderRegistry.addFirst(component.get());
        }
    }
}
