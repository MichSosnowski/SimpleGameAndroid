package myapp.app.drawingandsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showDrawing(View view) {
        Intent drawingIntent = new Intent(this, DrawingActivity.class);
        startActivity(drawingIntent);
    }

    public void quitApp(View view) {
        finishAffinity();
    }
}