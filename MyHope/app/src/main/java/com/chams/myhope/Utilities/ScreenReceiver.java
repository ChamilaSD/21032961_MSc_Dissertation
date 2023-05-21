package com.chams.myhope.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chams.myhope.MainActivity;

/**
 * Created by SamanthaDi on 1/10/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {

    ScreenReceiver screen;
    Context context=null;

    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println(intent.getAction());
//        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//            Intent intent1 = new Intent(context, MainActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
//        }


        this.context=context;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)||intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)||intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}