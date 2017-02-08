package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import database.ActivityBaseHelper;
import database.ActivityCursorWrapper;
import database.ActivityDbSchema;
import database.ActivityDbSchema.ActivityTable;

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

    private static ContentValues getContentValues(String activityString){
        ContentValues values = new ContentValues();
        Date d = new Date();
        values.put(ActivityTable.Cols.TIME, d.getTime());
        values.put(ActivityTable.Cols.TYPE, activityString);
        return values;
    }

    private ActivityCursorWrapper queryActivities(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                ActivityTable.NAME,
                null, // SELECT all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ActivityCursorWrapper(cursor);
    }

    public List<String> getActivities(){
        List<String> activities = new ArrayList<String>();
        ActivityCursorWrapper cursor = queryActivities(null, null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                activities.add(cursor.getActivity());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return activities;
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        DetectedActivity highestProbActivity = probableActivities.get(0);
        DetectedActivity lastAct;
        mContext = getApplicationContext();
        mDatabase = new ActivityBaseHelper(mContext)
                .getWritableDatabase();
        String activityString;
        Date activityDate;
        ContentValues vals;

        for( DetectedActivity activity : probableActivities ) {
            if (activity.getConfidence() > highestProbActivity.getConfidence())
                highestProbActivity = activity;
        }

        switch( highestProbActivity.getType() ) {
            case DetectedActivity.IN_VEHICLE: {
                activityString = "In Vehicle";
                vals = getContentValues(activityString);
                mDatabase.insert(ActivityTable.NAME, null, vals);
                Log.e("ActivityRecogition", "In Vehicle: " + highestProbActivity.getConfidence());
                break;
            }
            case DetectedActivity.ON_FOOT: {
                activityString = "On Foot";
                vals = getContentValues(activityString);
                mDatabase.insert(ActivityTable.NAME, null, vals);
                Log.e( "ActivityRecogition", "On Foot: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.RUNNING: {
                activityString = "Running";
                vals = getContentValues(activityString);
                mDatabase.insert(ActivityTable.NAME, null, vals);
                Log.e( "ActivityRecogition", "Running: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.STILL: {
                activityString = "Still";
                vals = getContentValues(activityString);
                mDatabase.insert(ActivityTable.NAME, null, vals);
                Log.e( "ActivityRecogition", "Still: " + highestProbActivity.getConfidence() );
                break;
            }
            case DetectedActivity.WALKING: {
                activityString = "Walking";
                vals = getContentValues(activityString);
                mDatabase.insert(ActivityTable.NAME, null, vals);
                Log.e( "ActivityRecogition", "Walking: " + highestProbActivity.getConfidence() );
                /*
                if( activity.getConfidence() >= 75 ) {
                    NotificationCompat.Builder builder = new NotificationCompat.B8uilder(this);
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
        List<String> acts = getActivities();
        for(String act: acts){
            System.out.println(act);
        }
        System.out.println();
    }
}
