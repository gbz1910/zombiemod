# Zona Morta 0.1.1

Mod para **Minecraft Java 26.3**, **Forge 66.1.0** e Java 25.

## Instalação

1. Inicie uma vez o perfil Minecraft 26.3 com Forge 66.1.0.
2. Copie `zona-0.1.1.jar` para `%APPDATA%\.minecraft\mods` (ou para a pasta `mods` da instância usada pelo launcher).
3. Inicie o perfil Forge. Em multiplayer, instale o mesmo JAR no servidor e nos clientes.

## Controles

| Ação | Controle |
|---|---|
| Atirar | Botão esquerdo; segurar dispara rajadas com a M4. |
| Mirar | Segure o botão direito com uma arma de fogo. |
| Recarregar | **R** com a munição correspondente no inventário. |
| Golpear com a machete | Botão esquerdo; o arco atinge infectados próximos uma vez por golpe. |
| Usar itens médicos/comida/água | Segure o botão direito até o uso terminar. |
| Lanterna | Segure na mão principal ou secundária para receber visão noturna. |

A pistola usa 12 cartuchos de 9 mm e a M4 usa 30 de 5,56 mm. Os tiros têm rastro visível; tiros na cabeça causam o dobro do dano e mostram um aviso breve. Zumbis recebem variações de camisa, não queimam ao sol e ficam mais fortes conforme passam os dias. O peso padrão mantém o tanque incomum; os zumbis aparecem em pequenos grupos e os limites por área e jogador podem ser ajustados em `config/zona-common.toml`.

## Receitas

As linhas mostram a grade da bancada; `.` indica espaço vazio.

| Item | Grade | Ingredientes |
|---|---|---|
| 3 sucatas metálicas | `III` | I = lingote de ferro |
| Mola de arma | `I` / `I` / `I` | I = lingote de ferro |
| Gatilho | shapeless | Pepita de ferro + mola de arma |
| Cano de arma | `SSS` | S = sucata metálica |
| Corpo da pistola | `SSS` / `I.I` / `SSS` | S = sucata; I = lingote de ferro |
| Receptor da M4 | `SSS` / `ISI` / `SSS` | S = sucata; I = lingote de ferro |
| Pistola | `BCT` / `.SI` / `.I.` | B = corpo; C = cano; T = gatilho; S = mola; I = lingote de ferro |
| M4 | `RCT` / `.S.` / `III` | R = receptor; C = cano; T = gatilho; S = mola; I = lingote de ferro |
| Machete | `..I` / `.SI` / `G..` | I = lingote de ferro; S = sucata; G = graveto |
| Lanterna | `.I.` / `IGI` / `.T.` | I = pepita de ferro; G = painel de vidro; T = tocha |
| Garrafa de água limpa | shapeless | Garrafa de vidro + balde de água; o balde vazio retorna |
| 12 cartuchos de 9 mm | `CN` / `PG` | C = cobre; N = pepita de ferro; P = papel; G = pólvora |
| 16 cartuchos de 5,56 mm | `IN` / `CG` / `PG` | I = ferro; N = pepita de ferro; C = cobre; P = papel; G = pólvora |
| 2 bandagens | `SS` / `WW` | S = linha; W = lã branca |
| Kit médico | `ILI` / `BGB` / `ILI` | I = pepita de ferro; L = couro; B = bandagem; G = maçã dourada |

Sucata, lata de comida e garrafa de água também podem aparecer raramente em baús; sucata e consumíveis podem cair de zumbis. A comida e a água recuperam pouca fome; a água devolve a garrafa de vidro vazia.

## Configuração

O mod cria `config/zona-common.toml`. É possível alterar pesos de spawn, tamanho de grupos, limites por área e jogador, dia máximo de evolução, vida/velocidade/dano dos três tipos, dano/intervalo da machete, valores das armas e depuração de hitboxes. A depuração de áreas de acerto vem desligada.

O mundo é configurado para permitir somente os três infectados como mobs. Isso bloqueia animais e mobs de outros mods também.
