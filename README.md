# Jogo do Marciano 👽

Projeto desenvolvido para a disciplina de Desenvolvimento de Games - UNINASSAU (5NA) 2026.1.

## 👨‍💻 Desenvolvedores
* **Allan Luciano da Silva** 
* **Dyjon Akinorô Francisco de Lima** 

## 🚀 Como Executar
1. Tenha o JDK instalado.
2. Compile: `javac JogoMarciano.java`
3. Rode: `java JogoMarciano`

## 📝 Relatório Técnico

1. Visão Geral do Projeto
O Jogo do Marciano é uma aplicação desktop de adivinhação numérica que integra lógica, agilidade sob pressão (tempo limitado) e mecânicas de risco/recompensa. O objetivo central é identificar um número secreto ("a árvore") dentro de parâmetros restritos de tentativas e tempo, utilizando dicas convencionais ou o recurso estratégico "Radar Marciano".

2. Estrutura Tecnológica
Linguagem: Java.
Bibliotecas Gráficas: javax.swing (JPanel, JFrame) e java.awt (Graphics2D).
Paradigma: Programação Orientada a Objetos (POO) com classes internas e interfaces (ActionListener).
Motor de Renderização: Customizado via javax.swing.Timer operando a 60 FPS (ciclos de 16ms).

3. Arquitetura do Código
3.1. Máquina de Estados
O fluxo da aplicação é governado pela variável status, alternando entre:
MENU: Seleção de dificuldade e inicialização de variáveis.
JOGANDO: Processamento de inputs, cronômetro e lógica de colisão lógica.
VITÓRIA / DERROTA: Encerramento, exibição de recordes e feedback de pontuação.

3.2. Mecânicas Principais
Radar Marciano
Função usarRadar(): gera dicas lógicas (paridade, múltiplos, faixas) ao custo de 10 segundos de penalidade.
Dificuldade Dinâmica
Escalonamento do limite de tentativas e do maxNumero (50, 100 ou 200).
Feedback Visual
Alteração dinâmica da corAlvo baseada na proximidade aritmética do chute ("Quente" ou "Frio").
Pontuação
Algoritmo:
$$(Tentativas\,Restantes \times 1000) + (Tempo\,Restante / 10)$$

4. Elementos de Game Feel (UX)
Para elevar a experiência do usuário, foram implementados recursos de animação dinâmica:
Screen Shake (Tremor): Manipulação de g2.translate() para criar impacto visual em erros ou eventos críticos.
Sistema de Partículas: Classe interna Particula com física de gravidade, velocidade vetorial e fade-out de opacidade.
Interpolação de Cores: Implementação manual de transição cromática suave (similar ao lerpColor), evitando mudanças bruscas de matiz no fundo.

5. Fluxo de Execução
Inicialização: O método main instância o frame e dispara o Timer.
Entrada: KeyListener dedicado para captura de dígitos, controle de fluxo (Enter/Space) e comandos de reset (R/M).
Ciclo de Atualização:
atualizarLogica(): Processa cronômetro, física de partículas e estados.
paintComponent(): Renderiza a interface e elementos gráficos baseados no estado ativo.

6. Conclusão
O projeto demonstra uma transição técnica sólida de um ambiente de prototipagem rápida (Processing) para uma arquitetura Java robusta. A eficiência no uso de um único Timer garante uma interface responsiva e estável, enquanto o uso de Graphics2D com Antialiasing entrega uma estética polida e profissional condizente com os padrões de desenvolvimento de games atuais.

© Recife, Pernambuco - 2026.1 | Projeto Acadêmico (5NA) - UNINASSAU | Desenvolvimento de Games

