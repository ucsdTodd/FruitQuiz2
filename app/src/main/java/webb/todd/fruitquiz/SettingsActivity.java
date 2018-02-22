package webb.todd.fruitquiz;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button soundToggle = (Button) findViewById( R.id.soundToggleButton );
        soundToggle.setSelected( getSharedPreferences( "fruitquiz", MODE_PRIVATE ).getBoolean(
                "sound", true) );

        soundToggle.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View view ) {
                // set main setting
                getSharedPreferences( "fruitquiz", MODE_PRIVATE ).edit().putBoolean(
                        "sound", ((Button)view).isSelected() ).commit();
            }
        });
    }
}
