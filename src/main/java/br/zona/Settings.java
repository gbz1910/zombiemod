package br.zona;
import net.minecraftforge.common.ForgeConfigSpec;
final class Settings {
    static final ForgeConfigSpec.Builder B=new ForgeConfigSpec.Builder();
    static ForgeConfigSpec.IntValue number(String key,int value,int max){return B.defineInRange(key,value,1,max);}
    static final ForgeConfigSpec.IntValue P_CAP=number("pistola.capacidade",12,200), R_CAP=number("m4.capacidade",30,200),
        P_DELAY=number("pistola.intervalo_ticks",5,60), R_DELAY=number("m4.intervalo_ticks",2,60),
        P_HIT=number("pistola.dano",6,100), R_HIT=number("m4.dano",6,100),
        P_RELOAD=number("pistola.recarga_ticks",28,160), R_RELOAD=number("m4.recarga_ticks",44,160),
        P_NOISE=number("pistola.raio_de_ruido",20,96), R_NOISE=number("m4.raio_de_ruido",38,96),
        LIMIT=number("ruido.maximo_atraidos",12,64);
    // Doubled spawn weights plus small groups yield roughly 2–3× the prior
    // population without making tanks common.
    // New keys preserve the higher default even when a world already has the
    // earlier 21/7/1 and pack-size settings saved in its config.
    static final ForgeConfigSpec.IntValue COMMON_WEIGHT=B.defineInRange("spawns.peso_comum_apocalipse",28,0,200), RUNNER_WEIGHT=B.defineInRange("spawns.peso_corredor_apocalipse",10,0,200), TANK_WEIGHT=B.defineInRange("spawns.peso_tanque_apocalipse",2,0,200),
        HORDE_MAX=number("spawns.maximo_por_horda_apocalipse",2,12), PLAYER_LIMIT=number("spawns.limite_por_jogador_em_48_blocos",36,160);
    static final class Enemy {
        final ForgeConfigSpec.DoubleValue health,speed,damage,knockback;
        Enemy(String name,double hp,double move,double hit,double kb){
            health=B.defineInRange(name+".vida",hp,1.0,500.0); speed=B.defineInRange(name+".velocidade",move,.01,1.0);
            damage=B.defineInRange(name+".dano",hit,0.0,80.0); knockback=B.defineInRange(name+".resistencia_empurrao",kb,0.0,1.0);
        }
    }
    static final Enemy NORMAL=new Enemy("comum",22,.22,4,0), FAST=new Enemy("corredor",14,.34,3,0), HEAVY=new Enemy("tanque",70,.17,12,.85);
    static final ForgeConfigSpec.IntValue LOCAL_LIMIT=number("spawns.limite_em_32_blocos",24,128),
        EVOLUTION_DAY_CAP=number("evolucao.maximo_dias",60,3650), MACHETE_DELAY=number("machete.intervalo_ticks",16,100), MACHETE_DAMAGE=number("machete.dano",12,100);
    static final ForgeConfigSpec.DoubleValue HIP_SPREAD=B.defineInRange("armas.dispersao_sem_mira",.006,0.0,.3),
        AIM_SPREAD=B.defineInRange("armas.dispersao_com_mira",.00075,0.0,.3), BURST_SPREAD=B.defineInRange("m4.dispersao_por_tiro",.0012,0.0,.1),
        RECOIL=B.defineInRange("armas.recuo_base_graus",.6,0.0,6.0);
    static final ForgeConfigSpec.BooleanValue DEBUG_HITBOXES=B.define("depuracao.mostrar_areas_de_acerto",false);
    static final ForgeConfigSpec SPEC=B.build();
}




