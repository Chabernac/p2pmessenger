/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OrderedDrinksAdapter extends BaseAdapter{
  private final DrinkList myDrinkList;
  private final Context myContext;

  public OrderedDrinksAdapter( Context aContext, DrinkList aDrinkList ) {
    myContext = aContext;
    myDrinkList = aDrinkList;
    myDrinkList.registerObserver( new DrinkListObserver() );
  }

  @Override
  public int getCount() {
    return myDrinkList.getList().size() * 4;
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    int theItem = (int)Math.floor( position / 4 );
    boolean isName = position % 4 == 0;
    boolean isNr = position % 4 == 1;
    boolean isLess = position % 4 == 2;
//    boolean isMore = position % 4 == 3;

    Drink theDrink = myDrinkList.getDrinkAt( theItem );

    if(isName || isNr){
      TextView theTextView = new TextView(myContext);
      theTextView.setTextColor(Color.BLACK);

      if(isNr){
        theTextView.setText( Integer.toString(myDrinkList.getDrinkOrder( theDrink )) );
      } else {
        theTextView.setText( theDrink.getName() );
      }

      System.out.println("Adding text at " + position);
      return theTextView;
    } else {
      System.out.println("Adding button at " + position);
      Button theButton = new Button( myContext );
      theButton.setWidth( 5 );
      theButton.setHeight( 3 );
      if(isLess) {
        theButton.setText( "-" );
        theButton.setOnClickListener( new RemoveDrinkListener( theDrink ));
      } else {
        theButton.setText( "+" );
        theButton.setOnClickListener( new AddDrinkListener( theDrink));
      }
      return theButton;
    }
  }

  private class RemoveDrinkListener implements OnClickListener{
    private final Drink myDrink;

    public RemoveDrinkListener(Drink aDrink){
      myDrink = aDrink;
    }

    @Override
    public void onClick( View aView ) {
      myDrinkList.removeDrink( myDrink );
    }
  }

  private class AddDrinkListener implements OnClickListener{
    private final Drink myDrink;

    public AddDrinkListener(Drink aDrink){
      myDrink = aDrink;
    }

    @Override
    public void onClick( View aView ) {
      myDrinkList.addDrink( myDrink );
    }
  }
  
  public class DrinkListObserver extends DataSetObserver {
    public void onChanged(){
     notifyDataSetChanged();
    }

  }



}
