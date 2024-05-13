package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.fragments.HelpFragment;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;
import com.group2.catan_android.fragments.PlayerResourcesFragment;
import com.group2.catan_android.fragments.PlayerScoresFragment;
import com.group2.catan_android.fragments.ButtonsClosedFragment;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;
import com.group2.catan_android.viewmodel.ActivePlayerViewModel;
import com.group2.catan_android.viewmodel.BoardViewModel;
import com.group2.catan_android.viewmodel.GameProgressViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements OnButtonClickListener {

    private ButtonType mLastButtonClicked;

    // drawables measurements
    final static int HEXAGON_HEIGHT = 230;
    final static int HEXAGON_WIDTH = (int) ((float) HEXAGON_HEIGHT / 99 * 86); // 99:86 is the aspect ratio of a hexagon with equal sites
    final static int HEXAGON_WIDTH_HALF = HEXAGON_HEIGHT / 2;
    final static int HEXAGON_WIDTH_QUARTER = HEXAGON_WIDTH / 4;
    final static int HEXAGON_HEIGHT_QUARTER = HEXAGON_HEIGHT / 4;
    static int INTERSECTION_SIZE = 40;
    static int connectionSize = HEXAGON_WIDTH_HALF;
    int statusBarHeight;

    // number of total elements
    final static int TOTAL_HEXAGONS = 19;
    final static int TOTAL_CONNECTIONS = 72;
    final static int TOTAL_INTERSECTIONS = 54;

    private BoardViewModel boardViewModel;
    private ActivePlayerViewModel activePlayerViewModel;
    private PlayerListViewModel playerListViewModel;
    private GameProgressViewModel gameProgressViewModel;
    private MoveMaker movemaker;
    PlayerResourcesFragment playerResourcesFragment;
    private List<Player> playerList;
    private Player activePlayer;
    PlayerScoresFragment playerScoresFragment;
    private boolean hasRolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        movemaker = MoveMaker.getInstance();
        //init of arrays to store displayable views
        ImageView[] hexagonViews = new ImageView[TOTAL_HEXAGONS];
        TextView[] rollValueViews = new TextView[TOTAL_HEXAGONS];
        ImageView[] robberViews = new ImageView[TOTAL_HEXAGONS];
        ImageView[] intersectionViews = new ImageView[TOTAL_INTERSECTIONS];
        ImageView[] connectionViews = new ImageView[TOTAL_CONNECTIONS];

        //draw Hexagons with Roll Values and Robber
        for (int i = 0; i < TOTAL_HEXAGONS; i++) {
            ImageView hexagonView = new ImageView(this);
            TextView rollValueView = new TextView(this);
            ImageView robberView = new ImageView(this);

            hexagonView.setId(i + 1); // must start at ID 1, because view with ID 0 is not allowed in Android Studio
            rollValueView.setId(hexagonView.getId() + TOTAL_HEXAGONS);
            robberView.setId(rollValueView.getId() + TOTAL_HEXAGONS);

            ConstraintLayout.LayoutParams paramsHexagon = new ConstraintLayout.LayoutParams(HEXAGON_WIDTH, HEXAGON_HEIGHT);
            ConstraintLayout.LayoutParams paramsRollValues = new ConstraintLayout.LayoutParams(connectionSize,connectionSize);
            ConstraintLayout.LayoutParams paramsRobber = new ConstraintLayout.LayoutParams(HEXAGON_HEIGHT/3,HEXAGON_HEIGHT/3);
            constraintLayout.addView(hexagonView, paramsHexagon);
            constraintLayout.addView(rollValueView, paramsRollValues);
            constraintLayout.addView(robberView, paramsRobber);

            hexagonViews[i] = hexagonView;
            rollValueViews[i] = rollValueView;
            robberViews[i] = robberView;

            //formatting of TextView
            rollValueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rollValueView.setTextColor(Color.BLACK);
            rollValueView.setGravity(Gravity.CENTER);
        }

        //draw Connections
        for (int i = 0; i < TOTAL_CONNECTIONS; i++) {
            ImageView connectionView = new ImageView(this);
            connectionView.setId(i + TOTAL_HEXAGONS*3 + 1);
            connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.street));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(connectionSize,connectionSize);
            constraintLayout.addView(connectionView, params);
            connectionViews[i] = connectionView;

            int connectionID = connectionView.getId() - TOTAL_HEXAGONS*3 - 1;

            connectionView.setOnClickListener(v -> {
                //Toast.makeText(getApplicationContext(), " " + connectionID, Toast.LENGTH_SHORT).show();
                if (mLastButtonClicked == ButtonType.ROAD) {
                    try {
                        movemaker.makeMove(new BuildRoadMoveDto(connectionID));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //draw Intersections
        for (int i = 0; i < TOTAL_INTERSECTIONS; i++) {
            ImageView intersectionView = new ImageView(this);
            intersectionView.setId(i + TOTAL_HEXAGONS*3 + TOTAL_CONNECTIONS + 1);
            intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.intersection));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(INTERSECTION_SIZE, INTERSECTION_SIZE);
            constraintLayout.addView(intersectionView, params);
            intersectionViews[i] = intersectionView;
            int intersectionID = intersectionView.getId() - TOTAL_HEXAGONS*3 - TOTAL_CONNECTIONS - 1;

            intersectionView.setOnClickListener(v -> {
                //Toast.makeText(getApplicationContext(), " " + intersectionID, Toast.LENGTH_SHORT).show();
                if (mLastButtonClicked == ButtonType.VILLAGE) {
                    try {
                        movemaker.makeMove(new BuildVillageMoveDto(intersectionID));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //constrain the views to the right position
        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, robberViews, layoutWidth, layoutHeight);
        });

        // initialisation of button fragments
        playerResourcesFragment = new PlayerResourcesFragment();
        playerScoresFragment = new PlayerScoresFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.playerResourcesFragment,playerResourcesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.leftButtonsFragment, new ButtonsClosedFragment()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.playerScoresFragment, playerScoresFragment).commit();

        boardViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(BoardViewModel.initializer)).get(BoardViewModel.class);
        activePlayerViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(ActivePlayerViewModel.initializer)).get(ActivePlayerViewModel.class);
        playerListViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        gameProgressViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(GameProgressViewModel.initializer)).get(GameProgressViewModel.class);

        boardViewModel.getBoardMutableLiveData().observe(this, this::updateUiBoard);
        activePlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.activePlayer = player;
            updateUiPlayerRessources(activePlayer);
        });
        gameProgressViewModel.getGameProgressDtoMutableLiveData().observe(this, gameProgressDto ->{
            if(gameProgressDto.getGameMoveDto() instanceof RollDiceDto){
                Toast.makeText(getApplicationContext(), "Dice got rolled: " + ((RollDiceDto)gameProgressDto.getGameMoveDto()).getDiceRoll(), Toast.LENGTH_SHORT).show();
            }
            if(gameProgressDto.getGameMoveDto() instanceof EndTurnMoveDto){
                Toast.makeText(getApplicationContext(), "Turn got ended, active player: "+ ((EndTurnMoveDto)gameProgressDto.getGameMoveDto()).getNextPlayer().getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        });
        playerListViewModel.getPlayerMutableLiveData().observe(this, this::updateUiPlayerScores);

        // endTurn Button
        findViewById(R.id.endTurnButton).setOnClickListener(v -> {
            if (!hasRolled) {
                try {
                    Random random = new Random();
                    int diceRoll = random.nextInt(6) + 1 + random.nextInt(6) + 1;
                    movemaker.makeMove(new RollDiceDto(diceRoll));
                    hasRolled = true;

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    movemaker.makeMove(new EndTurnMoveDto());
                    hasRolled = false;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        activePlayer = new Player("test", 0, new int[]{0,0,0,0,0}, 1);
        playerList = new ArrayList<>();
        playerList.add(activePlayer);
        updateUiBoard(new Board());
        //updateUiPlayerScores(playerList);
    }


    public void updateUiBoard(Board board){

        Connection[][] adjacencyMatrix = board.getAdjacencyMatrix();
        Intersection[][] intersections = board.getIntersections();
        List<Hexagon> hexagonList = board.getHexagonList();

        // update connections
        for(int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix[row].length; col++) {

                if(adjacencyMatrix[row][col] instanceof Road){
                    int id = (((Road) adjacencyMatrix[row][col]).getId() + TOTAL_HEXAGONS*3 + 1);

                    ImageView connectionView = findViewById(id);
                    connectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.steet_red));
                    connectionView.setColorFilter((adjacencyMatrix[row][col]).getPlayer().getColor());
                }
            }
        }

        // update intersections
        for(int row = 0; row < intersections.length; row++) {
            for (int col = 0; col < intersections[row].length; col++) {
                if(intersections[row][col] instanceof Building){

                    int id = (((Building) intersections[row][col]).getId() + 3*TOTAL_HEXAGONS + TOTAL_CONNECTIONS + 1);
                    ImageView intersectionView = findViewById(id);

                    if(intersections[row][col].getType() == BuildingType.VILLAGE){
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.village));
                    } else if(intersections[row][col].getType() == BuildingType.CITY){
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city));
                        intersectionView.setScaleX(1.5F);
                        intersectionView.setScaleY(1.5F);
                    }
                    intersectionView.setColorFilter(intersections[row][col].getPlayer().getColor());
                }
            }


        }

        // update hexagons with robber
        for(Hexagon hexagon : hexagonList){
            int hexagonViewID = hexagon.getId() + 1;
            int rollValueViewID = hexagon.getId() + TOTAL_HEXAGONS + 1;
            int robberViewID = rollValueViewID + TOTAL_HEXAGONS;

            ImageView hexagonView = findViewById(hexagonViewID);
            TextView rollValueView = findViewById(rollValueViewID);
            ImageView robberView = findViewById(robberViewID);

            // set hexagon image
            switch(hexagon.getLocation()){
                case HILLS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_hills));
                    break;
                case FOREST:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_forest));
                    break;
                case MOUNTAINS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_mountains));
                    break;
                case PASTURE:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_pasture));
                    break;
                case FIELDS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_fields));
                    break;
                default:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hexagon_desert));
                    break;
            }

            // set robber image and roll values
            rollValueView.setText(String.format(Locale.getDefault(), "%d", hexagon.getRollValue()));
            robberView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.robber));

            // show value or robber
            if(hexagon.isHavingRobber()){
                rollValueView.setVisibility(View.INVISIBLE);
                robberView.setVisibility(View.VISIBLE);
            } else{
                robberView.setVisibility(View.INVISIBLE);
                rollValueView.setVisibility(View.VISIBLE);
            }
        }

    }

    public void updateUiPlayerRessources(Player player){
        playerResourcesFragment.updateResources(player);
    }

    public void updateUiPlayerScores(List<Player> players){
        playerScoresFragment.updateScores(players);
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
    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, ImageView[] robberView, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //margins for Hexagons
        int hexStartMargin = layoutWidth / 2 - 6 * HEXAGON_WIDTH_QUARTER;
        int hexTopMargin = (layoutHeight / 2) - 2 * HEXAGON_HEIGHT - statusBarHeight;
        int secondRowMargin = -2 * HEXAGON_WIDTH -2 * HEXAGON_WIDTH_QUARTER -2; // -2 to make up some integer rounding
        int thirdRowMargin = secondRowMargin - HEXAGON_WIDTH;
        int rowHeightDifference = HEXAGON_WIDTH_HALF + HEXAGON_HEIGHT_QUARTER;

        //margins for drawing connections and intersection
        int[] conMargins = {HEXAGON_WIDTH_QUARTER, HEXAGON_WIDTH_QUARTER * 3, HEXAGON_WIDTH_HALF + HEXAGON_HEIGHT_QUARTER, 0};
        int[] intMargins = {0, 0, -HEXAGON_WIDTH_HALF, 0, HEXAGON_WIDTH_HALF}; // last value is the height difference for every second intersection

        //starting values for drawables
        int hexagon = hexagonViews[0].getId();
        int rollValue = rollValueViews[0].getId();
        int robber = robberView[0].getId();
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
                drawHexagon(set, hexagon, prevHexagon, HEXAGON_WIDTH, 0);
            }

            //draw RollValues & Robber
            drawView(set, hexagon, rollValue++, 0, 0, 0, HEXAGON_HEIGHT_QUARTER);
            drawView(set, hexagon, robber++, 0, 0, 0, HEXAGON_WIDTH/3);

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
        int endMargin = HEXAGON_WIDTH;

        for (int i = 0; i < intersectionsInRow; i++) {
            drawView(set, hexagon, intersection++, startMargin, endMargin, topMargin, bottomMargin);

            //move every intersection a half hexagon to the right
            startMargin += HEXAGON_WIDTH / 2;
            endMargin -= HEXAGON_WIDTH / 2;

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