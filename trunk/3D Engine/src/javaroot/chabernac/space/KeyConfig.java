package chabernac.space;

import chabernac.space.geom.GVector;

public class KeyConfig{
  public static final int STRAFE_LEFT = 0;
  public static final int STRAFE_RIGHT = 1;
  public static final int STRAFE_UP = 2;
  public static final int STRAFE_DOWN = 3;
  public static final int FORWARD = 4;
  public static final int BACKWARD = 5;
  public static final int LEFT = 6;
  public static final int RIGHT = 7;
  public static final int DOWN = 8;
  public static final int UP = 9;
  public static final int ROLL_LEFT = 10;
  public static final int ROLL_RIGHT = 11;
  public static final int SHOOT1 = 12;
  public static final int SHOOT2 = 13;
  public static final int OPEN = 14;
  public static final int ACCELERATE = 15;
  public static final int BREAK = 16;


  public GVector STRAFE_LEFT_VECTOR = new GVector(-15,0,0);
  public GVector STRAFE_RIGHT_VECTOR  = new GVector(15,0,0);
  public GVector STRAFE_DOWN_VECTOR  = new GVector(0,-15,0);
  public GVector STRAFE_UP_VECTOR  = new GVector(0,15,0);
  public GVector FORWARD_VECTOR  = new GVector(0,0,15);
  public GVector BACKWARD_VECTOR  = new GVector(0,0,-15);
  public float   ROTATE = (float)Math.PI/90;

  public float STRAFE_LEFT_DISTANCE = 15;
  public float STRAFE_RIGHT_DISTANCE  = 15;
  public float STRAFE_DOWN_DISTANCE  = 15;
  public float STRAFE_UP_DISTANCE  = 15;
  public float FORWARD_DISTANCE  = 30;
  public float BACKWARD_DISTANCE  = 30;
  public float ACCELERATION  = 1;
  public float BREAK_ACCELERATION = 1;




  public int[] keyBindings = new int[24];

  public KeyConfig(){
    initBindings();
  }

  private void initBindings(){
    //Bind everything to a non existing key event.
    for(int i=0;i<keyBindings.length;i++){ keyBindings[i] = 0; }
  }

}
