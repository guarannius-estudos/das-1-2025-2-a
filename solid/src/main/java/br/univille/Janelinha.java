package br.univille;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Janelinha extends JFrame {
    private final JButton botaozinho;
    private final Controlador controlador;

    public Janelinha() {
        String titulo = "Eu nao acredito";
        setTitle(titulo);
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        botaozinho = new JButton("ME CLICA!");
        controlador = new Controlador();
        botaozinho.addActionListener(controlador);
        botaozinho.addMouseMotionListener(controlador);

        /*
        botaozinho.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"oi");
            }
        });
        */
        
        add(botaozinho);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Janelinha();
    }
}
