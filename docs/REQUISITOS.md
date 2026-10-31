# Requisitos da primeira versão

Configuração solicitada: Minecraft 26.3, Forge 66.1.0. Entrega obrigatória: JAR compilado, acompanhado de validação real no jogo.

Crie a primeira versão jogável de um mod de Minecraft com tema de apocalipse zumbi. Quero um mod funcional, com identidade visual consistente, combate satisfatório e modelos e animações feitos no Blender.

Não entregue apenas um conceito ou modelos separados: o objetivo é ter os conteúdos integrados e funcionando dentro do Minecraft.

## 1. Preparação técnica

Antes de implementar, defina e registre a versão do Minecraft, o mod loader e a solução para modelos e animações. Se houver um projeto existente, respeite sua configuração.

Verifique a conexão disponível com o Blender. Utilize-o de verdade para criar e editar as armas, os zumbis, os itens tridimensionais, os rigs e as animações. Se a conexão não estiver disponível, informe o impedimento sem fingir que executou o trabalho.

Valide primeiro o caminho completo de um modelo simples: criação no Blender, exportação ou conversão, importação e exibição animada no jogo. Não assuma que arquivos `.blend` ou formatos genéricos funcionam diretamente no Minecraft.

Mantenha os arquivos editáveis do Blender e os recursos exportados organizados junto ao projeto.

## 2. Direção visual

Adote um estilo low-poly, blocado e coerente com o Minecraft, com texturas pixeladas bem trabalhadas.

Quero objetos e personagens com personalidade, silhuetas reconhecíveis e detalhes que funcionem na escala do jogo. Use desgaste, sujeira, tecido rasgado e metal marcado com moderação, sem depender de excesso de polígonos ou texturas grandes.

Priorize animações naturais dentro desse estilo, boa leitura durante o combate e desempenho quando houver vários zumbis na tela.

## 3. Armas da primeira versão

### Pistola

Crie uma pistola semiautomática com modelo próprio feito no Blender.

Implemente:
- Um disparo por acionamento do gatilho.
- Carregador de 12 munições e uso de munição de 9 mm.
- Recuo, dispersão e intervalo entre disparos equilibrados.
- Mira, recarga e indicação clara da munição restante.
- Sons de disparo, recarga e tentativa de disparar sem munição.
- Animações de equipar, repouso, disparar e recarregar.
- Movimento do ferrolho e efeitos breves de disparo.

A pistola deve ser confiável e econômica, com boa eficiência contra zumbis comuns.

### Rifle M4

Crie um rifle inspirado em uma M4 padrão, reconhecível e adaptado à estética do Minecraft.

Implemente:
- Disparo automático ao segurar o gatilho.
- Carregador de 30 munições e uso de munição de 5,56 mm.
- Recuo progressivo e dispersão maior durante rajadas longas.
- Mira, recarga e indicação de munição.
- Sons e animações próprios de equipar, repouso, disparar e recarregar.
- Carregador removível visível durante a animação de recarga.

O rifle deve oferecer maior poder de combate, com consumo de munição e ruído maiores.

### Regras comuns às armas

Os valores acima são parâmetros iniciais de balanceamento e devem ser configuráveis.

As armas precisam funcionar em primeira e terceira pessoa, com posicionamento correto nas mãos. Elas não podem disparar durante a recarga, gerar munição ao trocar de slot ou causar dano através de paredes.

A munição deve ser descontada corretamente do inventário. Defina um comportamento consistente para interromper a recarga, trocar de arma, morrer e reconectar.

Faça a validação de disparos, dano e munição no servidor. Sincronize os efeitos visuais e sonoros para os outros jogadores, sem duplicar dano.

## 4. Três tipos de zumbis

Crie modelos, texturas, rigs e animações no Blender para os três tipos. Eles devem ser distinguíveis pela silhueta, postura, movimento e som.

### Zumbi comum

O inimigo mais frequente:
- Velocidade baixa a moderada.
- Vida e dano equilibrados.
- Postura caída, roupas desgastadas e caminhada irregular.
- Animações de repouso, caminhada, ataque, reação a dano e morte.

Sua ameaça principal deve vir da quantidade.

### Zumbi corredor — Runner

Um inimigo mais rápido e agressivo:
- Velocidade alta e vida menor que a do zumbi comum.
- Corpo mais magro e postura inclinada para a frente.
- Corrida própria, com movimentos rápidos e reconhecíveis.
- Ataques com intervalo e alcance justos.
- Animações de repouso, deslocamento, corrida, ataque, reação a dano e morte.

