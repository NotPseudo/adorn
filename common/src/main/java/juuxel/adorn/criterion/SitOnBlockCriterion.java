package juuxel.adorn.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class SitOnBlockCriterion extends SimpleCriterionTrigger<SitOnBlockCriterion.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        trigger(player, conditions -> conditions.matches(player, pos));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, BlockPredicate block) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootItemCondition.CODEC.optionalFieldOf("player")
                .forGetter(Conditions::player),
            BlockPredicate.CODEC.fieldOf("block").forGetter(Conditions::block)
        ).apply(instance, Conditions::new));

        public boolean matches(ServerPlayer player, BlockPos pos) {
            return block.matches(player.level(), pos);
        }
    }
}
