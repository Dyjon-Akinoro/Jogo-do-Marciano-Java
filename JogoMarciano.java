import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class JogoMarciano extends JPanel implements ActionListener {

    // --- Variáveis de Estado e Configuração ---
    private String status = "MENU";
    private int arvoreSecreta, tentativas, limite = 7;
    private int maxNumero = 100;
    private String input = "", mensagem = "";
    private boolean novoRecorde = false;
    private int tremor = 0;
    private int[] historico = new int[10];
    
    private Color corFundo = new Color(15, 30, 15);
    private Color corAlvo = new Color(15, 30, 15);

    // --- Tempo e Pontuação ---
    private long tempoInicio;
    private int tempoRestante;
    private final int TEMPO_LIMITE = 30000;
    private int maiorPontuacao = 0;
    private int pontuacaoAtual = 0;
    private float pontuacaoAnimada = 0;

    private ArrayList<Particula> confetes = new ArrayList<>();
    private Timer gameLoop;
    private Random random = new Random();

    public JogoMarciano() {
        setPreferredSize(new Dimension(600, 450));
        setFocusable(true);
        
        // Timer de ~60 FPS (16ms por frame)
        gameLoop = new Timer(16, this);
        gameLoop.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                logicaTeclado(e);
            }
        });
    }

    // O "draw" do Processing acontece aqui
    @Override
    public void actionPerformed(ActionEvent e) {
        atualizarLogica();
        repaint();
    }

    private void atualizarLogica() {
        // Interpolação de cor (lerpColor)
        corFundo = interpolarCor(corFundo, corAlvo, 0.05f);

        if (status.equals("JOGANDO")) {
            tempoRestante = TEMPO_LIMITE - (int)(System.currentTimeMillis() - tempoInicio);
            if (tempoRestante <= 0) {
                tempoRestante = 0;
                status = "DERROTA";
                tremor = 20;
                corAlvo = new Color(60, 15, 15);
                mensagem = "O TEMPO ESGOTOU!";
            }
        }

        // Atualizar Partículas
        Iterator<Particula> it = confetes.iterator();
        while (it.hasNext()) {
            Particula p = it.next();
            p.atualizar();
            if (p.estaMorta()) it.remove();
        }
    }

    private void logicaTeclado(KeyEvent e) {
        char key = e.getKeyChar();
        int code = e.getKeyCode();

        if (status.equals("MENU")) {
            if (key == '1') iniciarJogo(10, 50);
            else if (key == '2') iniciarJogo(7, 100);
            else if (key == '3') iniciarJogo(5, 200);
        } else if (status.equals("JOGANDO")) {
            if (Character.isDigit(key) && input.length() < 3) input += key;
            else if (code == KeyEvent.VK_BACK_SPACE && input.length() > 0) input = input.substring(0, input.length() - 1);
            else if (code == KeyEvent.VK_ENTER && input.length() > 0) processarChute();
            else if (code == KeyEvent.VK_SPACE) usarRadar();
        }

        if (!status.equals("MENU")) {
            if (key == 'r' || key == 'R') reiniciar();
            if (key == 'm' || key == 'M') {
                status = "MENU";
                corAlvo = new Color(15, 30, 15);
            }
        }
    }

    private void usarRadar() {
        tempoInicio -= 10000; 
        tremor = 12;
        corAlvo = new Color(10, 70, 40);

        int tipoDica = random.nextInt(3);
        if (tipoDica == 0) {
            mensagem = "RADAR: É um número " + (arvoreSecreta % 2 == 0 ? "PAR" : "ÍMPAR") + "!";
        } else if (tipoDica == 1) {
            int metade = maxNumero / 2;
            mensagem = "RADAR: É " + (arvoreSecreta > metade ? "MAIOR" : "MENOR ou IGUAL") + " a " + metade + "!";
        } else {
            mensagem = "RADAR: " + (arvoreSecreta % 5 == 0 ? "É" : "NÃO É") + " múltiplo de 5!";
        }
    }

    private void processarChute() {
        int chute = Integer.parseInt(input);
        if (chute < 1 || chute > maxNumero) {
            mensagem = "Apenas 1 a " + maxNumero + "!";
            input = ""; tremor = 15; return;
        }

        historico[tentativas] = chute;
        input = ""; tentativas++;

        if (chute == arvoreSecreta) {
            status = "VITORIA";
            corAlvo = new Color(15, 60, 25);
            tremor = 10;
            for (int i = 0; i < 150; i++) confetes.add(new Particula(300, 150));
            pontuacaoAtual = ((limite - tentativas) * 1000) + (tempoRestante / 10);
            if (pontuacaoAtual > maiorPontuacao) { maiorPontuacao = pontuacaoAtual; novoRecorde = true; }
        } else if (tentativas >= limite) {
            status = "DERROTA"; tremor = 20; corAlvo = new Color(60, 15, 15);
        } else {
            tremor = 8;
            int dif = Math.abs(chute - arvoreSecreta);
            String dica = (chute > arvoreSecreta) ? "MENOR" : "MAIOR";
            if (dif <= 10) { mensagem = dica + " que " + chute + " (QUENTE!)"; corAlvo = new Color(80, 25, 25); }
            else { mensagem = dica + " que " + chute; corAlvo = new Color(15, 30, 70); }
        }
    }

    private void iniciarJogo(int nLimite, int nMax) {
        limite = nLimite; maxNumero = nMax; reiniciar();
    }

    private void reiniciar() {
        arvoreSecreta = random.nextInt(maxNumero) + 1;
        tentativas = 0; input = ""; status = "JOGANDO";
        mensagem = "Adivinhe a árvore (1-" + maxNumero + ")";
        novoRecorde = false; tremor = 0;
        corAlvo = new Color(15, 30, 15);
        confetes.clear();
        pontuacaoAtual = 0; pontuacaoAnimada = 0;
        tempoInicio = System.currentTimeMillis();
        tempoRestante = TEMPO_LIMITE;
        for (int i = 0; i < historico.length; i++) historico[i] = 0;
    }

    // --- Renderização ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (tremor > 0) {
            g2.translate(random.nextInt(tremor * 2 + 1) - tremor, random.nextInt(tremor * 2 + 1) - tremor);
            tremor--;
        }

        setBackground(corFundo);

        if (status.equals("MENU")) {
            desenharMenu(g2);
        } else {
            desenharInterfaceJogo(g2);
            for (Particula p : confetes) p.desenhar(g2);
            if (!status.equals("JOGANDO")) desenharFim(g2);
        }
    }

    private void desenharMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        drawCentered(g, "JOGO DO MARCIANO", 300, 100);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.LIGHT_GRAY);
        drawCentered(g, "Escolha a Dificuldade:", 300, 180);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.GREEN); drawCentered(g, "[ 1 ] FÁCIL: 1 a 50 (10 tentativas)", 300, 230);
        g.setColor(Color.ORANGE); drawCentered(g, "[ 2 ] MÉDIO: 1 a 100 (7 tentativas)", 300, 270);
        g.setColor(Color.RED); drawCentered(g, "[ 3 ] DIFÍCIL: 1 a 200 (5 tentativas)", 300, 310);
    }

    private void desenharInterfaceJogo(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 26));
        drawCentered(g, "JOGO DO MARCIANO", 300, 40);
        
        // Cronômetro
        int segs = (int) Math.ceil(tempoRestante / 1000.0);
        g.setColor(segs <= 10 ? Color.RED : Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Tempo: " + segs + "s", 450, 45);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.GRAY);
        drawCentered(g, "Maior Pontuação: " + maiorPontuacao + " pts", 300, 70);

        g.setColor(new Color(255, 204, 0));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, mensagem, 300, 115);

        // Input
        g.setColor(Color.WHITE);
        g.fillRoundRect(260, 130, 80, 45, 10, 10);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        drawCentered(g, input + (System.currentTimeMillis() % 1000 < 500 && status.equals("JOGANDO") ? "|" : ""), 300, 155);

        g.setColor(new Color(100, 255, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        drawCentered(g, "Pressione [ ESPAÇO ] para usar o Radar Marciano (-10s)", 300, 200);

        // Vidas e Histórico
        desenharVidas(g);
        desenharHistorico(g);
    }

    private void desenharVidas(Graphics2D g) {
        int posX = 300 - (limite * 10);
        for (int i = 0; i < limite; i++) {
            g.setColor(i < tentativas ? Color.DARK_GRAY : Color.RED);
            g.fillRect(posX + (i * 20), 215, 15, 15);
        }
    }

    private void desenharHistorico(Graphics2D g) {
        if (tentativas == 0) return;
        g.setColor(Color.CYAN);
        String h = "";
        for (int i = 0; i < tentativas; i++) h += historico[i] + (i < tentativas-1 ? " - " : "");
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, h, 300, 315);
    }

    private void desenharFim(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, 600, 450);
        
        if (status.equals("VITORIA")) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            drawCentered(g, "PARABÉNS!", 300, 150);
            
            if (pontuacaoAnimada < pontuacaoAtual) pontuacaoAnimada += 15;
            g.setColor(Color.WHITE);
            drawCentered(g, "PONTOS: " + (int)Math.min(pontuacaoAnimada, pontuacaoAtual), 300, 210);
        } else {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            drawCentered(g, "GAME OVER! Era " + arvoreSecreta, 300, 200);
        }
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCentered(g, "R para Reiniciar | M para Menu", 300, 350);
    }

    // --- Helpers ---
    private void drawCentered(Graphics g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    private Color interpolarCor(Color a, Color b, float f) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * f);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * f);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * f);
        return new Color(r, g, bl);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Jogo Marciano");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new JogoMarciano());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // --- Classe Particula interna ---
    class Particula {
        float x, y, vx, vy, opacidade = 255;
        Color cor;
        Particula(float x, float y) {
            this.x = x; this.y = y;
            this.vx = (random.nextFloat() * 10) - 5;
            this.vy = (random.nextFloat() * -8) - 2;
            this.cor = Color.getHSBColor(random.nextFloat(), 0.8f, 0.9f);
        }
        void atualizar() { vy += 0.2f; x += vx; y += vy; opacidade -= 4; }
        void desenhar(Graphics2D g) {
            if (opacidade <= 0) return;
            g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int)opacidade));
            g.fillOval((int)x, (int)y, 6, 6);
        }
        boolean estaMorta() { return opacidade <= 0; }
    }
}