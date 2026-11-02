package br.zona;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

/** Adds a small chance of apocalypse supplies to vanilla structure chests. */
final class CrateLoot extends LootModifier {
    static final MapCodec<CrateLoot> CODEC=RecordCodecBuilder.mapCodec(instance->codecStart(instance).apply(instance,CrateLoot::new));
    CrateLoot(LootItemCondition[] conditions){super(conditions);}
    @Override public MapCodec<? extends IGlobalLootModifier> codec(){return CODEC;}
    @Override protected ObjectArrayList<ItemStack> doApply(LootTable table,ObjectArrayList<ItemStack> loot,LootContext context){
        var id=context.getQueriedLootTableId();
        if(id==null || !id.getNamespace().equals("minecraft") || !id.getPath().startsWith("chests/"))return loot;
        var random=context.getRandom();
        if(random.nextFloat()<.16f)loot.add(new ItemStack(Zona.SCRAP.get(),1+random.nextInt(2)));
        if(random.nextFloat()<.08f)loot.add(new ItemStack(Zona.FOOD_CAN.get()));
        if(random.nextFloat()<.08f)loot.add(new ItemStack(Zona.CLEAN_WATER.get()));
        return loot;
    }
}
