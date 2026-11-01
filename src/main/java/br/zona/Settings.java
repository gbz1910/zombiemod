package br.zona;
import net.minecraftforge.common.ForgeConfigSpec;
final class Settings {
    static final ForgeConfigSpec.Builder B=new ForgeConfigSpec.Builder();
    static ForgeConfigSpec.IntValue number(String key,int value,int max){return B.defineInRange(key,value,1,max);}
    static final ForgeConfigSpec.IntValue P_CAP=number("pistola.capacidade",12,200), R_CAP=number("m4.capacidade",30,200),
        P_DELAY=number("pistola.intervalo_ticks",5,60), R_DELAY=number("m4.intervalo_ticks",2,60),
        P_HIT=number("pistola.dano",7,100), R_HIT=number("m4.dano",8,100),
        P_RELOAD=number("pistola.recarga_ticks",28,160), R_RELOAD=number("m4.recarga_ticks",44,160),
        P_NOISE=number("pistola.raio_de_ruido",20,96), R_NOISE=number("m4.raio_de_ruido",38,96),
        LIMIT=number("ruido.maximo_atraidos",12,64);
    static final ForgeConfigSpec SPEC=B.build();
}

