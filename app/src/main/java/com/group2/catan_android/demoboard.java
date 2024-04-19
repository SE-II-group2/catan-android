package com.group2.catan_android;

import android.os.Bundle;
import android.util.Log;
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

public class demoboard extends AppCompatActivity {

    // hexagon icon measurements
    static int hexagonSize = 200;
    static int halfHexagonSize = hexagonSize/2;

    static int intersectionSize = 30;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout constraintLayout = findViewById(R.id.main);

        int[] ids = new int[19];

        for (int i = 0; i < ids.length; i++) {
            Hexagon hexagon = hexagonList.get(i);

            switch (hexagon.getType()) {
                case "HILLS":
                    ids[i] = R.drawable.hexagon_brick_svg;
                    break;
                case "FOREST":
                    ids[i] = R.drawable.hexagon_wood_svg;
                    break;
                case "MOUNTAINS":
                    ids[i] = R.drawable.hexagon_stone_svg;
                    break;
                case "PASTURE":
                    ids[i] = R.drawable.hexagon_sheep_svg;
                    break;
                case "FIELDS":
                    ids[i] = R.drawable.hexagon_wheat_svg;
                    break;
                default:
                    ids[i] = R.drawable.desert_hexagon_svg;
                    break;
            }
        }

        ImageView[] hexagonViews = new ImageView[ids.length];

        for (int i = 0; i < ids.length; i++){
            ImageView hexagonView = new ImageView(this);
            hexagonView.setId(ViewCompat.generateViewId());
            hexagonView.setImageDrawable(ContextCompat.getDrawable(this, ids[i]));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonSize,hexagonSize);
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;
        }

        ImageView[] intersectionViews = new ImageView[54];

        for (int i = 0; i < 54; i++){
            ImageView intersectionView = new ImageView(this);
            intersectionView.setId(ViewCompat.generateViewId());
            intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_drawable));

            Log.d("hex","viewID: " + intersectionView.getId());

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(intersectionSize,intersectionSize);
            constraintLayout.addView(intersectionView, params);
            intersectionViews[i] = intersectionView;

            // toast for testing
            intersectionView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "index " + (intersectionView.getId()-20), Toast.LENGTH_SHORT).show();
                intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city));
            });
        }

        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, layoutWidth, layoutHeight);
        });
    }

    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        final int margin = -24;
        int thirdRow = (layoutWidth / 2) - (2*hexagonSize + halfHexagonSize) - (2*margin);
        int secondRow = thirdRow + halfHexagonSize + (margin/2);
        int firstRow = thirdRow + hexagonSize + margin;
        int firstHexagonMargin = (layoutHeight/2) - (2*hexagonSize+halfHexagonSize) - (4*margin) - getStatusBarHeight();

        int prevDrawableTop = ConstraintSet.PARENT_ID;
        int prevDrawableBottom = ConstraintSet.PARENT_ID;
        int hexagonID = hexagonViews[0].getId();
        int intersectionTopID = intersectionViews[0].getId();
        int intersectionBottomID = intersectionTopID + intersectionViews.length-1;

        for (int i = 0; i <= hexagonViews.length/2; i++) {
            int hexagonTopID = hexagonID++;
            int hexagonBottomID = hexagonID;

            if (i == 0 || i == 3 || i == 7){//start of new line

                switch(i) {
                    case 0:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, firstRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.TOP, firstHexagonMargin);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, firstRow);
                        set.connect(hexagonBottomID, ConstraintSet.BOTTOM, prevDrawableBottom, ConstraintSet.BOTTOM, firstHexagonMargin);
                        break;

                    case 3:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, secondRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.BOTTOM, margin*2);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, secondRow);
                        set.connect(hexagonBottomID, ConstraintSet.BOTTOM, prevDrawableBottom, ConstraintSet.TOP, margin*2);
                        break;
                    case 7:
                        set.connect(hexagonTopID, ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, thirdRow);
                        set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.BOTTOM, margin*2);
                        set.connect(hexagonBottomID, ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END, thirdRow);
                        set.connect(hexagonBottomID, ConstraintSet.BOTTOM, prevDrawableBottom, ConstraintSet.TOP, margin*2);

                        //draw missing intersections on borders
                        int missingIntersectionsID = intersectionTopID + 7;
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,-(hexagonSize)-margin,hexagonSize+margin,-hexagonSize,0); //top middle next Hexagon
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,0,hexagonSize+margin,-halfHexagonSize,0); // top left
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,-halfHexagonSize,-halfHexagonSize,-hexagonSize,0);// top middle
                        drawIntersection(set,hexagonBottomID,missingIntersectionsID++,hexagonSize+margin,0,-halfHexagonSize,0); //top right
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,0,hexagonSize+margin,0,-halfHexagonSize); //bottom left
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,-halfHexagonSize,-halfHexagonSize,0,-hexagonSize); //bottom middle
                        drawIntersection(set,hexagonTopID,missingIntersectionsID++,hexagonSize+margin,0,0,-halfHexagonSize); //bottom right
                        drawIntersection(set,hexagonTopID,missingIntersectionsID,hexagonSize+margin,-(hexagonSize)-margin,0,-hexagonSize); //bottom middle next Hexagon
                        break;

                }

                //draw intersections to Hexagons border
                drawIntersection(set,hexagonTopID,intersectionTopID++,0,hexagonSize+margin,-halfHexagonSize,0);
                drawIntersection(set,hexagonBottomID,intersectionBottomID--,hexagonSize+margin,0,0,-halfHexagonSize);

            } else {
                if(i == 9){
                        hexagonBottomID = hexagonTopID;
                }
                set.connect(hexagonTopID, ConstraintSet.START, prevDrawableTop, ConstraintSet.END, margin);
                set.connect(hexagonTopID, ConstraintSet.TOP, prevDrawableTop, ConstraintSet.TOP, 0);

                set.connect(hexagonBottomID, ConstraintSet.END, prevDrawableBottom, ConstraintSet.START, margin);
                set.connect(hexagonBottomID, ConstraintSet.TOP, prevDrawableBottom, ConstraintSet.TOP, 0);
            }

            //draw intersections to Hexagon
            drawIntersection(set,hexagonTopID,intersectionTopID++,-halfHexagonSize,-halfHexagonSize,-hexagonSize,0);// top middle
            drawIntersection(set,hexagonTopID,intersectionTopID++,hexagonSize+margin,0,-halfHexagonSize,0); //top right
            drawIntersection(set,hexagonBottomID,intersectionBottomID--,-halfHexagonSize,-halfHexagonSize,0,-hexagonSize); //bottom middle
            drawIntersection(set,hexagonBottomID,intersectionBottomID--,0,hexagonSize+margin,0,-halfHexagonSize); //bottom left

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

}