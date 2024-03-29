package alpha.android.gcm;

import alpha.android.common.CommonUtilities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends GCMBaseIntentService
{
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;

    public GcmIntentService()
    {
        super(CommonUtilities.SENDER_ID);
        
        Log.i(CommonUtilities.TAG, "ENTERED GCMINTENTSERVICE");
    }
    

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
//    private void sendNotification(String msg)
//    {
//    	/**
//    	 * TO DO:
//    	 * ======
//    	 * 
//    	 * IMPLEMENT CONNECTION WITH WEB SERVICE
//    	 * 
//    	 */
//        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//        	.setSmallIcon(R.drawable.ic_launcher)
//        	.setContentTitle("GCM Notification")
//        	.setStyle(new NotificationCompat.BigTextStyle()
//        	.bigText(msg))
//        	.setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }

    
    /**
     * Method called on Receiving a new message from GCM server
     **/
	@Override
	protected void onMessage(Context ctx, Intent intent)
	{
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        Log.i(CommonUtilities.TAG, "ENTERED ONMESSAGE IN GCMINTENTSERVICE");
        
        if (!extras.isEmpty())
        {
            // Filter messages based on message type
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
                //sendNotification("Send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                //sendNotification("Deleted messages on server: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                // Fancy testing purposes
                for (int i = 0; i < 5; i++)
                {
                    Log.i(CommonUtilities.TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e) { }
                }
                
                Log.i(CommonUtilities.TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());
                Log.i(CommonUtilities.TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        BroadcastReceiver.completeWakefulIntent(intent);
	}


	@Override
	protected void onRegistered(Context ctx, String regID) 
	{
		// send notification to web service to register regID
	}


	@Override
	protected void onUnregistered(Context ctx, String regID)
	{
		// send notification to web service to remove regID
	}


	@Override
	protected void onError(Context arg0, String message)
	{
		Log.d(CommonUtilities.TAG, "Error: " + message);
	}
    
}
