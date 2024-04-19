package com.group2.catan_android;

import android.graphics.drawable.Drawable;
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

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams((int) (hexagonSize), (int) (hexagonSize));
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;
        }

        ImageView[] intersectionViews = new ImageView[60];

        for (int i = 0; i < 60; i++){
            ImageView intersectionView = new ImageView(this);
            intersectionView.setId(ViewCompat.generateViewId());
            intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_drawable));

            Log.d("hex","viewID: " + intersectionView.getId());

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams((int) (intersectionSize), (int) (intersectionSize));
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

        int margin = -24;
        int thirdRow = (layoutWidth / 2) - (2*hexagonSize + halfHexagonSize) - (2*margin);
        int secondRow = thirdRow + halfHexagonSize + (margin/2);
        int firstRow = thirdRow + hexagonSize + margin;
        int firstTop = (layoutHeight/2) - (2*hexagonSize+halfHexagonSize) - (4*margin) - getStatusBarHeight();

        int prevDrawable = ConstraintSet.PARENT_ID;
        int firstDrawableInRow = hexagonViews[0].getId();

        int intersectionID = intersectionViews[0].getId()-hexagonViews.length-1;

        for (int i = 0; i < hexagonViews.length; i++) {
            ImageView hexagonView = hexagonViews[i];
            ImageView intersectionView = intersectionViews[intersectionID];

            // first hexagons in each line
            if (i == 0 || i == 3 || i == 7 || i == 12 || i == 16) {// start of new line 3-4-5-4-3

                switch(i) {
                    case 0:
                    case 16:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, firstRow);
                        break;
                    case 3:
                    case 12:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, secondRow);

                        break;
                    case 7:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, thirdRow);
                        break;
                }

                //draw Intersections to Hexagon
                drawIntersection(set,hexagonView,intersectionViews[intersectionID],0,hexagonSize+margin,-halfHexagonSize,0); //topLeft
                intersectionID++;
                drawIntersection(set,hexagonView,intersectionViews[intersectionID],-halfHexagonSize,-halfHexagonSize,-hexagonSize,0); //topMiddle
                intersectionID++;
                drawIntersection(set,hexagonView,intersectionViews[intersectionID],hexagonSize+margin,0,-halfHexagonSize,0); //topRight
                intersectionID++;

                firstDrawableInRow = hexagonView.getId(); // beginning of current line

                // if not the first-element in the FIRST line consider topMargin to the hex above
                if (i != 0) {
                    set.connect(hexagonView.getId(), ConstraintSet.TOP, prevDrawable, ConstraintSet.BOTTOM, margin*2);
                }
            } else {
                set.connect(hexagonView.getId(), ConstraintSet.START, prevDrawable, ConstraintSet.END, margin);
                set.connect(hexagonView.getId(), ConstraintSet.TOP, firstDrawableInRow, ConstraintSet.TOP, 0);

                //dot on top of Hexagon
                drawIntersection(set,hexagonView,intersectionViews[intersectionID],-halfHexagonSize,-halfHexagonSize,-hexagonSize,0);
                intersectionID++;

                //dot on the right of Hexagon
                drawIntersection(set,hexagonView,intersectionViews[intersectionID],hexagonSize+margin,0,-halfHexagonSize,0);
                intersectionID++;
            }

            // first-line first-element constraint top
            if (i == 0) {
                set.connect(hexagonView.getId(), ConstraintSet.TOP, prevDrawable, ConstraintSet.TOP, firstTop);

            }

            prevDrawable = hexagonView.getId();
        }

        set.applyTo(constraintLayout);
    }

    public void drawIntersection(ConstraintSet set, ImageView hexagonView, ImageView intersectionView, int startMargin, int endMargin, int topMargin, int bottomMargin){
        set.connect(intersectionView.getId(), ConstraintSet.START, hexagonView.getId(), ConstraintSet.START, startMargin);
        set.connect(intersectionView.getId(), ConstraintSet.END, hexagonView.getId(), ConstraintSet.END, endMargin);
        set.connect(intersectionView.getId(), ConstraintSet.TOP, hexagonView.getId(), ConstraintSet.TOP, topMargin);
        set.connect(intersectionView.getId(), ConstraintSet.BOTTOM, hexagonView.getId(), ConstraintSet.BOTTOM, bottomMargin);
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