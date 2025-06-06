package myapp.app.drawingandsensors;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout gameLinearLayout;
    private GameView gameView;
    private ArrayList<String> shapes;
    private int width;
    private int height;
    private SensorManager sensorManager;
    private Sensor accelSensor;

    // --------------- life cycle management --------------
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
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gameView.post(() -> {
            width = gameLinearLayout.getWidth();
            height = gameLinearLayout.getHeight();
        });
    }

    protected void onResume() {
        super.onResume();
        if (accelSensor != null)
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        if (accelSensor != null)
            sensorManager.unregisterListener(this);
    }

    // shows a Toast message when the game is over
    private void gameOver() {
        Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int xacc = (int) (2 * sensorEvent.values[0]);
        int yacc = (int) (2 * sensorEvent.values[1]);
        boolean result = gameView.moveBall(xacc, yacc, width, height);
        if (result) gameOver();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}

class GameView extends View {

    public final static int RECTANGLE = 1;
    public final static int CIRCLE = 2;
    public final static int LINE = 3;
    public final static int START_TEXT = 4;
    public final static int END_TEXT = 5;

    private ArrayList<String> shapes;
    private Paint defaultStyle;
    private Paint textStyle;
    private boolean firstDrawing = true;
    private int startX, startY;
    private int endX, endY;

    public GameView(Context context) {
        super(context);
        shapes = new ArrayList<>();
        defaultStyle = new Paint();
        defaultStyle.setStyle(Paint.Style.FILL);
        defaultStyle.setStrokeWidth(10);
        defaultStyle.setAntiAlias(true);
        textStyle = new Paint();
        textStyle.setStrokeWidth(10);
        textStyle.setAntiAlias(true);
        textStyle.setColor(Color.BLACK);
        textStyle.setTextSize(100);
    }

