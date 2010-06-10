package chabernac.pie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PiePanel extends JPanel {
  private Pie myPie = null;
  private Piece mySelectedPiece = null;
  private BufferedImage myImage = null;
  private Map myPieMap = null;
  
  private iPieListener myPieListener = null;
  
  public PiePanel(){
    this(null);
  }
  
  public PiePanel(Pie aPie){
    myPie = aPie;
    init();
    addListeners();
  }
  
  private void init(){
    myPieMap = new HashMap();
  }
  
  private void addListeners(){
    addMouseListener(new MyMouseAdapter());
    addMouseMotionListener(new MyMouseMotionAdatper());
  }
  
  public iPieListener getPieListener() {
    return myPieListener;
  }

  public void setPieListener(iPieListener anPieListener) {
    myPieListener = anPieListener;
  }

  public Pie getPie() {
    return myPie;
  }

  public void setPie(Pie anPie) {
    myPie = anPie;
  }
  
  public void paint(Graphics aGraphics){
    int theWidth = getWidth();
    int theHeight = getHeight();
    myImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics g = myImage.getGraphics();
    myPieMap.clear();
    
    g.setColor(Color.white);
    g.fillRect(0, 0, theWidth, theHeight);
    
    double theCurrentAngle = 0D;
    
    int j=0;
    double theColorPerPiece = 255d / myPie.getPieces().size();
    for(Iterator i=myPie.getPieces().iterator();i.hasNext();){
      Piece thePiece = (Piece)i.next();
      double theWeight = myPie.getWeight(thePiece);
      double theAngle = theWeight * 360;
      int theColor = (int)(theWeight * 255);
      Color theC = new Color(theColor, (int)(j * theColorPerPiece), 0);
      myPieMap.put(new Integer(theC.getRGB()), thePiece);
      g.setColor(theC);
      g.fillArc(0, 0, theWidth,theHeight, (int)Math.floor(theCurrentAngle), (int)Math.ceil(theAngle));
      theCurrentAngle += theAngle;
      j++;
    }
    
    g.setColor(Color.black);
    g.drawString(myPie.getName(), 5, 20);
    if(mySelectedPiece != null){
      g.drawString(mySelectedPiece.getName(), 5, getHeight() - 20);
    }
    
    aGraphics.drawImage(myImage, 0, 0, null);
  }
  
  public Piece getPieceAt(int x, int y){
    Integer theRGB = new Integer(myImage.getRGB(x, y));
    return (Piece)myPieMap.get(theRGB);
  }
  
  private class MyMouseAdapter extends MouseAdapter{
    public void mouseClicked(MouseEvent anE) {
      Piece thePiece = getPieceAt(anE.getX(), anE.getY());
      if(thePiece != null && myPieListener != null){
        myPieListener.pieceSelectedEvent(thePiece, anE);
      }
    }
  }
  
  private class MyMouseMotionAdatper extends MouseMotionAdapter{
    public void mouseMoved(MouseEvent e) {
      Piece thePiece = getPieceAt(e.getX(), e.getY());
      if(mySelectedPiece != thePiece){
        mySelectedPiece = thePiece;
        repaint();
      }
    }
  }
  
  public static void main(String args[]){
    
    Pie thePie = new Pie();
    Piece thePiece = new DefaultPiece("1", 5);
    thePie.addPiece(thePiece);
    Piece thePiece2 = new DefaultPiece("2", 20);
    thePie.addPiece(thePiece2);
    Piece thePiece3 = new DefaultPiece("3", 10);
    thePie.addPiece(thePiece3);
    Piece thePiece4 = new DefaultPiece("4", 12);
    thePie.addPiece(thePiece4);
    System.out.println("weight: " + thePie.getWeight(thePiece2));
    
    PiePanel thePanel = new PiePanel(thePie);
    
    JFrame theFrame = new JFrame();
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.getContentPane().setLayout(new BorderLayout());
    theFrame.getContentPane().add(thePanel);
    theFrame.setSize(200,200);
    theFrame.setVisible(true);
  }
}
