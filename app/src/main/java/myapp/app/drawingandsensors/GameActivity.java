package myapp.app.drawingandsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private LinearLayout gameLinearLayout;
    private MyDrawing myDrawing;
    private ArrayList<String> shapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent receivedIntent = getIntent();
        shapes = receivedIntent.getStringArrayListExtra("shapes");
        gameLinearLayout = findViewById(R.id.gameLinearLayout);
        myDrawing = new MyDrawing(this);
        myDrawing.setShapes(shapes);
        gameLinearLayout.addView(myDrawing);
    }
}