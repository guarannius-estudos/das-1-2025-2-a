package br.univille;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Janelinha3 extends JFrame {
    private final JPanel painel;

    public Janelinha3() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        painel = new JPanel();
        painel.setBackground(Color.ORANGE);
        painel.setPreferredSize(new Dimension(200,200));
        setLayout(new FlowLayout());
        painel.setBorder(new LineBorder(Color.BLACK));
        painel.setBorder(new TitledBorder("TITULO"));

        add(painel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new Janelinha3();
    }
}
