package br.zona;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.*;
final class Audio {
    static final DeferredRegister<SoundEvent> SOUNDS=DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,Zona.ID);
    static RegistryObject<SoundEvent> sound(String key){return SOUNDS.register(key,()->SoundEvent.createVariableRangeEvent(Zona.id(key)));}
    static final RegistryObject<SoundEvent> PISTOL=sound("pistol_fire"),RIFLE=sound("rifle_fire"),OUT=sound("mag_out"),IN=sound("mag_in"),DRY=sound("dry"),
        COMMON=sound("common_call"),RUNNER=sound("runner_call"),TANK=sound("tank_call");
}

