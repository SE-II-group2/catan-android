package com.group2.catan_android.data.service;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Set;

public class ViewConstrainer {

    // drawables measurements
    final int HEXAGON_HEIGHT = 230;
    final int HEXAGON_WIDTH = (int) ((float) HEXAGON_HEIGHT / 99 * 86); // 99:86 is the aspect ratio of a hexagon with equal sites
    final int HEXAGON_WIDTH_HALF = HEXAGON_HEIGHT / 2;
    final int HEXAGON_WIDTH_QUARTER = HEXAGON_WIDTH / 4;
    final int HEXAGON_HEIGHT_QUARTER = HEXAGON_HEIGHT / 4;

    //margins
    int hexStartMargin;
    int hexTopMargin;
    int secondRowMargin;
    int thirdRowMargin;
    int rowHeightDifference;
    int[] conMargins; //connectionMargins
    int[] intMargins; //intersectionMargins

    //drawable objects
    int hexagon;
    int rollValue;
    int robber;
    int intersection;
    int connection;
    int prevHexagon;
    int hexagonsInRow;
    ConstraintSet set;


    public void constrainViews(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, ImageView[] robberViews, int layoutWidth, int layoutHeight){
        set = new ConstraintSet();
        set.clone(constraintLayout);

        setupMargins(layoutWidth,layoutHeight);
        setObjectsStartingValues(hexagonViews,intersectionViews,connectionViews,rollValueViews,robberViews,constraintLayout);

        constrainBoard(hexagonViews);

        set.applyTo(constraintLayout);
    }

    private void constrainBoard(ImageView[] hexagonViews) {
        while (hexagon <= hexagonViews.length) { // constrain a hexagon with each iteration, and all intersections & connections of a row for every new row

            hexagonsInRow = 3;

            if (hexagon == 1 || hexagon == 4 || hexagon == 8 || hexagon == 13 || hexagon == 17) { //start of new row
                drawRows();
                drawFirstHexagonInRow();
            } else { //draw all other hexagons that are not the first in a row
                drawHexagon(set, hexagon, prevHexagon, HEXAGON_WIDTH, 0);
            }

            constrainHexagonContent();
            prevHexagon = hexagon++;
        }
    }

    private void drawFirstHexagonInRow() {
        drawHexagon(set, hexagon, prevHexagon, hexStartMargin, hexTopMargin);
        connection = drawHorizontalConnections(set, hexagonsInRow, hexagon, connection, conMargins[0], conMargins[1], conMargins[2], conMargins[3]);
        connection = drawVerticalConnections(set, hexagonsInRow, hexagon, connection);
        intersection = drawIntersections(set, hexagonsInRow * 2 + 1, hexagon, intersection, intMargins[2], intMargins[3], intMargins[4]);
    }

    private void drawRows() {
        switch (hexagon) {
            case 4: // second row
                hexagonsInRow = 4;
                hexStartMargin = secondRowMargin;
                hexTopMargin = rowHeightDifference;
                break;
            case 8: // third row
                hexagonsInRow = 5;
                hexStartMargin = thirdRowMargin;
                connection = drawHorizontalConnections(set, hexagonsInRow, hexagon, connection, conMargins[0], conMargins[1], conMargins[2], conMargins[3]);
                intersection = drawIntersections(set, hexagonsInRow * 2 + 1, hexagon, intersection, intMargins[2], intMargins[3], intMargins[4]);
                conMargins = switchMargins(conMargins);
                intMargins = switchMargins(intMargins);
                intMargins[4] = -intMargins[4];
                break;
            case 13: // fourth row
                hexagonsInRow = 4;
                break;
            case 17: // fifth row
                hexStartMargin = secondRowMargin; // secondRowMargin due to bottom half being mirrored
                break;
            default:
                break;
        }
    }

    private void constrainHexagonContent() {
        drawView(set, hexagon, rollValue++, 0, 0, 0, HEXAGON_HEIGHT_QUARTER);
        drawView(set, hexagon, robber++, 0, 0, 0, HEXAGON_WIDTH / 3);
    }

    private void setObjectsStartingValues(ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, ImageView[] robberViews, ConstraintLayout constraintLayout) {
        hexagon = hexagonViews[0].getId();
        rollValue = rollValueViews[0].getId();
        robber = robberViews[0].getId();
        intersection = intersectionViews[0].getId();
        connection = connectionViews[0].getId();
        prevHexagon = constraintLayout.getId();
    }

