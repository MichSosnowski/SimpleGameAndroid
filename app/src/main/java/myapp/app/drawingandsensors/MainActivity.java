package myapp.app.drawingandsensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static int R_CODE = 1;
    private ArrayList<String> shapes;
    private Button gameButton;

    // --------------- life cycle management --------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameButton = findViewById(R.id.gameButton);
        gameButton.setEnabled(false);
    }

    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("shapes", shapes);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        shapes = savedInstanceState.getStringArrayList("shapes");
    }

    public void onActivityResult(int code, int resultCode, Intent data) {
        super.onActivityResult(code, resultCode, data);
        if (code == R_CODE && resultCode == RESULT_OK) {
            shapes = data.getStringArrayListExtra("shapes");
            if (shapes.size() >= 2 && ((shapes.get(0).startsWith(String.valueOf(MyDrawing.START_TEXT))
                && shapes.get(1).startsWith(String.valueOf(MyDrawing.END_TEXT)))
                || (shapes.get(1).startsWith(String.valueOf(MyDrawing.START_TEXT))
                && shapes.get(0).startsWith(String.valueOf(MyDrawing.END_TEXT))))) {
                gameButton.setEnabled(true);
            }
        }
    }

    // launches a new activity for drawing obstacles and start and end points
    public void showDrawing(View view) {
        gameButton.setEnabled(false);
        Intent drawingIntent = new Intent(this, DrawingActivity.class);
        startActivityForResult(drawingIntent, R_CODE);
    }

    // launches a new activity for playing a game
    public void startGame(View view) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putStringArrayListExtra("shapes", shapes);
        startActivity(gameIntent);
    }

    public void quitApp(View view) {
        finishAffinity();
    }
}