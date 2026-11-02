# Zona Morta 0.1.0

Mod de sobrevivência para **Minecraft Java 26.3 e Forge 66.1.0**.

## Instalação

1. Instale o Forge 66.1.0 para Minecraft Java 26.3 e execute esse perfil uma vez.
2. Copie `zona-0.1.0.jar` para a pasta `mods` da instância. No launcher padrão do Windows, ela fica em `%APPDATA%\.minecraft\mods`.
3. Inicie o perfil Forge. Em multiplayer, instale o mesmo arquivo no servidor e em cada cliente.

O mod usa Java 25 e traz os próprios modelos, animações, texturas e áudios. A aba criativa **Zona Morta • Suprimentos** contém as armas, munições, itens médicos e os três ovos de invocação.

## Controles

| Ação | Controle padrão |
|---|---|
| Atirar | Botão esquerdo. Clique uma vez por tiro da pistola ou segure para a rajada da M4. |
| Mirar | Segure o botão direito enquanto empunha a arma. |
| Recarregar | Pressione **R** com a munição correta no inventário. |
| Tratar ferimento | Segure o botão direito com a bandagem ou o kit até terminar. |

As armas começam vazias. A pistola usa 12 cartuchos de 9 mm e a M4 usa 30 de 5,56 mm. A contagem aparece acima da barra de acesso rápido. A recarga desconta apenas o que foi transferido para a arma. Trocar de slot cancela uma recarga em andamento. A carga já inserida fica salva na própria arma. Morrer ou sair do servidor cancela a recarga pendente. A arma e as munições seguem as regras normais de queda de inventário do mundo. A cura exige vida abaixo do máximo. Receber dano interrompe o tratamento. Os disparos usam um traço luminoso de bala até o impacto, para tornar clara a direção de cada tiro.

## Sobrevivência

Use a bancada de trabalho. Cada grupo entre crases representa uma linha da grade, da esquerda para a direita. O ponto representa uma casa vazia.

| Resultado | Linhas | Ingredientes |
|---|---|---|
| Pistola | `III` `.LR` `..L` | I = lingote de ferro, L = couro, R = pó de redstone |
| M4 | `IBI` `RPI` `L.L` | I = lingote de ferro, B = bloco de ferro, R = pó de redstone, P = pistão, L = couro |
| 12 cartuchos de 9 mm | `CN` `PG` | C = lingote de cobre, N = pepita de ferro, P = papel, G = pólvora |
| 16 cartuchos de 5,56 mm | `IN` `CG` `PG` | I = lingote de ferro, N = pepita de ferro, C = lingote de cobre, G = pólvora, P = papel |
| 2 bandagens | `SS` `WW` | S = linha, W = lã branca |
| Kit de primeiros socorros | `ILI` `BGB` `ILI` | I = pepita de ferro, L = couro, B = bandagem, G = maçã dourada |

O comum acompanha a horda. O corredor é rápido, com menos vida e postura reta. O tanque tem braços pesados e prepara o golpe antes do impacto. Cada tipo escolhe aleatoriamente uma camisa preta, verde ou vermelha quando nasce. Disparos atraem infectados próximos. A distribuição natural é de 70% comuns, 25% corredores e 5% tanques. As listas de spawn naturais de todas as categorias são substituídas e qualquer mob vanilla ou de outro mod é recusado ao entrar no mundo, portanto somente os três infectados aparecem.

O Forge gera `config/zona-common.toml` após a primeira inicialização. Com o jogo fechado, ajuste a capacidade, intervalos, dano, recarga, ruído, dispersão, pesos de aparição e atributos. Em multiplayer, use a mesma configuração nos clientes e no servidor.

