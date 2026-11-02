# Testes de Zona Morta 0.1.0

Os testes usam Minecraft Java 26.3, Forge 66.1.0 e Java 25. O projeto de teste inicia o cliente com o arquivo instalável em `mods`. O servidor dedicado usa a instalação oficial do Forge.

## Cobertura

| Sistema | Comportamento exercitado |
|---|---|
| Registro | Carregar e equipar pistola e M4, obter bandagem, kit, munição de 9 mm e munição de 5,56 mm, invocar os três infectados. |
| Pistola | Tentar disparar vazia, interromper a recarga trocando de slot, completar a recarga de 12, conferir o estoque, mirar, segurar o gatilho e produzir um único disparo. |
| Colisão e dano | Disparar contra um infectado com uma parede no caminho, conferir a vida do alvo, remover a parede e repetir o disparo com o raycast e traçador atualizados. |
| M4 | Recarregar 30 cartuchos de 5,56 mm e produzir vários disparos enquanto o gatilho permanece pressionado. |
| Tratamento | Interromper bandagem e kit ao sofrer dano, conferir que o item permanece no inventário e completar cada tratamento para verificar cura e consumo. |
| Mundo e servidor | Iniciar servidor dedicado, invocar comum, corredor e tanque, salvar o mundo, desligar e reiniciar, localizar as três entidades persistentes. O cliente também tenta inserir um zumbi vanilla e confirma que ele é recusado. |
| Apresentação | Carregar os modelos e as nove texturas de variante no cliente real, incluindo o rosto em camadas frontais sem sobreposição e a nova postura do corredor. |

## Como interpretar os resultados

A validação é automatizada. A medição de desempenho representa a máquina de testes do GitHub Actions. Latência de internet, pacotes de shaders, outros mods e desempenho em computadores de jogadores dependem do ambiente. O balanceamento de cadência, recuo e raridade permanece ajustável.

## Identificação do arquivo

- Nome instalável — `zona-0.1.0.jar`
- Tamanho — 353.837 bytes
- SHA-256 — `34fb4f184237ae3931efc4496ff7e9874d711e57b3c852e2f515cc723aa95cbd`
- Commit que compilou o mod — `013854eba13c88845a958aeb0ef38ce63c73bba8`

A execução do cliente confirmou o gatilho da pistola, as rajadas da M4, as duas capacidades de carregador, o consumo de munição, a colisão protegida por parede e o bloqueio do zumbi vanilla. A interrupção e a conclusão das duas curas passaram. O servidor de produção carregou o JAR de mesmo SHA-256 e preservou os três infectados após reiniciar. As capturas e a lista de verificações do cliente acompanham o pacote de fontes em `docs/evidencias`.

[Compilação do JAR](https://github.com/gbz1910/zombiemod/actions/runs/41374210401) · [Cliente e mundo integrado](https://github.com/gbz1910/zombiemod/actions/runs/41487733562) · [Servidor dedicado e persistência](https://github.com/gbz1910/zombiemod/actions/runs/41374210166)

## Multiplayer e grupo misto

Dois clientes reais se conectaram ao mesmo servidor dedicado. O segundo jogador observou o carregador da pistola passar de 12 para 11 e recebeu o som do disparo. O servidor verificou uma única aplicação de dano e um único débito de munição. Durante o golpe do tanque, a vida do jogador permaneceu estável na preparação e diminuiu após o impacto.

O teste adicionou 24 infectados com IA ativa em um grupo misto e registrou uma média de **16,89 ms por tick** no servidor dessa execução. É uma amostra curta de carga. Desempenho em sessões longas, latência de internet e compatibilidade com outros mods permanecem sem medição. A proporção de aparição é definida por pesos, sem contagem estatística de longo prazo. Morte e reconexão durante uma recarga não tiveram um caso automatizado específico.

[Teste com dois clientes, golpe do tanque e grupo misto](https://github.com/gbz1910/zombiemod/actions/runs/41374210450)