    private void setupMargins(int layoutWidth, int layoutHeight) {
        hexStartMargin = layoutWidth / 2 - 6 * HEXAGON_WIDTH_QUARTER;
        hexTopMargin = (layoutHeight / 2) - 2 * HEXAGON_HEIGHT;
        secondRowMargin = -2 * HEXAGON_WIDTH - 2 * HEXAGON_WIDTH_QUARTER - 2; // -2 to make up some integer rounding
        thirdRowMargin = secondRowMargin - HEXAGON_WIDTH;
        rowHeightDifference = HEXAGON_WIDTH_HALF + HEXAGON_HEIGHT_QUARTER;
        conMargins = new int[]{HEXAGON_WIDTH_QUARTER, HEXAGON_WIDTH_QUARTER * 3, HEXAGON_WIDTH_HALF + HEXAGON_HEIGHT_QUARTER, 0};
        intMargins = new int[]{0, 0, -HEXAGON_WIDTH_HALF, 0, HEXAGON_WIDTH_HALF}; // last value is the height difference for every second intersection
    }

    public void drawView(ConstraintSet set, int hexagon, int intersection, int startMargin, int endMargin, int topMargin, int bottomMargin) {
        set.connect(intersection, ConstraintSet.START, hexagon, ConstraintSet.START, startMargin);
        set.connect(intersection, ConstraintSet.END, hexagon, ConstraintSet.END, endMargin);
        set.connect(intersection, ConstraintSet.TOP, hexagon, ConstraintSet.TOP, topMargin);
        set.connect(intersection, ConstraintSet.BOTTOM, hexagon, ConstraintSet.BOTTOM, bottomMargin);
    }

    public int drawIntersections(ConstraintSet set, int intersectionsInRow, int hexagon, int intersection, int topMargin, int bottomMargin, int offset) {
        int startMargin = 0;
        int endMargin = HEXAGON_WIDTH;

        for (int i = 0; i < intersectionsInRow; i++) {
            drawView(set, hexagon, intersection++, startMargin, endMargin, topMargin, bottomMargin);

            startMargin += HEXAGON_WIDTH / 2; //move every intersection a half hexagon to the right
            endMargin -= HEXAGON_WIDTH / 2;

            if (i % 2 == 0) { //add height offset to every second intersection
                topMargin -= offset;
            } else {
                topMargin += offset;
            }
        }
        return intersection;
    }

    public void drawConnection(ConstraintSet set, int hexagon, int connection, int startMargin, int endMargin, int topMargin, int bottomMargin, int rotation) {
        set.setRotation(connection, rotation);
        drawView(set, hexagon, connection, startMargin, endMargin, topMargin, bottomMargin);
    }

    public int drawHorizontalConnections(ConstraintSet set, int hexagonsInRow, int hexagon, int connection, int startMargin, int endMargin, int topMargin, int bottomMargin) {
        for (int i = 0; i < hexagonsInRow; i++) {
            drawConnection(set, hexagon, connection++, startMargin + HEXAGON_WIDTH * i, endMargin - HEXAGON_WIDTH * i, -topMargin, -bottomMargin, -30); //top left
            drawConnection(set, hexagon, connection++, endMargin + HEXAGON_WIDTH * i, startMargin - HEXAGON_WIDTH * i, -topMargin, -bottomMargin, 30); //top right
        }
        return connection;
    }

    public int drawVerticalConnections(ConstraintSet set, int hexagonsInRow, int hexagon, int connection) {
        for (int i = 0; i < hexagonsInRow + 1; i++) {
            drawConnection(set, hexagon, connection++, -HEXAGON_WIDTH + HEXAGON_WIDTH * i, -HEXAGON_WIDTH * i, 0, 0, 90); //right down
        }
        return connection;
    }

    public void drawHexagon(ConstraintSet set, int hexagon, int prevHexagon, int startMargin, int topMargin) {
        set.connect(hexagon, ConstraintSet.START, prevHexagon, ConstraintSet.START, startMargin);
        set.connect(hexagon, ConstraintSet.TOP, prevHexagon, ConstraintSet.TOP, topMargin);
    }

    public int[] switchMargins(int[] margins) {
        int temp = margins[0];
        margins[0] = margins[1];
        margins[1] = temp;
        temp = margins[2];
        margins[2] = margins[3];
        margins[3] = temp;
        return margins;
    }

}
