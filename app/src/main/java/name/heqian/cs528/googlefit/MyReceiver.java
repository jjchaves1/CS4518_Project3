package name.heqian.cs528.googlefit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Cory on 2/8/17.
 */

public class MyReceiver extends BroadcastReceiver {
    Bitmap bm;
    ImageView image;
    TextView textView;

    public MyReceiver(ImageView i, TextView t, Context context) {
        MainActivity.mediaPlayer = MediaPlayer.create(context, R.raw.beat_02);
        MainActivity.mediaPlayer.setLooping(true);
        image = i;
        textView = t;
    }
    public MyReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        System.out.println("RECEIVED SOMETHING");

        if (bundle != null) {
            int imageFile = (int) bundle.get(ActivityRecognizedService.IMAGE);
            int text = (int) bundle.get(ActivityRecognizedService.TEXT);
            textView.setText(context.getResources().getText(text));
            bm = BitmapFactory.decodeResource(context.getResources(), imageFile);
            image.setImageBitmap(bm);

            // Play the music if we are walking or runnning
            if (context.getResources().getText(text).equals("You are Running") || context.getResources().getText(text).equals("You are Walking")) {
                if (MainActivity.mediaPlayer.isPlaying() == false) {
                    MainActivity.mediaPlayer.start();
                }
            }

            System.out.println("BUNDLE NOT NULL");


        }
    }

    public Bitmap getBitMap() {
        return bm;
    }
}
