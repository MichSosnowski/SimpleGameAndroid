package myapp.app.drawingandsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private LinearLayout gameLinearLayout;
    private GameView gameView;
    private ArrayList<String> shapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent receivedIntent = getIntent();
        shapes = receivedIntent.getStringArrayListExtra("shapes");
        gameLinearLayout = findViewById(R.id.gameLinearLayout);
        gameView = new GameView(this);
        gameView.setShapes(shapes);
        gameLinearLayout.addView(gameView);
    }
}

class GameView extends View {

    public final static int RECTANGLE = 1;
    public final static int CIRCLE = 2;
    public final static int LINE = 3;

    private ArrayList<String> shapes;
    private Paint defaultStyle;

    public GameView(Context context) {
        super(context);
        shapes = new ArrayList<>();
        defaultStyle = new Paint();
        defaultStyle.setStyle(Paint.Style.FILL);
        defaultStyle.setStrokeWidth(10);
        defaultStyle.setAntiAlias(true);
    }

    public void setShapes(ArrayList<String> shapes) {
        this.shapes = shapes;
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        float x0, y0, x1, y1, radius;
        int color;
        for (String shape : shapes) {
            String[] infos = shape.split(":");
            x0 = Float.parseFloat(infos[1]);
            y0 = Float.parseFloat(infos[2]);
            x1 = Float.parseFloat(infos[3]);
            y1 = Float.parseFloat(infos[4]);
            color = Integer.parseInt(infos[5]);
            defaultStyle.setColor(color);
            switch (Integer.parseInt(infos[0])) {
                case RECTANGLE:
                    canvas.drawRect(x0, y0, x1, y1, defaultStyle);
                    break;
                case CIRCLE:
                    radius = (float)distance(x0, y0, x1, y1);
                    canvas.drawCircle(x0, y0, radius, defaultStyle);
                    break;
                case LINE:
                    canvas.drawLine(x0, y0, x1, y1, defaultStyle);
                    break;
            }
        }
    }

    public double distance(float xa, float ya, float xb, float yb) {
        return Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
    }
}