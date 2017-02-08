package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import database.ActivityBaseHelper;

/**
 * Created by Paul on 2/1/16.
 */
public class ActivityRecognizedService extends IntentService {

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        DetectedActivity highestProbActivity = probableActivities.get(0);
        mContext = getApplicationContext();
        mDatabase = new ActivityBaseHelper(mContext)
                .getWritableDatabase();

        for( DetectedActivity activity : probableActivities ) {
            if (activity.getConfidence() > highestProbActivity.getConfidence())
                highestProbActivity = activity;
        }

        switch( highestProbActivity.getType() ) {
            case DetectedActivity.IN_VEHICLE: {
                Log.e("ActivityRecogition", "In Vehicle: " + highestProbActivity.getConfidence());
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Log.e( "ActivityRecogition", "On Foot: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.RUNNING: {
                Log.e( "ActivityRecogition", "Running: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.STILL: {
                Log.e( "ActivityRecogition", "Still: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.WALKING: {
                Log.e( "ActivityRecogition", "Walking: " + highestProbActivity.getConfidence() );
                /*
                if( activity.getConfidence() >= 75 ) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setContentText( "Are you walking?" );
                    builder.setSmallIcon( R.mipmap.ic_launcher );
                    builder.setContentTitle( getString( R.string.app_name ) );
                    NotificationManagerCompat.from(this).notify(0, builder.build());
                } */
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.e( "ActivityRecogition", "Unknown: " + highestProbActivity.getConfidence() );
                break;
            }
        }

    }
}
