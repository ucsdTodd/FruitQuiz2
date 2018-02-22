package webb.todd.fruitquiz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );

        ImageView image = (ImageView) findViewById( R.id.imageViewMain );
        image.setImageResource( R.drawable.fruit );

        final MediaPlayer boingSound = MediaPlayer.create( this, R.raw.boing );

        playButton = (Button) findViewById( R.id.buttonPlay );
        playButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( getSharedPreferences( "fruitquiz", MODE_PRIVATE ).getBoolean( "sound", true) ){
                    boingSound.start();
                }
                startActivity( new Intent( MainActivity.this, GameActivity.class ) );
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        menu.add( "Settings" ); // sadded to activate the settings menu
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Log.i( "skippy", "item selected");
        startActivity( new Intent(this, SettingsActivity.class) );
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // only 1 item, skip the unnecessary menuitem for now
        //startActivity( new Intent(this, SettingsActivity.class) );
        return super.onMenuOpened(featureId, menu);
    }
}
