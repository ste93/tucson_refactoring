package it.unibo.mok.gui.impl.swing;

import it.unibo.mok.gui.impl.Canvas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class CanvasContainer extends JPanel {

    private static final Color GRID_COLOR = Color.decode("#cccccc");
    private static final int GRID_SIZE = 50;
    private static final long serialVersionUID = -1381108740335923215L;

    public CanvasContainer(final Canvas canvas) {
        this.setLayout(new BorderLayout());
        this.add(canvas, BorderLayout.CENTER);
        this.setBackground(Color.white);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        // TODO Why is this called every frame??
        super.paintComponent(g);
        final int width = this.getWidth();
        final int height = this.getHeight();
        g.setColor(CanvasContainer.GRID_COLOR);
        int x = CanvasContainer.GRID_SIZE;
        while (x < width) {
            g.drawLine(x, 0, x, height);
            x = x + CanvasContainer.GRID_SIZE;
        }
        int y = 0;
        while (y < height) {
            g.drawLine(0, y, width, y);
            y = y + CanvasContainer.GRID_SIZE;
        }
    }
}
