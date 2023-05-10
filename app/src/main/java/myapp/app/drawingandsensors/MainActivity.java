package myapp.app.drawingandsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static int R_CODE = 1;
    private ArrayList<String> shapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onActivityResult(int code, int resultCode, Intent data) {
        super.onActivityResult(code, resultCode, data);
        if (code == R_CODE && resultCode == RESULT_OK) {
            shapes = data.getStringArrayListExtra("shapes");
        }
    }

    public void showDrawing(View view) {
        Intent drawingIntent = new Intent(this, DrawingActivity.class);
        startActivityForResult(drawingIntent, R_CODE);
    }

    public void startGame(View view) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putStringArrayListExtra("shapes", shapes);
        startActivity(gameIntent);
    }

    public void quitApp(View view) {
        finishAffinity();
    }
}