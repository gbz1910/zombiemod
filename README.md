# Zona Morta 0.1.1

Mod de combate e sobrevivência para Minecraft Java **26.3**, Forge **66.1.0** e Java 25.

A atualização inclui pistola semiautomática de 9 mm, M4 automática de 5,56 mm, machete com golpe em arco, três infectados, itens de sobrevivência, componentes de armas, headshots e evolução diária. O servidor controla tiros, melee, munição e dano.

[Baixar o JAR testado](https://github.com/gbz1910/zombiemod/actions/runs/41503871361): na seção **Artifacts**, baixe `playtest-results` e extraia `build/libs/zona-0.1.1.jar`.

O [guia de jogo](docs/GUIA.md) explica instalação, controles e receitas. O [registro de validação](docs/VALIDACAO.md) separa o que foi implementado dos cenários testados.

## Arquivos do projeto

- `src/main/java/br/zona` contém a implementação e as animações exportadas.
- `art/runtime.zip` contém os recursos usados na compilação; a árvore expandida também está em `src/main/resources`.
- `art/blender` contém os arquivos editáveis dos itens, armas e infectados.
- `tools` contém os scripts de geração; a [origem dos recursos](docs/ORIGEM.md) está documentada.
- `qa` e `.github/workflows` contêm os testes do cliente, do servidor dedicado e do multiplayer.
