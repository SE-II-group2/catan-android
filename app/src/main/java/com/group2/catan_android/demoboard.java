package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.objects.Hexagon;

import java.util.List;
import java.util.Locale;

public class demoboard extends AppCompatActivity{

    // hexagon icon measurements
    final static int hexagonHeight = 198;
    final static int hexagonWidth = hexagonHeight/99*86; // 99:86 is the aspect ratio of a hexagon with equal sites
    final static int hexagonHalfHeight = hexagonHeight/2; // also equals size of one site of the hexagon
    final static int hexagonQuarterWidth = hexagonWidth/4;
    final static int hexagonQuarterHeight = hexagonHeight/4;

    // intersection Size
    static int intersectionSize = 40;

    Board board = new Board();
    List<Hexagon> hexagonList = board.getHexagonList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demoboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        ConstraintLayout constraintLayout = findViewById(R.id.main);

        //init of arrays to store displayable views and values
        int[] hexagonPictures = new int[19];
        int[] hexagonRollValues = new int[19];
        ImageView[] hexagonViews = new ImageView[hexagonPictures.length];
        ImageView[] intersectionViews = new ImageView[54];
        ImageView[] connectionViews = new ImageView[72];
        TextView[] rollValueViews = new TextView[19];

        //get Roll Values and set images of Hexagons
        for (int i = 0; i < hexagonPictures.length; i++) {
            Hexagon hexagon = hexagonList.get(i);

            hexagonRollValues[i] = hexagon.getRollValue(); // save roll Value

            switch (hexagon.getType()) { // save SVG
                case "HILLS":
                    hexagonPictures[i] = R.drawable.hexagon_brick_svg;
                    break;
                case "FOREST":
                    hexagonPictures[i] = R.drawable.hexagon_wood_svg;
                    break;
                case "MOUNTAINS":
                    hexagonPictures[i] = R.drawable.hexagon_stone_svg;
                    break;
                case "PASTURE":
                    hexagonPictures[i] = R.drawable.hexagon_sheep_svg;
                    break;
                case "FIELDS":
                    hexagonPictures[i] = R.drawable.hexagon_wheat_svg;
                    break;
                default:
                    hexagonPictures[i] = R.drawable.desert_hexagon_svg;
                    break;
            }
        }

        //draw Hexagons
        for (int i = 0; i < hexagonPictures.length; i++){
            ImageView hexagonView = new ImageView(this);
            hexagonView.setId(ViewCompat.generateViewId());
            hexagonView.setImageDrawable(ContextCompat.getDrawable(this, hexagonPictures[i]));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonWidth,hexagonHeight);
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;

