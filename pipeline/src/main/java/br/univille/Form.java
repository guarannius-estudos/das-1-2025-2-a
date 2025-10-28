package br.univille;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Form extends JFrame {
    private Producer producer;
    private Consumer consumer;
    private DrawingPanel drawingPanel;
    private Color currentColor = new Color((int)(Math.random() * 0x1000000));
    
    public Form(String titulo, Producer producer, Consumer consumer) {
        setTitle(titulo);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawingPanel = new DrawingPanel();
        add(drawingPanel);
        this.producer = producer;
        this.consumer = consumer;
        setVisible(true);

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Event> events = consumer.getEvents();

                if(events.size() > 0){
                    for (Event event : events) {
                        drawingPanel.getPoints().add(new Point(event.x(), event.y()));
                    }

                    drawingPanel.repaint();
                }
            }
        });

        if(consumer != null) {
            timer.start();
        }
    }

    class DrawingPanel extends JPanel {
        private List<Point> points = new ArrayList<>();
        private final int PIXEL_SIZE = 5;
        
        public List<Point> getPoints() {
            return points;
        }

        public DrawingPanel() {
            setBackground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    points.add(new Point(e.getX(), e.getY()));

                    if(producer != null) {
                        Event event = new Event(e.getX(), e.getY(), currentColor.getRGB());
                        producer.publishEvent(event);
                    }

                    repaint();
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    points.add(new Point(e.getX(), e.getY()));

                    if(producer != null) {
                        Event event = new Event(e.getX(), e.getY(), currentColor.getRGB());
                        producer.publishEvent(event);
                    }

                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(currentColor);

            for (Point p : points) {
                g2d.fillRect(p.x - (PIXEL_SIZE/2), p.y - (PIXEL_SIZE/2), PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }    
}
