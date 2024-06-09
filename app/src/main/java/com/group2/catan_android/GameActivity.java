package com.group2.catan_android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import com.group2.catan_android.fragments.interfaces.OnButtonEventListener;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.enums.ResourceCost;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.ActivePlayerViewModel;
import com.group2.catan_android.viewmodel.BoardViewModel;
import com.group2.catan_android.viewmodel.GameProgressViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

// fixme avoid making this class a god class. it is also the only one implementing the OnButtonClickListener, handling events from the fragments
public class GameActivity extends AppCompatActivity implements OnButtonClickListener {

    // drawables measurements
    final int HEXAGON_HEIGHT = 230;
    final int HEXAGON_WIDTH = (int) ((float) HEXAGON_HEIGHT / 99 * 86); // 99:86 is the aspect ratio of a hexagon with equal sites
    final int HEXAGON_WIDTH_HALF = HEXAGON_HEIGHT / 2;
    final int HEXAGON_WIDTH_QUARTER = HEXAGON_WIDTH / 4;
    final int HEXAGON_HEIGHT_QUARTER = HEXAGON_HEIGHT / 4;
    final int INTERSECTION_SIZE = 40;
    final int CONNECTION_SIZE = HEXAGON_WIDTH_HALF;

    // number of total elements
    final int TOTAL_HEXAGONS = 19;
    final int TOTAL_CONNECTIONS = 72;
    final int TOTAL_INTERSECTIONS = 54;

    // storing of possible moves
    private List<ImageView> possibleRoads;
    private List<ImageView> possibleVillages;
    private List<ImageView> possibleCities;
    private boolean showingPossibleRoads = false;
    private boolean showingPossibleVillages = false;
    private boolean showingPossibleCities = false;

    // gamelogic and movemaking
    private Player localPlayer;
    private Board board;
    private MoveMaker movemaker;

    // fragments and button listeners
    private PlayerResourcesFragment playerResourcesFragment;
    private PlayerScoresFragment playerScoresFragment;
    private OnButtonEventListener currentButtonFragmentListener; // listens to which button was clicked in the currently active button fragment
    private ButtonType lastButtonClicked; // stores the last button clicked, the "active button"

    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        movemaker = MoveMaker.getInstance();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setToFullScreen();

        createViews(constraintLayout);

        createFragments();

        setupViewModels();

        setupEndTurnButton();

