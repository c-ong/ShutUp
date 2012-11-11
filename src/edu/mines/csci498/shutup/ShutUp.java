package edu.mines.csci498.shutup;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShutUp extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shut_up);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shut_up, menu);
        return true;
    }
}