    // moves a ball according to sensor parameters
    public boolean moveBall(int xacc, int yacc, int width, int height) {
        String[] shape = shapes.get(shapes.size() - 1).split(":");
        Rect textBounds = new Rect();
        textStyle.getTextBounds("E", 0, 1, textBounds);
        // check if there is a collision between a ball and an obstacle
        float x0, y0, x1, y1, radius;
        for (int i = 2; i < shapes.size() - 1; i++) {
            String[] infos = shapes.get(i).split(":");
            x0 = Float.parseFloat(infos[1]);
            y0 = Float.parseFloat(infos[2]);
            x1 = Float.parseFloat(infos[3]);
            y1 = Float.parseFloat(infos[4]);
            switch (Integer.parseInt(infos[0])) {
                case RECTANGLE:
                    float a_top = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y0);
                    float b_top = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x1, y0);
                    float top = (float) distance(x0, y0, x1, y0);
                    // calculating t
                    float t_top = (a_top * a_top - b_top * b_top + top * top) / (2 * top * top);
                    // calculating distance d_top
                    float d_top = (float) Math.sqrt(a_top * a_top - t_top * t_top * top * top);
                    // the intersection point is outside of the segment
                    if (t_top < 0 || t_top > 1) {
                        if (a_top <= 30 || b_top <= 30) {
                            shapes.remove(shapes.size() - 1);
                            shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                    + ":" + (startX + 30) + ":" + (startY + 30)
                                    + ":" + Color.BLACK);
                            invalidate();
                            return false;
                        }
                    }
                    // if distance d is less than or equal to 30 (length of the ball radius) - collision
                    else if (d_top <= 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    float a_left = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y0);
                    float b_left = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y1);
                    float left = (float) distance(x0, y0, x0, y1);
                    // calculating t_left
                    float t_left = (a_left * a_left - b_left * b_left + left * left) / (2 * left * left);
                    // calculating distance d_left
                    float d_left = (float) Math.sqrt(a_left * a_left - t_left * t_left * left * left);
                    // the intersection point is outside of the segment
                    if (t_left < 0 || t_left > 1) {
                        if (a_left <= 30 || b_left <= 30) {
                            shapes.remove(shapes.size() - 1);
                            shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                    + ":" + (startX + 30) + ":" + (startY + 30)
                                    + ":" + Color.BLACK);
                            invalidate();
                            return false;
                        }
                    }
                    // if distance d is less than or equal to 30 (length of the ball radius) - collision
                    else if (d_left <= 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    float a_right = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x1, y0);
                    float b_right = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x1, y1);
                    float right = (float) distance(x1, y0, x1, y1);
                    // calculating t_right
                    float t_right = (a_right * a_right - b_right * b_right + right * right) / (2 * right * right);
                    // calculating distance d_right
                    float d_right = (float) Math.sqrt(a_right * a_right - t_right * t_right * right * right);
                    // the intersection point is outside of the segment
                    if (t_right < 0 || t_right > 1) {
                        if (a_right <= 30 || b_right <= 30) {
                            shapes.remove(shapes.size() - 1);
                            shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                    + ":" + (startX + 30) + ":" + (startY + 30)
                                    + ":" + Color.BLACK);
                            invalidate();
                            return false;
                        }
                    }
                    // if distance d is less than or equal to 30 (length of the ball radius) - collision
                    else if (d_right <= 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    float a_bottom = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y1);
                    float b_bottom = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x1, y1);
                    float bottom = (float) distance(x0, y1, x1, y1);
                    // calculating t_bottom
                    float t_bottom = (a_bottom * a_bottom - b_bottom * b_bottom + bottom * bottom) / (2 * bottom * bottom);
                    // calculating distance d_bottom
                    float d_bottom = (float) Math.sqrt(a_bottom * a_bottom - t_bottom * t_bottom * bottom * bottom);
                    // the intersection point is outside of the segment
                    if (t_bottom < 0 || t_bottom > 1) {
                        if (a_bottom <= 30 || b_bottom <= 30) {
                            shapes.remove(shapes.size() - 1);
                            shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                    + ":" + (startX + 30) + ":" + (startY + 30)
                                    + ":" + Color.BLACK);
                            invalidate();
                            return false;
                        }
                    }
                    // if distance d is less than or equal to 30 (length of the ball radius) - collision
                    else if (d_bottom <= 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    break;
                case CIRCLE:
                    radius = (float) distance(x0, y0, x1, y1);
                    if (distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y0) <= radius + 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    break;
                case LINE:
                    // calculating distance a, b, c
                    float a = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x0, y0);
                    float b = (float) distance(Float.parseFloat(shape[1]), Float.parseFloat(shape[2]), x1, y1);
                    float c = (float) distance(x0, y0, x1, y1);
                    // calculating t
                    float t = (a * a - b * b + c * c) / (2 * c * c);
                    // calculating distance d
                    float d = (float) Math.sqrt(a * a - t * t * c * c);
                    // the intersection point is outside of the segment
                    if (t < 0 || t > 1) {
                        if (a <= 30 || b <= 30) {
                            shapes.remove(shapes.size() - 1);
                            shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                    + ":" + (startX + 30) + ":" + (startY + 30)
                                    + ":" + Color.BLACK);
                            invalidate();
                            return false;
                        }
                    }
                    // if distance d is less than or equal to 30 (length of the ball radius) - collision
                    else if (d <= 30) {
                        shapes.remove(shapes.size() - 1);
                        shapes.add(GameView.CIRCLE + ":" + (startX) + ":" + (startY)
                                + ":" + (startX + 30) + ":" + (startY + 30)
                                + ":" + Color.BLACK);
                        invalidate();
                        return false;
                    }
                    break;
            }
        }
        // there is a ball in the end point
        if (((Float.parseFloat(shape[1]) - xacc + 30) >= endX + 6 + textBounds.left
           && (Float.parseFloat(shape[1]) - xacc + 30) <= endX + 6 + textBounds.right)
           && ((Float.parseFloat(shape[2]) + yacc + 30) >= endY + 6 + textBounds.top
           && (Float.parseFloat(shape[2]) + yacc + 30) <= endY + 6 + textBounds.bottom)) {
           return true;
        }
        // there is not a ball in the end point but it should be displayed on the screen
        else if (((Float.parseFloat(shape[1]) - xacc + 30) > 60 && (Float.parseFloat(shape[1]) - xacc + 30) < width)
            && ((Float.parseFloat(shape[2]) + yacc + 30) > 60 && (Float.parseFloat(shape[2]) + yacc + 30) < height)) {
            shapes.remove(shapes.size() - 1);
            shapes.add(GameView.CIRCLE + ":" + (Float.parseFloat(shape[1]) - xacc) + ":" + (Float.parseFloat(shape[2]) + yacc)
                    + ":" + (Float.parseFloat(shape[1]) - xacc + 30) + ":" + (Float.parseFloat(shape[2]) + yacc + 30)
                    + ":" + Color.BLACK);
            invalidate();
        }
        return false;
    }

    public void setShapes(ArrayList<String> shapes) {
        this.shapes = shapes;
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        float x0, y0, x1, y1, radius;
        int color;
        String[] start;
        if (firstDrawing) {
            if (shapes.get(0).startsWith(String.valueOf(START_TEXT)))
                start = shapes.get(0).split(":");
            else start = shapes.get(1).split(":");
            shapes.add(CIRCLE + ":" + start[1] + ":" + start[2] + ":" + (Float.parseFloat(start[1]) + 30)
                    + ":" + (Float.parseFloat(start[2]) + 30) + ":" + Color.BLACK);
            firstDrawing = false;
        }
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
                    radius = (float) distance(x0, y0, x1, y1);
                    canvas.drawCircle(x0, y0, radius, defaultStyle);
                    break;
                case LINE:
                    canvas.drawLine(x0, y0, x1, y1, defaultStyle);
                    break;
                case START_TEXT:
                    canvas.drawText("S", x0, y0, textStyle);
                    startX = (int) x0;
                    startY = (int) y0;
                    break;
                case END_TEXT:
                    canvas.drawText("E", x0, y0, textStyle);
                    endX = (int) x0;
                    endY = (int) y0;
                    break;
            }
        }
    }

    public double distance(float xa, float ya, float xb, float yb) {
        return Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
    }
}