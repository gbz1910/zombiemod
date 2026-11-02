# Validação de Zona Morta 0.1.1

Validação feita com **Minecraft Java 26.3**, **Forge 66.1.0** e **Java 25**. O workflow compilou o JAR e o instalou na pasta `mods` de um cliente Forge real com servidor integrado. O mesmo pacote foi usado nas execuções de servidor dedicado e multiplayer.

## Arquivo validado

- JAR: `zona-0.1.1.jar`
- Tamanho: **386.434 bytes**
- SHA-256: `697B8232E88FA6CE537EE8E22AEC1CAF488A4928480FEE39052AE99D67389C9B`
- Commit de código de jogo: `65d0b72c3d292cc9ef0274c07513317110e6b3ba`
- Execução integrada final: [Playtest 41503871361](https://github.com/gbz1910/zombiemod/actions/runs/41503871361) (concluída com sucesso; commit final `d433dad3c903486c179facec7c19eb239909c9f9`, com ajustes apenas na sequência de QA).

## Implementado

O JAR contém pistola e M4 com munição e recarga, três infectados, machete com golpe em arco e rastro visual, variações de cores nas roupas, lanterna, comida enlatada, água limpa, bandagem, kit médico e componentes fabricáveis. Também inclui tiros na cabeça calculados pelo servidor, bloqueio de dano duplicado, evolução diária, proteção contra sol, regras de spawn, limites configuráveis e configuração que bloqueia spawn de mobs que não sejam os três infectados.

Os arquivos de modelo editáveis do Blender e os recursos de compilação estão no pacote-fonte.

## Testado no jogo

| Execução | Verificações aprovadas |
|---|---|
| [Build 41497119401](https://github.com/gbz1910/zombiemod/actions/runs/41497119401) | Compilação do JAR. |
| [Cliente e mundo integrado 41503871361](https://github.com/gbz1910/zombiemod/actions/runs/41503871361) | Inicialização no Forge; registro dos itens; receitas verificadas; pistola, recarga interrompida, carregador de 12, disparo único por pressão e parede bloqueando tiro; M4 com carregador de 30 e rajada; machete acertando três alvos uma vez por golpe; lanterna, comida e água; regras de spawn dos três tipos em horários de manhã, tarde e noite; evolução após dois dias e sobrevivência ao sol. |
| [Servidor dedicado e persistência 41497119471](https://github.com/gbz1910/zombiemod/actions/runs/41497119471) | Servidor Forge dedicado abriu o mundo, salvou, reiniciou e carregou os três tipos de infectado persistidos. |
| [Dois clientes e servidor 41497119299](https://github.com/gbz1910/zombiemod/actions/runs/41497119299) | Sincronização do disparo e da munição observada por outro cliente; servidor aplicou dano e débito de munição uma vez; preparação e impacto do golpe do tanque sincronizados. Teste curto com 24 infectados ativos mediu média de 16,89 ms por tick nessa máquina. |

No teste integrado, o tiro da pistola no tronco causou **5,904** de dano e o tiro na cabeça **11,808**. A M4 repetiu essa proporção nos tipos comum, corredor e tanque. Os testes também verificaram as receitas de componentes e armas, a interrupção e conclusão de bandagem e kit médico, a devolução da garrafa vazia após beber, e a machete atingindo três alvos próximos sem repetir acerto no mesmo alvo.

## Limites da validação

As regras de spawn foram exercitadas nos três períodos do dia, mas a taxa aleatória de aparecimento não foi contada durante uma sessão longa nem comparada estatisticamente à primeira versão. Limites por área/jogador e frequência de hordas foram configurados, mas não receberam um teste de carga prolongado. Os headshots foram validados em poses de teste; cada fase de caminhada, corrida, ataque, reação e morte não foi verificada em um caso separado. O teste de 24 infectados é uma amostra curta, não uma garantia de desempenho em outros computadores. Compatibilidade com outros mods e shaders não foi medida.

As verificações automatizadas confirmam comportamento e dano no servidor. A inspeção visual do cliente confirmou que os recursos do mod carregam; a captura de todas as animações em cada configuração gráfica não faz parte desta validação.
