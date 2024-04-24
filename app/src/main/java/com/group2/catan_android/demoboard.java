package com.group2.catan_android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class demoboard extends AppCompatActivity{
    // hexagon icon measurements
    static int hexagonSize = 198;
    static int intersectionSize = 40;
    static int connectionSize = hexagonSize/2;
    static int halfHexagonSize = hexagonSize/2;


    //TODO: should be moved to backend
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

        int[] hexagonPictures = new int[19];

        for (int i = 0; i < hexagonPictures.length; i++) {
            Hexagon hexagon = hexagonList.get(i);

            switch (hexagon.getType()) {
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

        ImageView[] hexagonViews = new ImageView[hexagonPictures.length];
        ImageView[] intersectionViews = new ImageView[54];
        ImageView[] connectionViews = new ImageView[72];

        for (int i = 0; i < hexagonPictures.length; i++){
            ImageView hexagonView = new ImageView(this);
            hexagonView.setId(ViewCompat.generateViewId());
            hexagonView.setImageDrawable(ContextCompat.getDrawable(this, hexagonPictures[i]));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonSize,hexagonSize);
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;
        }

        for (int i = 0; i < connectionViews.length; i++){
            ImageView connectionView = new ImageView(this);
            connectionView.setId(ViewCompat.generateViewId());
            connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.street));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(connectionSize,connectionSize);
            constraintLayout.addView(connectionView, params);
            connectionViews[i] = connectionView;

            // toast for testing
            connectionView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "index " + (connectionView.getId()), Toast.LENGTH_SHORT).show();
                connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.steet_red));
            });
        }

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
                params.width = intersectionSize; // Beispiel für die Verdoppelung der Größe
                params.height = intersectionSize; // Beispiel für die Verdoppelung der Größe
            });
        }

        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, layoutWidth, layoutHeight);
        });
    }

    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        final int margin = -26;
        int thirdRow = layoutWidth/2 - 2*hexagonSize - halfHexagonSize - 2*margin;
        Log.d("nav","t " + getNavBarHeight());
        int secondRow = thirdRow + halfHexagonSize + margin/2;
        int firstRow = thirdRow + hexagonSize + margin;
        int firstHexagonMargin = (layoutHeight/2) - 2*hexagonSize - getStatusBarHeight();
        int secondHexagonMargin = (layoutHeight/2) + hexagonSize - getStatusBarHeight();

        int prevDrawableTop = ConstraintSet.PARENT_ID;
        int prevDrawableBottom = ConstraintSet.PARENT_ID;
        int hexagonID = hexagonViews[0].getId();
        int intersectionTopID = intersectionViews[0].getId();
        int intersectionBottomID = intersectionTopID + intersectionViews.length-1;
        int connectionID = connectionViews[0].getId();
        int hexagonWidth = hexagonSize+margin;

        for (int i = 0; i <= hexagonViews.length/2; i++) {
            int hexagonTopID = hexagonID++;
            int hexagonBottomID = hexagonID;

            if (i == 0 || i == 3 || i == 7){//start of new line

                switch(i) {
                    case 0:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, firstRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.TOP, firstHexagonMargin);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, firstRow);
                        set.connect(hexagonBottomID, ConstraintSet.TOP, prevDrawableBottom, ConstraintSet.TOP, secondHexagonMargin);
                        break;

                    case 3:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, secondRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.BOTTOM, -halfHexagonSize/2);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, secondRow);
                        set.connect(hexagonBottomID, ConstraintSet.BOTTOM, prevDrawableBottom, ConstraintSet.TOP, -halfHexagonSize/2);
                        break;
                    case 7:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, thirdRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.BOTTOM, -halfHexagonSize/2-1);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, thirdRow);
                        set.connect(hexagonBottomID, ConstraintSet.BOTTOM, prevDrawableBottom, ConstraintSet.TOP, -halfHexagonSize/2-1);

                        //draw missing intersections in the middle row
                        int missingIntersectionsID = intersectionTopID + 7;
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,-hexagonSize-margin,hexagonSize+margin,-hexagonSize,0); //top middle of left Hexagon
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,0,hexagonSize+margin,-halfHexagonSize,0); // top left
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,0,0,-hexagonSize,0);// top middle
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,hexagonSize+margin,0,-halfHexagonSize,0); //top right
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,0,hexagonSize+margin,0,-halfHexagonSize); //bottom left
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,0,0,0,-hexagonSize); //bottom middle
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,hexagonSize+margin,0,0,-halfHexagonSize); //bottom right
                        drawIntersection(set,hexagonTopID,missingIntersectionsID,hexagonSize+margin,-hexagonSize-margin,0,-hexagonSize); //bottom middle of right Hexagon

                        //draw missing connections in the middle row
                        drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4-margin/2,hexagonWidth/4*3-margin/2,0,-halfHexagonSize-halfHexagonSize/2,30); //bottom left
                        drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4*3-margin/2,hexagonWidth/4-margin/2,-halfHexagonSize-halfHexagonSize/2,0,30); //top right
                        drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4*3-margin/2,hexagonWidth/4-margin/2,0,-halfHexagonSize-halfHexagonSize/2,-30); //bottom right
                        drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4-margin/2,hexagonWidth/4*3-margin/2,-halfHexagonSize-halfHexagonSize/2,0,-30); //top left
                        drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4-margin/2,hexagonWidth/4*3-margin/2-hexagonWidth*2,0,-halfHexagonSize-halfHexagonSize/2,30); //bottom left
                        drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4*3-margin/2-hexagonWidth*2,hexagonWidth/4-margin/2,-halfHexagonSize-halfHexagonSize/2,0,30); //top right
                        drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4*3-margin/2,hexagonWidth/4-margin/2-hexagonWidth*2,0,-halfHexagonSize-halfHexagonSize/2,-30); //bottom right
                        drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4-margin/2-hexagonWidth*2,hexagonWidth/4*3-margin/2,-halfHexagonSize-halfHexagonSize/2,0,-30); //top left
                        break;
                }

                // draw intersections & connections on outside borders
                drawIntersection(set,hexagonTopID,intersectionTopID++,0,hexagonSize+margin,-halfHexagonSize,0); //left top border
                drawConnection(set,hexagonTopID,connectionID++,-hexagonWidth,0,0,0,90); //left top border
                drawIntersection(set,hexagonBottomID,intersectionBottomID--,hexagonSize+margin,0,0,-halfHexagonSize); // right bottom border
                drawConnection(set,hexagonBottomID,connectionID++,0,-hexagonWidth,0,0,90); //right bottom border

            } else {

                if(i == 9){ // last Hexagon in the middle
                        hexagonBottomID = hexagonTopID;
                }

                set.connect(hexagonTopID, ConstraintSet.START, prevDrawableTop, ConstraintSet.END, margin);
                set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.TOP, 0);
                set.connect(hexagonBottomID, ConstraintSet.END, prevDrawableBottom, ConstraintSet.START, margin);
                set.connect(hexagonBottomID, ConstraintSet.TOP, prevDrawableBottom, ConstraintSet.TOP, 0);

            }

            //draw intersections to Hexagon
            drawIntersection(set,hexagonTopID,intersectionTopID++,0,0,-hexagonSize,0);// top middle
            drawIntersection(set,hexagonTopID,intersectionTopID++,hexagonSize+margin,0,-halfHexagonSize,0); //top right
            drawIntersection(set,hexagonBottomID,intersectionBottomID--,0,0,0,-hexagonSize); //bottom middle
            drawIntersection(set,hexagonBottomID,intersectionBottomID--,0,hexagonSize+margin,0,-halfHexagonSize); //bottom left

            //draw connections to Hexagon
            drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4*3-margin/2,hexagonWidth/4-margin/2,-halfHexagonSize-halfHexagonSize/2,0,30); //top right
            drawConnection(set,hexagonTopID,connectionID++,hexagonWidth/4-margin/2,hexagonWidth/4*3-margin/2,-halfHexagonSize-halfHexagonSize/2,0,-30); //top left

            drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4-margin/2,hexagonWidth/4*3-margin/2,0,-halfHexagonSize-halfHexagonSize/2,30); //bottom left
            drawConnection(set,hexagonBottomID,connectionID++,hexagonWidth/4*3-margin/2,hexagonWidth/4-margin/2,0,-halfHexagonSize-halfHexagonSize/2,-30); //bottom right

            //all vertical connections already drawn before the last hexagon
            if (i!=9){
                drawConnection(set,hexagonBottomID,connectionID++,-hexagonWidth,0,0,0,90); //top down
                drawConnection(set,hexagonTopID,connectionID++,0,-hexagonWidth,0,0,90); //top down
            }

            prevDrawableTop = hexagonID - 1;
            prevDrawableBottom = hexagonID++;
        }

        set.applyTo(constraintLayout);
    }

    public void drawIntersection(ConstraintSet set, int hexagonID, int intersectionID, int startMargin, int endMargin, int topMargin, int bottomMargin){
        set.connect(intersectionID, ConstraintSet.START, hexagonID, ConstraintSet.START, startMargin);
        set.connect(intersectionID, ConstraintSet.END, hexagonID, ConstraintSet.END, endMargin);
        set.connect(intersectionID, ConstraintSet.TOP, hexagonID, ConstraintSet.TOP, topMargin);
        set.connect(intersectionID, ConstraintSet.BOTTOM, hexagonID, ConstraintSet.BOTTOM, bottomMargin);
    }

    public void drawConnection(ConstraintSet set, int hexagonID, int connectionID, int startMargin, int endMargin, int topMargin, int bottomMargin,int rotation){
        set.setRotation(connectionID,rotation);
        drawIntersection(set,hexagonID,connectionID,startMargin,endMargin,topMargin,bottomMargin);
    }

    //TODO: find alternative Method to get StatusBarHeight
    // Status Bar Height needed to center Board
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



}