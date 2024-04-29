package com.group2.catan_android;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class demoboard extends AppCompatActivity {

    // hexagon icon measurements
    static int height = 200;
    static int width = 200;

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

        //TODO: Insert these drawables according to the playing field from the server (not ready yet)
        int[] ids = {
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg
        };

        ImageView[] imageViews = new ImageView[ids.length];

        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(ViewCompat.generateViewId());
            imageView.setImageDrawable(ContextCompat.getDrawable(this, ids[i]));
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams((int) (width), (int) (height));
            constraintLayout.addView(imageView, params);
            imageViews[i] = imageView;
        }


        //getSupportFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_2()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, imageViews, layoutWidth, layoutHeight);
            });


        getSupportFragmentManager().beginTransaction().replace(R.id.leftButtons, new leftButtons_1()).addToBackStack(null).commit();



    }

    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] imageViews, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //TODO: Maybe revise the margins sometime
        int topMargin = -20;
        int sideMargin = 0;
        int thirdRow = (int) Math.round((layoutWidth / 2) - (2.5 * width));
        int secondRow = (int) Math.round(thirdRow + (width / 2));
        int firstRow = (int) Math.round(thirdRow + width);
        int firstTop = (int) Math.round((layoutHeight / 2) - (2.5 * height) + (topMargin));

        int prevDrawable = ConstraintSet.PARENT_ID;
        int firstDrawableInRow = imageViews[0].getId();

        for (int i = 0; i < imageViews.length; i++) {
            ImageView imageView = imageViews[i];

            // first hexagons in each line
            if (i == 0 || i == 3 || i == 7 || i == 12 || i == 16) { // start of new line 3-4-5-4-3
                switch (i) {
                    case 0:
                    case 16:
                        set.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, firstRow);
                        break;
                    case 3:
                    case 12:
                        set.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, secondRow);
                        break;
                    case 7:
                        set.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, thirdRow);
                        break;
                }

                firstDrawableInRow = imageView.getId(); // beginning of current line

                // if not the first-element in the FIRST line consider topMargin to the hex above
                if (i != 0) {
                    set.connect(imageView.getId(), ConstraintSet.TOP, prevDrawable, ConstraintSet.BOTTOM, topMargin);
                }
            } else {
                set.connect(imageView.getId(), ConstraintSet.START, prevDrawable, ConstraintSet.END, sideMargin);
                set.connect(imageView.getId(), ConstraintSet.TOP, firstDrawableInRow, ConstraintSet.TOP, 0);
            }

            // first-line first-element constraint top
            if (i == 0) {
                set.connect(imageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, firstTop);
            }

            prevDrawable = imageView.getId();
        }

        set.applyTo(constraintLayout);
    }
}