        setupDiceRollButton();
    }

    private void setToFullScreen() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void createFragments() {
        playerResourcesFragment = new PlayerResourcesFragment();
        playerScoresFragment = new PlayerScoresFragment();
        ButtonsClosedFragment buttonsClosedFragment = new ButtonsClosedFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.playerResourcesFragment,playerResourcesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.playerScoresFragment, playerScoresFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.leftButtonsFragment, buttonsClosedFragment).commit();
        currentButtonFragmentListener = buttonsClosedFragment;
    }

    private void createViews(ConstraintLayout constraintLayout) {
        ImageView[] hexagonViews = setupViews(constraintLayout,TOTAL_HEXAGONS,HEXAGON_WIDTH,HEXAGON_HEIGHT,0,null);
        TextView[] rollValueViews = setupTextViews(constraintLayout,TOTAL_HEXAGONS,CONNECTION_SIZE,CONNECTION_SIZE,TOTAL_HEXAGONS);
        ImageView[] robberViews = setupViews(constraintLayout,TOTAL_HEXAGONS,HEXAGON_HEIGHT/3,HEXAGON_HEIGHT/3,TOTAL_HEXAGONS*2,null);
        ImageView[] connectionViews = setupViews(constraintLayout,TOTAL_CONNECTIONS,CONNECTION_SIZE,CONNECTION_SIZE,TOTAL_HEXAGONS*3,ButtonType.ROAD);
        ImageView[] intersectionViews = setupViews(constraintLayout,TOTAL_INTERSECTIONS,INTERSECTION_SIZE,INTERSECTION_SIZE,(TOTAL_HEXAGONS*3 + TOTAL_CONNECTIONS),ButtonType.VILLAGE);
        possibleRoads = new ArrayList<>();
        possibleVillages = new ArrayList<>();
        possibleCities = new ArrayList<>();

        //constrain views to the right position
        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, robberViews, layoutWidth, layoutHeight);
        });
    }

    private void setupEndTurnButton() {
        findViewById(R.id.endTurnButton).setOnClickListener(v -> {
            if (movemaker.hasRolled()) {
                try {
                    movemaker.makeMove(new EndTurnMoveDto());
                    movemaker.setHasRolled(false);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupDiceRollButton() {
        findViewById(R.id.diceRollButton).setOnClickListener(v -> {
            if (!movemaker.hasRolled() && !board.isSetupPhase()) {
                try {
                    Random random = new Random();
                    int diceRoll = random.nextInt(6) + 1 + random.nextInt(6) + 1;
                    movemaker.makeMove(new RollDiceDto(diceRoll));
                    movemaker.setHasRolled(true);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupViewModels() {
        // viewModels
        BoardViewModel boardViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(BoardViewModel.initializer)).get(BoardViewModel.class);
        ActivePlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(ActivePlayerViewModel.initializer)).get(ActivePlayerViewModel.class);
        PlayerListViewModel playerListViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        GameProgressViewModel gameProgressViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameProgressViewModel.initializer)).get(GameProgressViewModel.class);

        boardViewModel.getBoardMutableLiveData().observe(this, board -> {
            this.board = board;
            updateUiBoard(board);
        });

        localPlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.localPlayer = player;
            updateUiPlayerResources(localPlayer);
        });

        gameProgressViewModel.getGameProgressDtoMutableLiveData().observe(this, gameProgressDto ->{
            if(gameProgressDto.getGameMoveDto() instanceof RollDiceDto){
                Toast.makeText(getApplicationContext(), "Dice got rolled: " + ((RollDiceDto)gameProgressDto.getGameMoveDto()).getDiceRoll(), Toast.LENGTH_SHORT).show();
            }
            if(gameProgressDto.getGameMoveDto() instanceof EndTurnMoveDto){
                if(((EndTurnMoveDto) gameProgressDto.getGameMoveDto()).getNextPlayer().getInGameID() == localPlayer.getInGameID()) {
                    MessageBanner.makeBanner(this, MessageType.INFO, "Your Turn!").show();
                    vibrateIfAvailable();
                }
                else
                    MessageBanner.makeBanner(this, MessageType.INFO, "Turn ended! Next Player: " + ((EndTurnMoveDto)gameProgressDto.getGameMoveDto()).getNextPlayer().getDisplayName()).show();
            }
        });

        playerListViewModel.getPlayerMutableLiveData().observe(this, data ->{
            List<Player> tempList = new ArrayList<>(data);
            tempList.sort(Comparator.comparingInt(Player::getInGameID));
            updateUiPlayerScores(tempList);
        });
    }

    private ImageView[] setupViews(ConstraintLayout constraintLayout, int totalViews, int width, int height, int offset, ButtonType type) {
        ImageView[] views = new ImageView[totalViews];

        for (int i = 0; i < totalViews; i++) {
            ImageView view = new ImageView(this);
            view.setId(i + offset + 1);
            int correctID = view.getId() - offset - 1;

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
            constraintLayout.addView(view, params);
            views[i] = view;

            if(type != null){
                setViewOnClickListener(view,correctID,type);
            }
        }

        return views;
    }

    private TextView[] setupTextViews(ConstraintLayout constraintLayout, int totalViews, int width, int height, int offset) {
        TextView[] views = new TextView[totalViews];

        for (int i = 0; i < totalViews; i++) {
            TextView view = new TextView(this);
            view.setId(i + offset + 1);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            view.setTextColor(Color.BLACK);
            view.setGravity(Gravity.CENTER);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
            constraintLayout.addView(view, params);
            views[i] = view;
        }
        return views;
    }

    private void setViewOnClickListener(View view, int correctID, ButtonType type){
        view.setOnClickListener(v -> {
            if (lastButtonClicked == type) {
                try {
                    switch (type){
                        case ROAD: movemaker.makeMove(new BuildRoadMoveDto(correctID));
                        break;
                        case VILLAGE: movemaker.makeMove(new BuildVillageMoveDto(correctID));
                        break;
                    }
                    currentButtonFragmentListener.onButtonEvent(type);
                } catch (Exception e) {
                    MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
                }
            }
        });
    }

    @Override
    public void onButtonClicked(ButtonType button) {
        currentButtonFragmentListener.onButtonEvent(button);

        if (button == ButtonType.ROAD || button == ButtonType.VILLAGE || button == ButtonType.CITY) {
            showPossibleMoves(button);
        }

        if (button == ButtonType.HELP) {
            HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentById(R.id.helpFragment);
            if (helpFragment == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.helpFragment, new HelpFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().remove(helpFragment).commit();
            }
        }

        lastButtonClicked = button;
    }

    public void setCurrentButtonFragmentListener(OnButtonEventListener listener) {
        currentButtonFragmentListener = listener;
    }

    //TODO: move UI drawing to separate UiDrawer class and extract methods
    public void updateUiBoard(Board board){
        removePossibleMovesFromUI(possibleRoads);
        removePossibleMovesFromUI(possibleVillages);
        removePossibleMovesFromUI(possibleCities,R.drawable.village);
        showingPossibleRoads = showingPossibleVillages = showingPossibleCities = false;

        updatePossibleMoves();

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
                    connectionView.clearAnimation();
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
                        intersectionView.clearAnimation();
                    } else if(intersections[row][col].getType() == BuildingType.CITY){
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city));
                        intersectionView.setScaleX(1.5F);
                        intersectionView.setScaleY(1.5F);
                        intersectionView.clearAnimation();
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

        lastButtonClicked = null;
    }

    public void updateUiPlayerResources(Player player){
        playerResourcesFragment.updateResources(player);
    }

    public void updateUiPlayerScores(List<Player> players){
        playerScoresFragment.updateScores(players);
    }

    public void updatePossibleMoves(){
        possibleRoads.clear();
        possibleVillages.clear();
        possibleCities.clear();

        updatePossibleRoads();
        updatePossibleVillagesOrCities();
    }

    private void updatePossibleVillagesOrCities() {
        for(int intersectionsID = 0; intersectionsID < TOTAL_INTERSECTIONS; intersectionsID++) {
            if(board.checkPossibleVillage(localPlayer, intersectionsID)){
                int id = (intersectionsID + TOTAL_HEXAGONS * 3 + TOTAL_CONNECTIONS + 1);
                ImageView intersectionView = findViewById(id);
                possibleVillages.add(intersectionView);
            }

            if(board.checkPossibleCity(localPlayer, intersectionsID)){
                int id = (intersectionsID + TOTAL_HEXAGONS * 3 + TOTAL_CONNECTIONS + 1);
                ImageView intersectionView = findViewById(id);
                possibleCities.add(intersectionView);
            }
        }
    }

    private void updatePossibleRoads() {
        for(int connectionID = 0; connectionID < TOTAL_CONNECTIONS; connectionID++) {
            if(board.checkPossibleRoad(localPlayer, connectionID)){
                int id = (connectionID + TOTAL_HEXAGONS * 3 + 1);
                ImageView connectionView = findViewById(id);
                possibleRoads.add(connectionView);
            }
        }
    }

    public void showPossibleMoves(ButtonType button) {
        // remove all possible moves and then draw the ones according to pressed button
        removePossibleMovesFromUI(possibleRoads);
        removePossibleMovesFromUI(possibleVillages);
        removePossibleMovesFromUI(possibleCities,R.drawable.village);

            switch (button) {
                case ROAD:
                    showingPossibleVillages = showingPossibleCities = false;
                    showingPossibleRoads = drawPossibleMovesToUI(showingPossibleRoads,possibleRoads, R.drawable.possible_street, button);
                    break;
                case VILLAGE:
                    showingPossibleRoads = showingPossibleCities = false;
                    showingPossibleVillages = drawPossibleMovesToUI(showingPossibleVillages, possibleVillages, R.drawable.village, button);
                    break;
                case CITY:
                    showingPossibleRoads = showingPossibleVillages = false;
                    showingPossibleCities = drawPossibleMovesToUI(showingPossibleCities, possibleCities, R.drawable.city, button);
                    break;
            }
    }

    public boolean drawPossibleMovesToUI(boolean showingMoves, List<ImageView> possibleMoveViews, int drawable, ButtonType button){

        if(showingMoves){
            if(button == ButtonType.CITY){
                removePossibleMovesFromUI(possibleMoveViews,R.drawable.village);
            } else{
                removePossibleMovesFromUI(possibleMoveViews);
            }
            return false;
        }

        boolean resourceSufficient = true;
        switch(button){
            case ROAD: resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.ROAD.getCost()); break;
            case VILLAGE: resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost()); break;
            case CITY: resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.CITY.getCost()); break;
        }

        drawPossibleMoves(possibleMoveViews,resourceSufficient,drawable);
        return true;
    }

    private void drawPossibleMoves(List<ImageView> possibleMoveViews, boolean resourceSufficient, int drawable) {
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(ContextCompat.getDrawable(this, drawable));
            possibleMoveView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation));
            if(resourceSufficient || board.isSetupPhase()){
                possibleMoveView.setColorFilter(Color.WHITE);
            } else{
                possibleMoveView.setColorFilter(Color.BLACK);
            }
        }
    }

    public void removePossibleMovesFromUI(List<ImageView> possibleMoveViews){
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(null);
        }
    }

    public void removePossibleMovesFromUI(List<ImageView> possibleMoveViews,int drawable){
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(ContextCompat.getDrawable(this, drawable));
            possibleMoveView.setColorFilter(localPlayer.getColor());
            possibleMoveView.clearAnimation();
        }
    }

    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, ImageView[] robberView, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        //margins for Hexagons
        int hexStartMargin = layoutWidth / 2 - 6 * HEXAGON_WIDTH_QUARTER;
        int hexTopMargin = (layoutHeight / 2) - 2 * HEXAGON_HEIGHT;
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

    private void vibrateIfAvailable(){
        if(vibrator != null && vibrator.hasVibrator())
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}