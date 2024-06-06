package com.group2.catan_android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.service.UiDrawer;
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
    final int INTERSECTION_SIZE = 40;
    final int CONNECTION_SIZE = HEXAGON_WIDTH_HALF;

    // number of total elements
    final int TOTAL_HEXAGONS = 19;
    final int TOTAL_CONNECTIONS = 72;
    final int TOTAL_INTERSECTIONS = 54;

    // gamelogic and movemaking
    private Player localPlayer;
    private Board board;
    private MoveMaker movemaker;

    // fragments and button listeners
    private PlayerResourcesFragment playerResourcesFragment;
    private PlayerScoresFragment playerScoresFragment;
    private UiDrawer uiDrawer;
    private OnButtonEventListener currentButtonFragmentListener; // listens to which button was clicked in the currently active button fragment
    private ButtonType lastButtonClicked; // stores the last button clicked, the "active button"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        movemaker = MoveMaker.getInstance();
        uiDrawer = UiDrawer.getInstance(GameActivity.this);

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

    public ImageView[] setupViews(ConstraintLayout constraintLayout, int totalViews, int width, int height, int offset, ButtonType button) {
        ImageView[] views = new ImageView[totalViews];

        for (int i = 0; i < totalViews; i++) {
            ImageView view = new ImageView(this);
            view.setId(i + offset + 1);
            int correctID = view.getId() - offset - 1;

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
            constraintLayout.addView(view, params);
            views[i] = view;

            if(button != null){
                setViewOnClickListener(view,correctID,button);
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

    private void createViews(ConstraintLayout constraintLayout) {
        ImageView[] hexagonViews = setupViews(constraintLayout,TOTAL_HEXAGONS,HEXAGON_WIDTH,HEXAGON_HEIGHT,0,null);
        TextView[] rollValueViews = setupTextViews(constraintLayout,TOTAL_HEXAGONS,CONNECTION_SIZE,CONNECTION_SIZE,TOTAL_HEXAGONS);
        ImageView[] robberViews = setupViews(constraintLayout,TOTAL_HEXAGONS,HEXAGON_HEIGHT/3,HEXAGON_HEIGHT/3,TOTAL_HEXAGONS*2,null);
        ImageView[] connectionViews = setupViews(constraintLayout,TOTAL_CONNECTIONS,CONNECTION_SIZE,CONNECTION_SIZE,TOTAL_HEXAGONS*3,ButtonType.ROAD);
        ImageView[] intersectionViews = setupViews(constraintLayout,TOTAL_INTERSECTIONS,INTERSECTION_SIZE,INTERSECTION_SIZE,(TOTAL_HEXAGONS*3 + TOTAL_CONNECTIONS),ButtonType.VILLAGE);

        //constrain views to the right position
        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            uiDrawer.applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, robberViews, layoutWidth, layoutHeight);
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
            uiDrawer.updateUiBoard(board);
        });

        localPlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.localPlayer = player;
            uiDrawer.updateUiPlayerResources(playerResourcesFragment,player);
        });

        gameProgressViewModel.getGameProgressDtoMutableLiveData().observe(this, gameProgressDto ->{
            if(gameProgressDto.getGameMoveDto() instanceof RollDiceDto){
                Toast.makeText(getApplicationContext(), "Dice got rolled: " + ((RollDiceDto)gameProgressDto.getGameMoveDto()).getDiceRoll(), Toast.LENGTH_SHORT).show();
            }
            if(gameProgressDto.getGameMoveDto() instanceof EndTurnMoveDto){
                Toast.makeText(getApplicationContext(), "Turn ended. New active player: "+ ((EndTurnMoveDto)gameProgressDto.getGameMoveDto()).getNextPlayer().getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        });

        playerListViewModel.getPlayerMutableLiveData().observe(this, playerList ->{
            Player activePlayer = playerList.get(0);
            List<Player> tempList = new ArrayList<>(playerList);
            tempList.sort(Comparator.comparingInt(Player::getInGameID));
            uiDrawer.updateUiPlayerScores(playerScoresFragment,tempList,activePlayer);
        });
    }

    private void setViewOnClickListener(View view, int correctID, ButtonType button){
        view.setOnClickListener(v -> {
            if(button == ButtonType.ROAD && button == lastButtonClicked || button == ButtonType.VILLAGE || button == ButtonType.CITY){
            try {
                switch (lastButtonClicked){
                    case ROAD: movemaker.makeMove(new BuildRoadMoveDto(correctID));
                        break;
                    case VILLAGE: movemaker.makeMove(new BuildVillageMoveDto(correctID));
                        break;
                    case CITY: movemaker.makeMove(new BuildCityMoveDto(correctID));
                        break;
                    default: break;
                }
                currentButtonFragmentListener.onButtonEvent(lastButtonClicked);
                lastButtonClicked = null;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

    @Override
    public void onButtonClicked(ButtonType button) {
        currentButtonFragmentListener.onButtonEvent(button);

        if (button == ButtonType.ROAD || button == ButtonType.VILLAGE || button == ButtonType.CITY) {
            uiDrawer.showPossibleMoves(button);
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

}