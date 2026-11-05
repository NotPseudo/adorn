package juuxel.adorn.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class BoughtFromTradingStationCriterion extends SimpleCriterionTrigger<BoughtFromTradingStationCriterion.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack soldItem) {
        trigger(player, conditions -> conditions.matches(soldItem));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, Optional<ItemPredicate> soldItem) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootItemCondition.CODEC.optionalFieldOf("player")
                .forGetter(Conditions::player),
            ItemPredicate.CODEC.optionalFieldOf("item")
                .forGetter(Conditions::soldItem)
        ).apply(instance, Conditions::new));

        public boolean matches(ItemStack stack) {
            return soldItem.map(predicate -> predicate.test(stack)).orElse(true);
        }
    }
}
