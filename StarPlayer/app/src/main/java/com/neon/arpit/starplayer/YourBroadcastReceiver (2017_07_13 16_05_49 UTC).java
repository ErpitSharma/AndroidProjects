package com.neon.arpit.starplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by arpit on 16-02-2017.
 */
  public class YourBroadcastReceiver extends BroadcastReceiver{

    // Constructor is mandatory
    public YourBroadcastReceiver()
    {
        super ();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Log.i ("s gh", intentAction.toString() + " happended");
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            Log.i ("a yy", "no media button information");
            return;
        }
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            Log.i ("ag a", "no keypress");
            return;
        }
        // other stuff you want to do
    }
}