            hexagonView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "index " + (hexagonView.getId()), Toast.LENGTH_SHORT).show();
            });
        }

        //draw Connections
        for (int i = 0; i < connectionViews.length; i++){
            ImageView connectionView = new ImageView(this);
            connectionView.setId(ViewCompat.generateViewId());
            connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.steet_red));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonHalfHeight,hexagonHalfHeight);
            constraintLayout.addView(connectionView, params);
            connectionViews[i] = connectionView;

            // toast for testing
            connectionView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "index " + (connectionView.getId()), Toast.LENGTH_SHORT).show();
                connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.steet_red));
            });
        }

        //draw Intersections
        for (int i = 0; i < 54; i++){
            ImageView intersectionView = new ImageView(this);
            intersectionView.setId(ViewCompat.generateViewId());
            intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.intersection));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(intersectionSize,intersectionSize);
            constraintLayout.addView(intersectionView, params);
            intersectionViews[i] = intersectionView;

            // toast for testing
            intersectionView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "index " + (intersectionView.getId()), Toast.LENGTH_SHORT).show();
                intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city));
            });
        }

        //draw Roll Values
        for (int i = 0; i < hexagonRollValues.length; i++){
            TextView rollValueView = new TextView(this);
            rollValueView.setId(ViewCompat.generateViewId());
            String rollValue = String.format(Locale.getDefault(),"%d",hexagonRollValues[i]);
            rollValueView.setText(rollValue);

            rollValueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rollValueView.setTextColor(Color.BLACK);
            rollValueView.setGravity(Gravity.CENTER);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonHalfHeight,hexagonHalfHeight); //view size should be square
            constraintLayout.addView(rollValueView, params);
            rollValueViews[i] = rollValueView;
        }

        //constrain the drawables to the right position
        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, layoutWidth, layoutHeight);
        });
    }


    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews,int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //margins for first Hexagon
        int firstRowMargin = layoutWidth/2 - hexagonWidth - hexagonWidth/2;
        int firstTopMargin = (layoutHeight/2) - 2 * hexagonHeight - getStatusBarHeight();

        //set starting values for drawables
        int hexagon = hexagonViews[0].getId();
        int rollValue = rollValueViews[0].getId();
        int intersection = intersectionViews[0].getId();
        int connection = connectionViews[0].getId();
        int prevHexagon = constraintLayout.getId();

        //margins
        int conStartMargin = hexagonQuarterWidth;
        int conEndMargin = hexagonQuarterWidth*3;
        int conTopMargin = hexagonQuarterHeight*3;
        int conBottomMargin = 0;

        while (hexagon <= hexagonViews.length) {

            if (hexagon == 1 || hexagon == 4 || hexagon == 8 || hexagon == 13 || hexagon == 17){//start of new line

                switch(hexagon) {
                    case 1:
                        drawHexagon(set,hexagon,prevHexagon,firstRowMargin,firstTopMargin);
                        connection = drawConnectionRow(set,3, hexagon,connection,hexagonQuarterWidth,hexagonQuarterWidth*3,hexagonQuarterHeight*3,0);
                        break;

                    case 4:
                        drawHexagon(set,hexagon,prevHexagon,-10*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        connection = drawConnectionRow(set,4, hexagon,connection,hexagonQuarterWidth,hexagonQuarterWidth*3,hexagonQuarterHeight*3,0);
                        break;

                    case 8:
                        drawHexagon(set,hexagon,prevHexagon,-14*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        connection = drawConnectionRow(set,5, hexagon,connection,hexagonQuarterWidth,hexagonQuarterWidth*3,hexagonQuarterHeight*3,0);
                        connection = drawConnectionRow(set,5, hexagon,connection,hexagonQuarterWidth*3,hexagonQuarterWidth,0,hexagonQuarterHeight*3);
                        break;

                    case 13:
                        drawHexagon(set,hexagon,prevHexagon,-14*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        connection = drawConnectionRow(set,4, hexagon,connection,hexagonQuarterWidth*3,hexagonQuarterWidth,0,hexagonQuarterHeight*3);
                        break;

                    case 17:
                        drawHexagon(set,hexagon,prevHexagon,-10*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        connection = drawConnectionRow(set,3, hexagon,connection,hexagonQuarterWidth*3,hexagonQuarterWidth,0,hexagonQuarterHeight*3);
                        break;
                }

            } else {
                drawHexagon(set,hexagon,prevHexagon,hexagonWidth,0);
            }

            //drawRollValues
            drawIntersection(set,hexagon,rollValue++,0,0,0,hexagonQuarterHeight);

            prevHexagon = hexagon++;
        }

        //in each iteration: draw a Hexagon with its roll value, 2 intersections and 3 connections
        //and do the same mirrored starting from the bottom at the same time, making it easier to avoid exceptional cases

        /*
        for (int i = 0; i <= hexagonViews.length/2; i++) {
            int hexagonTop = hexagon++;
            int hexagonBottom = hexagon;

            if (i == 0 || i == 3 || i == 7){//start of new line

                switch(i) {
                    case 0:
                        drawHexagon(set,hexagonTop,prevHexagonTop,firstRowMargin,firstTopMargin);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,firstRowMargin+2*hexagonWidth,firstBottomMargin);

                        connectionTop = drawConnectionRow(set,3, hexagonTop,connectionTop);
                        connectionTop = drawConnectionRow(set,3,hexagonBottom,connectionTop);

                        break;

                    case 3:
                        drawHexagon(set,hexagonTop,prevHexagonTop,-10*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,10*hexagonQuarterWidth,-3*hexagonQuarterHeight-1);

                        connectionTop = drawConnectionRow(set,4, hexagonTop,connectionTop);
                        connectionTop = drawConnectionRow(set,4,hexagonBottom,connectionTop);
                        break;

                    case 7:
                        drawHexagon(set,hexagonTop,prevHexagonTop,-14*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,14*hexagonQuarterWidth,-3*hexagonQuarterHeight-1);

                        connectionTop = drawConnectionRow(set,5, hexagonTop,connectionTop);
                        connectionTop = drawConnectionRow(set,5,hexagonBottom,connectionTop);

                        break;
                }

                // draw intersections & connections on outside borders
                drawIntersection(set,hexagonTop,intersectionTop++,0,hexagonWidth,-hexagonHalfHeight,0); //left top border
                drawConnection(set,hexagonTop,connectionTop++,-hexagonWidth,0,0,0,90); //left top border
                drawIntersection(set,hexagonBottom,intersectionBottom--,hexagonWidth,0,0,-hexagonHalfHeight); // right bottom border
                drawConnection(set,hexagonBottom,connectionBottom--,0,-hexagonWidth,0,0,90); //right bottom border

            } else {

                if(i == 9){ // last Hexagon in the middle
                    hexagonBottom = hexagonTop;
                }

                drawHexagon(set,hexagonTop,prevHexagonTop,hexagonWidth,0);
                drawHexagon(set,hexagonBottom,prevHexagonBottom,-hexagonWidth,0);
            }

            //draw intersections to Hexagon
            drawIntersection(set,hexagonTop,intersectionTop++,0,0,-hexagonHeight,0);// top middle
            drawIntersection(set,hexagonTop,intersectionTop++,hexagonWidth,0,-hexagonHalfHeight,0); //top right
            drawIntersection(set,hexagonBottom,intersectionBottom--,0,0,0,-hexagonHeight); //bottom middle
            drawIntersection(set,hexagonBottom,intersectionBottom--,0,hexagonWidth,0,-hexagonHalfHeight); //bottom left


            //all vertical connections already drawn before the last hexagon
            /*
            if (i!=9){
                drawConnection(set,hexagonBottom,connectionBottom--,-hexagonWidth,0,0,0,90); //top down
                drawConnection(set,hexagonTop,connectionTop++,0,-hexagonWidth,0,0,90); //top down
            }


            //drawRollValues
            drawIntersection(set,hexagonTop,rollValue++,0,0,0,hexagonQuarterHeight);
            drawIntersection(set,hexagonBottom,rollValue++,0,0,0,hexagonQuarterHeight);

            prevHexagonTop = hexagon - 1;
            prevHexagonBottom = hexagon++;
        }

         */


        set.applyTo(constraintLayout);
    }


    /*
    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews,int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //margins from left border to first Hexagon in Row
        int thirdRowMargin = layoutWidth/2 - 2 * hexagonWidth - hexagonWidth/2;
        int secondRowMargin = thirdRowMargin + hexagonWidth/2;
        int firstRowMargin = thirdRowMargin + hexagonWidth;

        //margins for the first Hexagons in the first line on top and bottom
        int firstTopMargin = (layoutHeight/2) - 2 * hexagonHeight - getStatusBarHeight();
        int firstBottomMargin = (layoutHeight/2) + hexagonHeight - getStatusBarHeight();

        //set starting values for drawables
        int hexagon = hexagonViews[0].getId();
        int rollValue = rollValueViews[0].getId();
        int intersectionTop = intersectionViews[0].getId();
        int intersectionBottom = intersectionTop + intersectionViews.length-1;
        int connectionTop = connectionViews[0].getId();
        int connectionBottom = connectionTop + connectionViews.length-1;
        int prevHexagonTop = constraintLayout.getId();
        int prevHexagonBottom = prevHexagonTop;

        //in each iteration: draw a Hexagon with its roll value, 2 intersections and 3 connections
        //and do the same mirrored starting from the bottom at the same time, making it easier to avoid exceptional cases
        for (int i = 0; i <= hexagonViews.length/2; i++) {
            int hexagonTop = hexagon++;
            int hexagonBottom = hexagon;

            if (i == 0 || i == 3 || i == 7){//start of new line

                switch(i) {
                    case 0:
                        drawHexagon(set,hexagonTop,prevHexagonTop,firstRowMargin,firstTopMargin);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,firstRowMargin+2*hexagonWidth,firstBottomMargin);
                        break;

                    case 3:
                        drawHexagon(set,hexagonTop,prevHexagonTop,-10*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,10*hexagonQuarterWidth,-3*hexagonQuarterHeight-1);
                        break;

                    case 7:
                        drawHexagon(set,hexagonTop,prevHexagonTop,-14*hexagonQuarterWidth,3*hexagonQuarterHeight+1);
                        drawHexagon(set,hexagonBottom,prevHexagonBottom,14*hexagonQuarterWidth,-3*hexagonQuarterHeight-1);

                        //draw missing intersections in the middle row
                        int missingIntersections = intersectionTop + 7;
                        drawIntersection(set,hexagonBottom,missingIntersections++,-hexagonWidth,hexagonWidth,-hexagonHeight,0); //top middle of left Hexagon
                        drawIntersection(set,hexagonBottom,missingIntersections++,0,hexagonWidth,-hexagonHalfHeight,0); // top left
                        drawIntersection(set,hexagonBottom,missingIntersections++,0,0,-hexagonHeight,0);// top middle
                        drawIntersection(set,hexagonBottom,missingIntersections++,hexagonWidth,0,-hexagonHalfHeight,0); //top right
                        drawIntersection(set,hexagonTop,missingIntersections++,0,hexagonWidth,0,-hexagonHalfHeight); //bottom left
                        drawIntersection(set,hexagonTop,missingIntersections++,0,0,0,-hexagonHeight); //bottom middle
                        drawIntersection(set,hexagonTop,missingIntersections++,hexagonWidth,0,0,-hexagonHalfHeight); //bottom right
                        drawIntersection(set,hexagonTop,missingIntersections,hexagonWidth,-hexagonWidth,0,-hexagonHeight); //bottom middle of right Hexagon

                        //draw missing connections in the middle row
                        drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth,hexagonQuarterWidth*3,0,-hexagonQuarterHeight*3,30); //bottom left
                        drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth*3,hexagonQuarterWidth,-hexagonQuarterHeight*3,0,30); //top right
                        drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth*3,hexagonQuarterWidth,0,-hexagonQuarterHeight*3,-30); //bottom right
                        drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth,hexagonQuarterWidth*3,-hexagonQuarterHeight*3,0,-30); //top left
                        drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth,hexagonQuarterWidth*3-hexagonWidth*2,0,-hexagonQuarterHeight*3,30); //bottom left
                        drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth*3-hexagonWidth*2,hexagonQuarterWidth,-hexagonQuarterHeight*3,0,30); //top right
                        drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth*3,hexagonQuarterWidth-hexagonWidth*2,0,-hexagonQuarterHeight*3,-30); //bottom right
                        drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth-hexagonWidth*2,hexagonQuarterWidth*3,-hexagonQuarterHeight*3,0,-30); //top left
                        break;
                }

                // draw intersections & connections on outside borders
                drawIntersection(set,hexagonTop,intersectionTop++,0,hexagonWidth,-hexagonHalfHeight,0); //left top border
                drawConnection(set,hexagonTop,connectionTop++,-hexagonWidth,0,0,0,90); //left top border
                drawIntersection(set,hexagonBottom,intersectionBottom--,hexagonWidth,0,0,-hexagonHalfHeight); // right bottom border
                drawConnection(set,hexagonBottom,connectionBottom--,0,-hexagonWidth,0,0,90); //right bottom border

            } else {

                if(i == 9){ // last Hexagon in the middle
                        hexagonBottom = hexagonTop;
                }

                drawHexagon(set,hexagonTop,prevHexagonTop,hexagonWidth,0);
                drawHexagon(set,hexagonBottom,prevHexagonBottom,-hexagonWidth,0);
            }

            //draw intersections to Hexagon
            drawIntersection(set,hexagonTop,intersectionTop++,0,0,-hexagonHeight,0);// top middle
            drawIntersection(set,hexagonTop,intersectionTop++,hexagonWidth,0,-hexagonHalfHeight,0); //top right
            drawIntersection(set,hexagonBottom,intersectionBottom--,0,0,0,-hexagonHeight); //bottom middle
            drawIntersection(set,hexagonBottom,intersectionBottom--,0,hexagonWidth,0,-hexagonHalfHeight); //bottom left

            //draw connections to Hexagon
            drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth*3,hexagonQuarterWidth,-hexagonQuarterHeight*3,0,30); //top right
            drawConnection(set,hexagonTop,connectionTop++,hexagonQuarterWidth,hexagonQuarterWidth*3,-hexagonQuarterHeight*3,0,-30); //top left
            drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth,hexagonQuarterWidth*3,0,-hexagonQuarterHeight*3,30); //bottom left
            drawConnection(set,hexagonBottom,connectionBottom--,hexagonQuarterWidth*3,hexagonQuarterWidth,0,-hexagonQuarterHeight*3,-30); //bottom right

            //all vertical connections already drawn before the last hexagon
            if (i!=9){
                drawConnection(set,hexagonBottom,connectionBottom--,-hexagonWidth,0,0,0,90); //top down
                drawConnection(set,hexagonTop,connectionTop++,0,-hexagonWidth,0,0,90); //top down
            }

            //drawRollValues
            drawIntersection(set,hexagonTop,rollValue++,0,0,0,hexagonQuarterHeight);
            drawIntersection(set,hexagonBottom,rollValue++,0,0,0,hexagonQuarterHeight);

            prevHexagonTop = hexagon - 1;
            prevHexagonBottom = hexagon++;
        }

        set.applyTo(constraintLayout);
    }


     */
    public void drawIntersection(ConstraintSet set, int hexagon, int intersection, int startMargin, int endMargin, int topMargin, int bottomMargin){
        set.connect(intersection, ConstraintSet.START, hexagon, ConstraintSet.START, startMargin);
        set.connect(intersection, ConstraintSet.END, hexagon, ConstraintSet.END, endMargin);
        set.connect(intersection, ConstraintSet.TOP, hexagon, ConstraintSet.TOP, topMargin);
        set.connect(intersection, ConstraintSet.BOTTOM, hexagon, ConstraintSet.BOTTOM, bottomMargin);
    }

    public void drawConnection(ConstraintSet set, int hexagon, int connection, int startMargin, int endMargin, int topMargin, int bottomMargin,int rotation){
        set.setRotation(connection,rotation);
        drawIntersection(set,hexagon,connection,startMargin,endMargin,topMargin,bottomMargin);
    }

    public int drawConnectionRow(ConstraintSet set, int hexagonsInRow, int hexagon, int connection, int startMargin, int endMargin, int topMargin, int bottomMargin){
        for(int i = 0; i < hexagonsInRow; i++){
            drawConnection(set,hexagon,connection++,startMargin+hexagonWidth*i,endMargin-hexagonWidth*i,-topMargin,-bottomMargin,-30); //top left
            drawConnection(set,hexagon,connection++,endMargin+hexagonWidth*i,startMargin-hexagonWidth*i,-topMargin,-bottomMargin,30); //top right
        }
        return connection;
    }

    public void drawHexagon(ConstraintSet set, int hexagon, int prevHexagon, int startMargin, int topMargin){
        set.connect(hexagon, ConstraintSet.START, prevHexagon, ConstraintSet.START, startMargin);
        set.connect(hexagon, ConstraintSet.TOP, prevHexagon, ConstraintSet.TOP, topMargin);
    }

    //TODO: find alternative Method to get StatusBarHeight
    // does not work correctly on all devices
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



}