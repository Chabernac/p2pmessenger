package chabernac.android.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ViewFlipper;

public class HelloAndroid extends Activity implements OnClickListener {



    Button next;
    Button previous;
    ViewFlipper vf;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        vf = (ViewFlipper) findViewById(R.id.ViewFlipper01);
        next = (Button) findViewById(R.id.Button01);
        previous = (Button) findViewById(R.id.Button02);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        
        GridView theGridView = (GridView)findViewById( R.id.gridview1 );
        theGridView.setAdapter( new ImageAdapter( this ) );
//        
//        GridView theGridView2 = (GridView)findViewById( R.id.gridview2 );
//        theGridView2.setAdapter( new ImageAdapter( this ) );
 
    }
 
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == next) {
            vf.showNext();
        }
        if (v == previous) {
            vf.showPrevious();
        }
    }

}