O jogador precisa conseguir perceber sua aproximação por movimento e som.

### Zumbi tanque

Um inimigo grande e resistente:
- Silhueta larga, braços pesados e postura imponente.
- Mais vida, menor velocidade e resistência maior a empurrões.
- Ataque forte, precedido por uma preparação visual clara.
- Janela de recuperação após atacar.
- Animações de repouso, caminhada pesada, preparação de golpe, ataque, reação a dano e morte.

Ele deve ser perigoso sem causar dano antes do momento correto da animação. Ajuste a área de colisão ao tamanho do modelo e considere suas limitações de passagem pelo cenário.

## 5. Comportamento e aparição

Implemente perseguição, navegação e ataques consistentes, respeitando obstáculos e distância de alcance.

Disparos devem atrair zumbis próximos dentro de um raio configurável. Evite buscas custosas a cada quadro e limite a quantidade de criaturas atraídas quando necessário.

Use inicialmente esta distribuição entre os zumbis do mod:
- 70% comuns.
- 25% corredores.
- 5% tanques.

Deixe frequência, limites de aparição e atributos configuráveis. Preserve as regras de iluminação e os mecanismos de proteção contra aparição adotados pelo projeto. Não remova os monstros originais nem transforme toda a geração do mundo sem necessidade para esta versão.

## 6. Itens essenciais

Implemente estes itens na primeira versão:

### Bandagem
- Recupera uma pequena quantidade de vida ao longo de um uso curto.
- Exige que o jogador complete a ação.
- Tem uso interrompido ao sofrer dano.
- Só é consumida quando o tratamento termina.
- Possui aparência de tecido enrolado, textura própria e feedback de uso.

### Kit de primeiros socorros
- Recupera mais vida que a bandagem.
- Leva mais tempo para usar e aparece com menor frequência.
- Tem modelo próprio, com estojo e identificação visual legível.
- Segue as mesmas regras de interrupção e consumo da bandagem.

### Munição de 9 mm
- Usada exclusivamente pela pistola.
- Tem ícone e modelo próprios, com identificação fácil.

### Munição de 5,56 mm
- Usada exclusivamente pela M4.
- Possui aparência claramente diferente da munição de pistola.

Para simplificar a primeira versão, use munição solta no inventário para abastecer os carregadores internos das armas. Não implemente um sistema separado de carregadores físicos nesta etapa.

Dê acabamento aos itens: nomes e descrições em português do Brasil, ícones legíveis, escala correta quando segurados e aparência cuidada quando largados no chão. Crie os modelos tridimensionais no Blender e prepare as representações de inventário necessárias.

Disponibilize todos os conteúdos em uma aba criativa própria para testes e ofereça uma forma documentada de obtê-los no modo sobrevivência, por receitas ou saques compatíveis com a versão escolhida.

## 7. Qualidade e limites de escopo

Mantenha os sistemas de armas, munição, cura e entidades organizados para expansão futura.

Não amplie esta versão com veículos, NPCs, missões, cidades, chefes, sede, infecção ou árvores de habilidades. Primeiro, conclua e valide o combate e a sobrevivência básica.

Use recursos próprios ou com licença compatível, registrando a origem de recursos externos.

## 8. Validação e entrega

Teste dentro do jogo:
- Equipar, mirar, disparar e recarregar as duas armas.
- Ficar sem munição e tentar disparar.
- Interromper recargas e trocar de slot.
- Usar e interromper itens de cura.
- Enfrentar cada zumbi e grupos mistos.
- Conferir colisões, ataques, animações e obstáculos.
- Salvar e carregar o mundo.
- Verificar sincronização em multiplayer e inicialização em servidor dedicado.
- Avaliar desempenho com vários zumbis ativos.

Entregue:
- Código-fonte organizado.
- Arquivo instalável do mod e dependências necessárias.
- Arquivos `.blend`, texturas e animações editáveis.
- Recursos exportados usados pelo jogo.
- Instruções curtas de instalação, controles e obtenção dos itens.
- Resumo do que foi testado e das limitações conhecidas.

Não declare uma funcionalidade concluída apenas porque o código ou o modelo foi criado. Diferencie claramente o que foi implementado, o que foi testado no jogo e o que ainda depende de validação.

O resultado esperado é uma primeira versão pequena, bonita e jogável: pistola, M4, zumbi comum, corredor, tanque, bandagem, kit de primeiros socorros e os dois tipos de munição funcionando juntos.
