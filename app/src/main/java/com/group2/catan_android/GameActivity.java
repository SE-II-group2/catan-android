package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
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

import com.group2.catan_android.fragments.HelpFragment;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;
import com.group2.catan_android.fragments.PlayerResourcesFragment;
import com.group2.catan_android.fragments.PlayerScoresFragment;
import com.group2.catan_android.fragments.ButtonsClosedFragment;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ResourceCost;
import com.group2.catan_android.gamelogic.objects.Hexagon;

import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements OnButtonClickListener {

    private ButtonType mLastButtonClicked;

    // hexagon icon measurements
    final static int hexagonHeight = 198;
    final static int hexagonWidth = hexagonHeight / 99 * 86; // 99:86 is the aspect ratio of a hexagon with equal sites
    final static int hexagonHalfHeight = hexagonHeight / 2;
    final static int hexagonQuarterWidth = hexagonWidth / 4;
    final static int hexagonQuarterHeight = hexagonHeight / 4;

    // intersection Size
    static int intersectionSize = 40;
    static int connectionSize = hexagonHalfHeight;

    // view IDs offsets
    final static int CONNECTIONS_OFFSET = 20; // 19 Hexagons + 1 (0 index)
    final static int INTERSECTIONS_OFFSET = 92; // 19 Hexagons + 72 Connections + 1

    Player player = new Player("token","displayName","gameID",Color.GREEN);

    Board board = new Board();
    List<Hexagon> hexagonList = board.getHexagonList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout constraintLayout = findViewById(R.id.main);

        player.adjustResources(new int[]{99,99,99,99,99}); //unlimited resources for testing
        board.addNewRoad(player,0);
        board.setSetupPhase(false);

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

            switch (hexagon.getLocation()) { // save SVG
                case HILLS:
                    hexagonPictures[i] = R.drawable.hexagon_brick_svg;
                    break;
                case FOREST:
                    hexagonPictures[i] = R.drawable.hexagon_wood_svg;
                    break;
                case MOUNTAINS:
                    hexagonPictures[i] = R.drawable.hexagon_stone_svg;
                    break;
                case PASTURE:
                    hexagonPictures[i] = R.drawable.hexagon_sheep_svg;
                    break;
                case FIELDS:
                    hexagonPictures[i] = R.drawable.hexagon_wheat_svg;
                    break;
                default:
                    hexagonPictures[i] = R.drawable.desert_hexagon_svg;
                    break;
            }
        }

        //draw Hexagons
        for (int i = 0; i < hexagonPictures.length; i++) {
            ImageView hexagonView = new ImageView(this);
            hexagonView.setId(ViewCompat.generateViewId());
            hexagonView.setImageDrawable(ContextCompat.getDrawable(this, hexagonPictures[i]));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(hexagonWidth, hexagonHeight);
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;
        }

        //draw Connections
        for (int i = 0; i < connectionViews.length; i++) {
            ImageView connectionView = new ImageView(this);
            connectionView.setId(ViewCompat.generateViewId());
            connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.street));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(connectionSize,connectionSize);
            constraintLayout.addView(connectionView, params);
            connectionViews[i] = connectionView;

            int connectionID = connectionView.getId() - CONNECTIONS_OFFSET;

            connectionView.setOnClickListener(v -> {
                if(mLastButtonClicked == ButtonType.ROAD){
                    if(board.addNewRoad(player,connectionID)){
                        player.adjustResources(ResourceCost.ROAD.getCost());
                        connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.steet_red));
                        connectionView.setColorFilter(player.getColor());
                    } else{
                        Toast.makeText(getApplicationContext(), "Invalid Move " + connectionID, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //draw Intersections
        for (int i = 0; i < 54; i++) {
            ImageView intersectionView = new ImageView(this);
            intersectionView.setId(ViewCompat.generateViewId());
            intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.intersection));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(intersectionSize, intersectionSize);
            constraintLayout.addView(intersectionView, params);
            intersectionViews[i] = intersectionView;
            int intersectionID = intersectionView.getId() - INTERSECTIONS_OFFSET;

            intersectionView.setOnClickListener(v -> {
                if(mLastButtonClicked == ButtonType.VILLAGE) {
                    if (board.addNewVillage(player, intersectionID)) {
                        player.adjustResources(ResourceCost.VILLAGE.getCost());
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.village));
                        intersectionView.setColorFilter(player.getColor());
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Move", Toast.LENGTH_SHORT).show();
                    }
                } else if(mLastButtonClicked == ButtonType.CITY){
                    if (board.addNewCity(player,intersectionID)){
                        player.adjustResources(ResourceCost.CITY.getCost());
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city));
                        intersectionView.setColorFilter(player.getColor());
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Move", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //draw Roll Values
        for (int i = 0; i < hexagonRollValues.length; i++) {
            TextView rollValueView = new TextView(this);
            rollValueView.setId(ViewCompat.generateViewId());
            String rollValue = String.format(Locale.getDefault(), "%d", hexagonRollValues[i]);
            rollValueView.setText(rollValue);

            rollValueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rollValueView.setTextColor(Color.BLACK);
            rollValueView.setGravity(Gravity.CENTER);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(connectionSize,connectionSize);
            constraintLayout.addView(rollValueView, params);
            rollValueViews[i] = rollValueView;
        }

        //constrain the drawables to the right position
        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, layoutWidth, layoutHeight);
        });

        // initialisation of button fragments
        PlayerResourcesFragment playerResourcesFragment = new PlayerResourcesFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.playerResourcesFragment,playerResourcesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.leftButtonsFragment, new ButtonsClosedFragment()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.playerScoresFragment, new PlayerScoresFragment()).commit();

        // endTurn Button
        findViewById(R.id.endTurnButton).setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "End Turn Button", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onButtonClicked(ButtonType button) {
        mLastButtonClicked = button;

        if(button == ButtonType.HELP){
            HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentById(R.id.helpFragment);
            if(helpFragment == null){
                getSupportFragmentManager().beginTransaction().add(R.id.helpFragment, new HelpFragment()).commit();
            } else{
                getSupportFragmentManager().beginTransaction().remove(helpFragment).commit();
            }
        }

        if(button == ButtonType.BUILD){
            HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentById(R.id.helpFragment);
            if(helpFragment != null){
                getSupportFragmentManager().beginTransaction().remove(helpFragment).commit();
            }
        }
    }


    //drawing of board
    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //margins for Hexagons
        int hexStartMargin = layoutWidth / 2 - 6 * hexagonQuarterWidth;
        int hexTopMargin = (layoutHeight / 2) - 2 * hexagonHeight - getStatusBarHeight();
        int secondRowMargin = -10 * hexagonQuarterWidth;
        int thirdRowMargin = secondRowMargin - hexagonWidth;
        int rowHeightDifference = hexagonHalfHeight + hexagonQuarterHeight;

        //margins for drawing connections and intersection
        int[] conMargins = {hexagonQuarterWidth, hexagonQuarterWidth * 3, hexagonHalfHeight + hexagonQuarterHeight, 0};
        int[] intMargins = {0, 0, -hexagonHalfHeight, 0, hexagonHalfHeight}; // last value is the height difference for every second intersection

        //starting values for drawables
        int hexagon = hexagonViews[0].getId();
        int rollValue = rollValueViews[0].getId();
        int intersection = intersectionViews[0].getId();
        int connection = connectionViews[0].getId();
        int prevHexagon = constraintLayout.getId();

        // draw a hexagons with each iteration, and all intersections & connections of a row for every new row
        while (hexagon <= hexagonViews.length) {
            int hexagonsInRow = 3;

            if (hexagon == 1 || hexagon == 4 || hexagon == 8 || hexagon == 13 || hexagon == 17) {//start of new row

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

                        //switch values of start/end and top/bottom margins for the bottom half of board to mirror the top half
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
                }

                //draw first Hexagon in row and all connections and intersections for that row
                drawHexagon(set, hexagon, prevHexagon, hexStartMargin, hexTopMargin);
                connection = drawHorizontalConnections(set, hexagonsInRow, hexagon, connection, conMargins[0], conMargins[1], conMargins[2], conMargins[3]);
                connection = drawVerticalConnections(set, hexagonsInRow, hexagon, connection);
                intersection = drawIntersections(set, hexagonsInRow * 2 + 1, hexagon, intersection, intMargins[2], intMargins[3], intMargins[4]);

            } else { //draw all other hexagons that are not the first in a row
                drawHexagon(set, hexagon, prevHexagon, hexagonWidth, 0);
            }

            //draw RollValues
            drawView(set, hexagon, rollValue++, 0, 0, 0, hexagonQuarterHeight);

            prevHexagon = hexagon++;
        }

        set.applyTo(constraintLayout);
    }

    public void drawView(ConstraintSet set, int hexagon, int intersection, int startMargin, int endMargin, int topMargin, int bottomMargin) {
        set.connect(intersection, ConstraintSet.START, hexagon, ConstraintSet.START, startMargin);
        set.connect(intersection, ConstraintSet.END, hexagon, ConstraintSet.END, endMargin);
        set.connect(intersection, ConstraintSet.TOP, hexagon, ConstraintSet.TOP, topMargin);
        set.connect(intersection, ConstraintSet.BOTTOM, hexagon, ConstraintSet.BOTTOM, bottomMargin);
    }

    public int drawIntersections(ConstraintSet set, int intersectionsInRow, int hexagon, int intersection, int topMargin, int bottomMargin, int offset) {
        int startMargin = 0;
        int endMargin = hexagonWidth;

        for (int i = 0; i < intersectionsInRow; i++) {
            drawView(set, hexagon, intersection++, startMargin, endMargin, topMargin, bottomMargin);

            //move every intersection a half hexagon to the right
            startMargin += hexagonWidth / 2;
            endMargin -= hexagonWidth / 2;

            //add height offset to every second intersection
            if (i % 2 == 0) {
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
            drawConnection(set, hexagon, connection++, startMargin + hexagonWidth * i, endMargin - hexagonWidth * i, -topMargin, -bottomMargin, -30); //top left
            drawConnection(set, hexagon, connection++, endMargin + hexagonWidth * i, startMargin - hexagonWidth * i, -topMargin, -bottomMargin, 30); //top right
        }
        return connection;
    }

    public int drawVerticalConnections(ConstraintSet set, int hexagonsInRow, int hexagon, int connection) {
        for (int i = 0; i < hexagonsInRow + 1; i++) {
            drawConnection(set, hexagon, connection++, -hexagonWidth + hexagonWidth * i, -hexagonWidth * i, 0, 0, 90); //right down
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