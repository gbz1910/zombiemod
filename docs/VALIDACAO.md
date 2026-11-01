# Testes de Zona Morta 0.1.0

Os testes usam Minecraft Java 26.3, Forge 66.1.0 e Java 25. O projeto de teste inicia o cliente com o arquivo instalável em `mods`. O servidor dedicado usa a instalação oficial do Forge.

## Cobertura

| Sistema | Comportamento exercitado |
|---|---|
| Registro | Carregar e equipar pistola e M4, obter bandagem, kit, munição de 9 mm e munição de 5,56 mm, invocar os três infectados. |
| Pistola | Tentar disparar vazia, interromper a recarga trocando de slot, completar a recarga de 12, conferir o estoque, mirar, segurar o gatilho e produzir um único disparo. |
| Colisão e dano | Disparar contra um infectado com uma parede no caminho, conferir a vida do alvo, remover a parede e repetir o disparo. |
| M4 | Recarregar 30 cartuchos de 5,56 mm e produzir vários disparos enquanto o gatilho permanece pressionado. |
| Tratamento | Interromper bandagem e kit ao sofrer dano, conferir que o item permanece no inventário e completar cada tratamento para verificar cura e consumo. |
| Mundo e servidor | Iniciar servidor dedicado, invocar comum, corredor e tanque, salvar o mundo, desligar e reiniciar, localizar as três entidades persistentes. |
| Apresentação | Carregar os modelos e texturas no cliente real, capturar armas em primeira e terceira pessoa. A prova inicial do Blender foi fotografada em duas poses distintas dentro do Minecraft. |

## Como interpretar os resultados

A validação é automatizada. A medição de desempenho representa a máquina de testes do GitHub Actions. Latência de internet, pacotes de shaders, outros mods e desempenho em computadores de jogadores dependem do ambiente. O balanceamento de cadência, recuo e raridade permanece ajustável.

## Identificação do arquivo

- Nome instalável — `zona-0.1.0.jar`
- Tamanho — 207.562 bytes
- SHA-256 — `aac64a5ff697cd5d0ce18fa177466fa281f9e8f7ee95003c7f486b834cd8aef2`
- Commit que compilou o mod — `a37a85048e5afa95f7d3bb1656d9e25c18b21787`

A execução do cliente confirmou o gatilho da pistola, as rajadas da M4, as duas capacidades de carregador e o consumo de munição. A interrupção e a conclusão das duas curas passaram. O servidor de produção carregou o JAR de mesmo SHA-256 e preservou os três infectados após reiniciar. As capturas e a lista de verificações do cliente acompanham o pacote de fontes em `docs/evidencias`.

[Compilação do JAR](https://github.com/gbz1910/zombiemod/actions/runs/41364209079) · [Cliente e mundo integrado](https://github.com/gbz1910/zombiemod/actions/runs/41364209027) · [Servidor dedicado e persistência](https://github.com/gbz1910/zombiemod/actions/runs/41364209048)

## Multiplayer e grupo misto

Dois clientes reais se conectaram ao mesmo servidor dedicado. O segundo jogador observou o carregador da pistola passar de 12 para 11 e recebeu o som do disparo. O servidor verificou uma única aplicação de dano e um único débito de munição. Durante o golpe do tanque, a vida do jogador permaneceu estável na preparação e diminuiu após o impacto.

O teste adicionou 24 infectados com IA ativa em um grupo misto e registrou uma média de **16,89 ms por tick** no servidor dessa execução. É uma amostra curta de carga. Desempenho em sessões longas, latência de internet e compatibilidade com outros mods permanecem sem medição. A proporção de aparição é definida por pesos, sem contagem estatística de longo prazo. Morte e reconexão durante uma recarga não tiveram um caso automatizado específico.

[Teste com dois clientes, golpe do tanque e grupo misto](https://github.com/gbz1910/zombiemod/actions/runs/41364392116)
