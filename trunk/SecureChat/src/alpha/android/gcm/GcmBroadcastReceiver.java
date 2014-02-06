package alpha.android.gcm;

import alpha.android.common.CommonUtilities;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GcmBroadcastReceiver extends BroadcastReceiver 
{
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	Log.i(CommonUtilities.TAG, "GcmBroadcastReceiver's onReceive() was entered.");
    	
    	Toast.makeText(context, "printing context package name: " + context.getPackageName(), Toast.LENGTH_LONG).show();
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        
        context.startService((intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

    	Log.i(CommonUtilities.TAG, "GcmBroadcastReceiver's onReceive() was exited.");
    }
}