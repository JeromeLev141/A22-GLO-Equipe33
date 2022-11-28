package muracle.domaine.drawer;

import muracle.domaine.MuracleController;
import muracle.utilitaire.CoordPouce;
import muracle.utilitaire.FractionError;
import muracle.utilitaire.PouceError;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Afficheur {

    protected final MuracleController controller;
    protected Dimension initialDimension;

    protected BasicStroke ligneStroke;

    protected BasicStroke selectedStroke;
    protected final Color lineColor;
    protected final Color fillColor;
    private final Color grilleColor;
    protected final Color selectColor;
    protected final Color errorColor;
    protected final Color backErrorColor;

    public Afficheur(MuracleController controller, Dimension initDim) {
        this.controller = controller;
        initialDimension = initDim;
        lineColor = Color.black;
        fillColor = Color.white;
        grilleColor = new Color(150, 173, 233);
        selectColor = new Color(97, 255, 89);
        errorColor = new Color(233, 103, 104);
        backErrorColor = new Color(46, 52, 64);
        ligneStroke = new BasicStroke(1);
        selectedStroke = new BasicStroke(2);
    }

    public void draw(Graphics g, double zoom, Dimension dim, CoordPouce posiCam, CoordPouce dimPlan) throws FractionError {
        /*
        if (zoom >= 2) {
            ligneStroke = new BasicStroke(3 / (float) zoom);
            selectedStroke = new BasicStroke(6 / (float) zoom);
        }

         */
        g.setColor(lineColor);
    }

    protected void drawGrille(Graphics2D g2d, double posX, double posY, double zoom) {
        g2d.setColor(grilleColor);
        double tokenX = posX;
        double tokenY = posY;
        double w = initialDimension.width;
        double h = initialDimension.height;
        if (zoom < 1) {
            w = (1 / zoom) * initialDimension.width;
            h = (1 / zoom) * initialDimension.height;
        }
        while (posX < w) {
            g2d.draw(new Line2D.Double(posX, -h, posX, h));
            posX += controller.getDistLigneGrille().toDouble();
        }
        posX = tokenX - controller.getDistLigneGrille().toDouble();
        while (posX > - w) {
            g2d.draw(new Line2D.Double(posX, -h, posX, h));
            posX -= controller.getDistLigneGrille().toDouble();
        }
        while (posY < h) {
            g2d.draw(new Line2D.Double(-w, posY, w, posY));
            posY += controller.getDistLigneGrille().toDouble();
        }
        posY = tokenY - controller.getDistLigneGrille().toDouble();
        while (posY > - h) {
            g2d.draw(new Line2D.Double(-w, posY, w, posY));
            posY -= controller.getDistLigneGrille().toDouble();
        }
        g2d.setColor(lineColor);
    }

    protected void drawErrorMessage(Graphics2D g2d) {
        if (!controller.getErrorMessage().equals("")) {
            g2d.setFont(new Font("TimesRoman", Font.ITALIC, 18));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int textWidth = g2d.getFontMetrics().stringWidth(controller.getErrorMessage());
            int xPos = (initialDimension.width / 2) - (textWidth / 2);
            int yPos = initialDimension.height - 40;
            Rectangle2D.Double rect = new Rectangle2D.Double(xPos - 6, yPos - 20, textWidth + 12, 28);
            g2d.setColor(backErrorColor);
            g2d.fill(rect);
            g2d.setColor(lineColor);
            g2d.draw(rect);
            g2d.setColor(errorColor);
            g2d.drawString(controller.getErrorMessage(), xPos, yPos);
            controller.ackErrorMessage();
        }
    }


    protected void ajustement(Graphics2D g2d,double zoom,Dimension dim, CoordPouce posiCam, CoordPouce dimPlan){
        AffineTransform at = new AffineTransform();

        try{
            at.translate((-1*zoom * posiCam.getX().sub(dimPlan.getX().div(2)).toDouble()) + (-1*(zoom-1)*dim.getWidth()/2),
                    (-1*zoom * posiCam.getY().sub(dimPlan.getY().div(2)).toDouble()) + (-1*(zoom-1)*dim.getHeight()/2));
        }catch (PouceError ignored){}

        at.scale(zoom,zoom);
        g2d.transform(at);
    }

    protected void reset(Graphics2D g2d, double zoom,Dimension dim, CoordPouce posiCam, CoordPouce dimPlan){
        AffineTransform at = new AffineTransform();
        at.scale(1/zoom,1/zoom);
        try {
            at.translate((1 * zoom * posiCam.getX().sub(dimPlan.getX().div(2)).toDouble()) + (1 * (zoom - 1) * dim.getWidth() / 2),
                    (1 * zoom * posiCam.getY().sub(dimPlan.getY().div(2)).toDouble()) + (1 * (zoom - 1) * dim.getHeight() / 2));
        }catch (PouceError ignored){}
        g2d.transform(at);
    }
}
