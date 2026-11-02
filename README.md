# Zona Morta 0.1.1

Mod de combate e sobrevivência para Minecraft Java **26.3** com **Forge 66.1.0** e Java 25.

A atualização inclui pistola semiautomática de 9 mm, M4 automática de 5,56 mm, machete com golpe em arco, três infectados, itens de sobrevivência, componentes de armas, headshots e evolução diária. O servidor controla tiros, melee, munição e dano.

[Baixar o JAR de Zona Morta 0.1.1](https://github.com/gbz1910/zombiemod/raw/refs/heads/main/dist/zona-0.1.1.jar)

O [guia de jogo](docs/GUIA.md) explica a instalação, os controles e as receitas. O [registro de validação](docs/VALIDACAO.md) identifica o arquivo testado e separa o que foi implementado do que passou nos testes.

## Arquivos do projeto

- `src/main/java/br/zona` contém a implementação do mod e as animações exportadas dos esqueletos de animação.
- `art/runtime.zip` contém os recursos utilizados na compilação. O pacote de fontes da entrega também contém a árvore expandida em `src/main/resources`.
- `art/blender` contém os arquivos editáveis das armas, dos itens e dos três infectados.
- `tools` contém os scripts de geração. A [origem dos recursos](docs/ORIGEM.md) está documentada.
- `qa` e `.github/workflows` contêm os testes do cliente, do servidor dedicado e do multiplayer.


