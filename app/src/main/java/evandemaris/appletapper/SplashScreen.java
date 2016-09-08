package evandemaris.appletapper;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class SplashScreen extends AppCompatActivity {

    Button mButton;
    Random random = new Random();
    String[] splashText = {
            "Tap every apple you find!",
            "When an apple comes around, you must tap it!",
            "Seize the apple!",
            "Apple-tastic!",
            "What does the apple say?",
            "Apple mania!"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mButton = (Button) findViewById(R.id.startButton);
        mButton.setText(splashText[random.nextInt(splashText.length)]);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (SplashScreen.this, Fruit_Main.class);
                startActivity(intent);
            }
        });


    }

}
