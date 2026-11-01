# Zona Morta

Mod de combate e sobrevivência para Minecraft Java **26.3** com **Forge 66.1.0** e Java 25.

A versão 0.1.0 inclui pistola semiautomática de 9 mm, M4 automática de 5,56 mm, três infectados, bandagens e kits de primeiros socorros. O servidor controla o dano, o carregador de cada arma e o consumo das munições.

[Baixar o JAR de Zona Morta 0.1.0](https://github.com/gbz1910/zombiemod/raw/refs/heads/main/dist/zona-0.1.0.jar)

O [guia de jogo](docs/GUIA.md) explica a instalação, os controles e as seis receitas. O [registro de validação](docs/VALIDACAO.md) identifica o arquivo testado e descreve a cobertura dos testes.

## Arquivos do projeto

- `src/main/java/br/zona` contém a implementação do mod e as animações exportadas dos esqueletos de animação.
- `art/runtime.zip` contém os recursos utilizados na compilação. O pacote de fontes da entrega também contém a árvore expandida em `src/main/resources`.
- `art/blender` contém os arquivos editáveis das armas, dos itens e dos três infectados.
- `tools` contém os scripts de geração. A [origem dos recursos](docs/ORIGEM.md) está documentada.
- `qa` e `.github/workflows` contêm os testes do cliente, do servidor dedicado e do multiplayer.


