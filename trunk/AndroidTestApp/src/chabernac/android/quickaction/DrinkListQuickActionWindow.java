/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.quickaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import chabernac.android.drinklist.DrinkList;
import chabernac.android.drinklist.DrinkOrder;
import chabernac.android.drinklist.R;

public class DrinkListQuickActionWindow extends QuickActionWindow{
  private final DrinkOrder myOrder;
  private final DrinkList myDrinkList;


  public DrinkListQuickActionWindow( View aView, DrinkOrder aDrinkOrder, DrinkList aDrinkList ) {
    super(aView);
    myOrder = aDrinkOrder;
    myDrinkList = aDrinkList;
    onCreate();
  }

  @Override
  protected void onCreate() {
    // inflate layout
    LayoutInflater inflater =
      (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_grid_layout, null);

    // setup button events
    for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
      View v = root.getChildAt(i);

      if(v instanceof TableRow) {
        TableRow row = (TableRow) v;

        for(int j = 0, jcount = row.getChildCount() ; j < jcount ; j++) {
          View item = row.getChildAt(j);
          if(item instanceof Button) {
            Button b = (Button) item;
            b.setOnClickListener(getOnClickListener(b));
          } else if(item instanceof TextView){
            ((TextView)item).setText( myOrder.getName() );
          }
        }
      }
    }

    // set the inflated view as what we want to display
    this.setContentView(root);
  }

  private OnClickListener getOnClickListener( Button aButton ) {
    if(aButton.getId() == R.id.removealldrinks) return new RemoveAllDrinksListener();
    if(aButton.getId() == R.id.addrink) return new AddDrinkListener();
    if(aButton.getId() == R.id.removedrink) return new RemoveDrinkListener();
    return null;
  }

  public class RemoveDrinkListener implements OnClickListener {

    @Override
    public void onClick( View aView ) {
      myDrinkList.removeDrink(  myOrder );
      dismiss();
    }

  }

  public class AddDrinkListener implements OnClickListener {

    @Override
    public void onClick( View aView ) {
      myDrinkList.addDrink( myOrder );
      dismiss();
    }

  }

  public class RemoveAllDrinksListener implements OnClickListener {

    @Override
    public void onClick( View aView ) {
      myDrinkList.removeAll( myOrder );
      dismiss();
    }

  }

}
