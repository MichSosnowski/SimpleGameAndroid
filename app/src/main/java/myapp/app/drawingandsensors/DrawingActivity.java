package myapp.app.drawingandsensors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DrawingActivity extends AppCompatActivity {

    private LinearLayout mainLinearLayout;
    private MyDrawing myDrawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        mainLinearLayout = findViewById(R.id.drawingLinearLayout);
        myDrawing = new MyDrawing(this);
        mainLinearLayout.addView(myDrawing);
    }

    public void onBackPressed() {
        Intent returnResult = new Intent();
        returnResult.putStringArrayListExtra("shapes", myDrawing.getShapes());
        setResult(RESULT_OK, returnResult);
        super.onBackPressed();
    }

    public void chooseShape(View view) {
        if (view.getId() == R.id.circle) {
            myDrawing.setChosenShape(MyDrawing.CIRCLE);
        }
        else if (view.getId() == R.id.rectangle) {
            myDrawing.setChosenShape(MyDrawing.RECTANGLE);
        }
        else {
            myDrawing.setChosenShape(MyDrawing.LINE);
        }
    }

    public void chooseColor(View view) {
        if (view.getId() == R.id.red) {
            myDrawing.setChosenColor(Color.RED);
        }
        else if (view.getId() == R.id.green) {
            myDrawing.setChosenColor(Color.GREEN);
        }
        else {
            myDrawing.setChosenColor(Color.BLUE);
        }
    }

    public void undo(View view) {
        myDrawing.undo();
    }

    // --------------- life cycle management --------------
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<String> theShapes = myDrawing.getShapes();
        savedInstanceState.putStringArrayList("shapes", theShapes);
        savedInstanceState.putInt("chosenShape", myDrawing.getChosenShape());
        savedInstanceState.putInt("chosenColor", myDrawing.getChosenColor());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> theShapes = savedInstanceState.getStringArrayList("shapes");
        myDrawing.setShapes(theShapes);
        myDrawing.setChosenShape(savedInstanceState.getInt("chosenShape"));
        myDrawing.setChosenColor(savedInstanceState.getInt("chosenColor"));
    }
}

class MyDrawing extends View implements View.OnClickListener, View.OnTouchListener {

    public final static int RECTANGLE = 1;
    public final static int CIRCLE = 2;
    public final static int LINE = 3;

    private ArrayList<String> shapes;
    private Paint defaultStyle;

    // current drawn figure
    private float xinit, yinit, xcurrent, ycurrent;
    private int chosenShape = RECTANGLE;
    private int chosenColor = Color.RED;
    private boolean currentDrawing = false;

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
        if (currentDrawing) {
            defaultStyle.setColor(chosenColor);
            switch(chosenShape) {
                case RECTANGLE:
                    canvas.drawRect(xinit, yinit, xcurrent, ycurrent, defaultStyle);
                    break;
                case CIRCLE:
                    radius = (float) distance(xinit, yinit, xcurrent, ycurrent);
                    canvas.drawCircle(xinit, yinit, radius, defaultStyle);
                    break;
                case LINE:
                    canvas.drawLine(xinit, yinit, xcurrent, ycurrent, defaultStyle);
                    break;
            }
        }
    }

    public MyDrawing(Context context) {
        super(context);
        init();
    }

    public void init() {
        shapes = new ArrayList<>();
        defaultStyle = new Paint();
        defaultStyle.setStyle(Paint.Style.FILL);
        defaultStyle.setStrokeWidth(10);
        defaultStyle.setAntiAlias(true);
        defaultStyle.setColor(Color.RED);
        setOnClickListener(this);
        setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        String newShape = "";
        switch(chosenShape) {
            case RECTANGLE:
                newShape = RECTANGLE + ":" + xinit + ":" + yinit + ":" + xcurrent + ":" + ycurrent + ":" + chosenColor;
                break;
            case CIRCLE:
                newShape = CIRCLE+":"+ xinit + ":" + yinit + ":"+xcurrent+":"+ycurrent+":"+chosenColor;
                break;
            case LINE:
                newShape = LINE + ":" + xinit + ":" + yinit + ":" + xcurrent + ":" + ycurrent + ":" + chosenColor;
                break;
        }
        shapes.add(newShape);
        currentDrawing = false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            xinit = motionEvent.getX();
            yinit = motionEvent.getY();
            xcurrent = xinit;
            ycurrent = yinit;
            currentDrawing = true;
        } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            xcurrent = motionEvent.getX();
            ycurrent = motionEvent.getY();
        }
        invalidate();
        return false;
    }

    public void undo() {
        if(shapes.size() > 0)
            shapes.remove(shapes.size() - 1);
        invalidate();
    }

    public int getChosenShape() {
        return chosenShape;
    }

    public void setChosenShape(int chosenShape) {
        this.chosenShape = chosenShape;
    }

    public int getChosenColor() {
        return chosenColor;
    }

    public void setChosenColor(int chosenColor) {
        this.chosenColor = chosenColor;
    }

    public ArrayList<String> getShapes()  {
        return shapes;
    }

    public void setShapes(ArrayList<String> theShapes) {
        shapes = theShapes;
        invalidate();
    }

    public double distance(float xa, float ya, float xb, float yb) {
        return Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
    }
}