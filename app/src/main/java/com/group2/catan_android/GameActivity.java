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
import androidx.lifecycle.ViewModelProvider;

import com.group2.catan_android.data.exception.IllegalGameMoveException;
import com.group2.catan_android.data.live.game.AccuseCheatingDto;
import com.group2.catan_android.data.live.game.BuildCityMoveDto;
import com.group2.catan_android.data.live.game.BuildRoadMoveDto;
import com.group2.catan_android.data.live.game.BuildVillageMoveDto;
import com.group2.catan_android.data.live.game.BuyProgressCardDto;
import com.group2.catan_android.data.live.game.EndTurnMoveDto;
import com.group2.catan_android.data.live.game.MoveRobberDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.service.UiDrawer;
import com.group2.catan_android.fragments.HelpFragment;
import com.group2.catan_android.fragments.enums.ClickableElement;
import com.group2.catan_android.fragments.interfaces.OnButtonClickListener;
import com.group2.catan_android.fragments.PlayerResourcesFragment;
import com.group2.catan_android.fragments.PlayerScoresFragment;
import com.group2.catan_android.fragments.ButtonsClosedFragment;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.fragments.interfaces.OnButtonEventListener;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.data.service.MoveMaker;
import com.group2.catan_android.gamelogic.Player;

import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;
import com.group2.catan_android.util.GameEffectManager;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.BoardViewModel;
import com.group2.catan_android.viewmodel.GameProgressViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements OnButtonClickListener {

    // drawables measurements
    private static final int HEXAGON_HEIGHT = 230;
    private static final int HEXAGON_WIDTH = (int) ((float) HEXAGON_HEIGHT / 99 * 86); // 99:86 is the aspect ratio of a hexagon with equal sites
    private static final int HEXAGON_WIDTH_HALF = HEXAGON_HEIGHT / 2;
    private static final int INTERSECTION_SIZE = 40;
    private static final int CONNECTION_SIZE = HEXAGON_WIDTH_HALF;

    private static final int TOTAL_HEXAGONS = 19;
    private static final int TOTAL_CONNECTIONS = 72;
    private static final int TOTAL_INTERSECTIONS = 54;
    private Board board;
    private Player localPlayer;

    private MoveMaker movemaker;


    // fragments and button listeners
    private PlayerResourcesFragment playerResourcesFragment;
    private PlayerScoresFragment playerScoresFragment;
    private UiDrawer uiDrawer;
    private OnButtonEventListener currentButtonFragmentListener; // listens to which button was clicked in the currently active button fragment
    private ButtonType lastButtonClicked; // stores the last button clicked, the "active button"

    private GameEffectManager gameEffectManager;
    private boolean hasRolledSeven = false;

    // List of views
    ImageView[] robberViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        movemaker = MoveMaker.getInstance();
        uiDrawer = new UiDrawer(GameActivity.this);

        gameEffectManager = new GameEffectManager(this);
        gameEffectManager.loadSound(R.raw.pop);
        gameEffectManager.loadSound(R.raw.small_error);
        gameEffectManager.loadSound(R.raw.tap);

        setToFullScreen();

        createViews(constraintLayout);

        createFragments();

        setupViewModels();

        setupEndTurnButton();

        setupDiceRollButton();

        setupAccuseCheatingButton();
    }


    private void setToFullScreen() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void createFragments() {
        playerResourcesFragment = new PlayerResourcesFragment();
        playerScoresFragment = new PlayerScoresFragment();
        ButtonsClosedFragment buttonsClosedFragment = new ButtonsClosedFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.playerResourcesFragment, playerResourcesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.playerScoresFragment, playerScoresFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.leftButtonsFragment, buttonsClosedFragment).commit();
        currentButtonFragmentListener = buttonsClosedFragment;
    }

    private void createViews(ConstraintLayout constraintLayout) {
        ImageView[] hexagonViews = setupViews(constraintLayout, TOTAL_HEXAGONS, HEXAGON_WIDTH, HEXAGON_HEIGHT, 0, null);
        TextView[] rollValueViews = setupRollValueViews(constraintLayout);
        robberViews = setupViews(constraintLayout, TOTAL_HEXAGONS, HEXAGON_HEIGHT / 3, HEXAGON_HEIGHT / 3, TOTAL_HEXAGONS * 2, ClickableElement.ROBBER);
        ImageView[] connectionViews = setupViews(constraintLayout, TOTAL_CONNECTIONS, CONNECTION_SIZE, CONNECTION_SIZE, TOTAL_HEXAGONS * 3, ClickableElement.CONNECTION);
        ImageView[] intersectionViews = setupViews(constraintLayout, TOTAL_INTERSECTIONS, INTERSECTION_SIZE, INTERSECTION_SIZE, (TOTAL_HEXAGONS * 3 + TOTAL_CONNECTIONS), ClickableElement.INTERSECTION);

        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            uiDrawer.applyConstraints(constraintLayout, hexagonViews, intersectionViews, connectionViews, rollValueViews, robberViews, layoutWidth, layoutHeight);
        });
    }

    public ImageView[] setupViews(ConstraintLayout constraintLayout, int totalViews, int width, int height, int offset, ClickableElement clickableElement) {
        ImageView[] views = new ImageView[totalViews];

        for (int i = 0; i < totalViews; i++) {
            ImageView view = new ImageView(this);
            view.setId(i + offset + 1);
            int correctID = view.getId() - offset - 1;

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
            constraintLayout.addView(view, params);
            views[i] = view;

            if (clickableElement != null) {
                setOnClickListener(view, correctID, clickableElement);
            }
        }

        return views;
    }

    private void setOnClickListener(View view, int correctID, ClickableElement clickableElement) {
        view.setOnClickListener(v -> {
            try {
                switch (clickableElement) {
                    case CONNECTION:
                        clickOnConnection(correctID);
                        break;
                    case INTERSECTION:
                        clickOnIntersection(correctID);
                        break;
                    case ROBBER:
                        clickOnRobber(correctID);
                        break;
                    default:
                        break;
                }
                currentButtonFragmentListener.onButtonEvent(lastButtonClicked);
                if (clickableElement != ClickableElement.ROBBER) lastButtonClicked = null;
                gameEffectManager.playSound(R.raw.pop);
            } catch (Exception e) {
                MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
                gameEffectManager.playSound(R.raw.small_error);
                gameEffectManager.doubleVibrate();
            }
        });
    }

    private void clickOnIntersection(int correctID) throws IllegalGameMoveException {
        if(lastButtonClicked != ButtonType.VILLAGE && lastButtonClicked != ButtonType.CITY){
            throw new IllegalGameMoveException("Select the correct button to build a village or city!");
        }

        switch (lastButtonClicked){
            case VILLAGE: movemaker.makeMove(new BuildVillageMoveDto(correctID)); break;
            case CITY: movemaker.makeMove(new BuildCityMoveDto(correctID)); break;
            default: throw new IllegalGameMoveException("Select the correct button to build a village or city!");
        }
    }

    private void clickOnConnection(int correctID) throws IllegalGameMoveException {
        if(lastButtonClicked != ButtonType.ROAD){
            throw new IllegalGameMoveException("Select the correct button to build a road!");
        }
        movemaker.makeMove(new BuildRoadMoveDto(correctID));
    }

    private void clickOnRobber(int correctID) {
        if (hasRolledSeven) {
            movemaker.makeMove(new MoveRobberDto(correctID, true));
            hasRolledSeven = false;
            uiDrawer.setHasRolledSeven(false);
            uiDrawer.removeAllPossibleMovesFromUI();
            return;
        }
        if (lastButtonClicked != ButtonType.ROBBER) {
            uiDrawer.showPossibleMoves(ButtonType.ROBBER);
            lastButtonClicked = ButtonType.ROBBER;
        } else {
            try {
                movemaker.makeMove(new MoveRobberDto(correctID, false));
            } catch (Exception e) {
                lastButtonClicked = null;
                uiDrawer.removeAllPossibleMovesFromUI();
                MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
            }
            lastButtonClicked = null;
            uiDrawer.removeAllPossibleMovesFromUI();
        }
    }

    private TextView[] setupRollValueViews(ConstraintLayout constraintLayout) {
        TextView[] views = new TextView[19];

        for (int i = 0; i < 19; i++) {
            TextView view = new TextView(this);
            view.setId(i + 19 + 1);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            view.setTextColor(Color.BLACK);
            view.setGravity(Gravity.CENTER);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(115, 115);
            constraintLayout.addView(view, params);
            views[i] = view;
        }
        return views;
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
                    if (diceRoll == 7) {
                        hasRolledSeven = true;
                        uiDrawer.setHasRolledSeven(true);
                        uiDrawer.showPossibleMoves(ButtonType.ROBBER);
                    }
                    movemaker.makeMove(new RollDiceDto(diceRoll));
                    movemaker.setHasRolled(true);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupAccuseCheatingButton() {
        findViewById(R.id.accuseCheatingButton).setOnClickListener(v -> {
            movemaker.makeMove(new AccuseCheatingDto());
            uiDrawer.removeAllPossibleMovesFromUI();
            lastButtonClicked = null;
        });
    }

    private void setupViewModels() {
        // viewModels
        BoardViewModel boardViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(BoardViewModel.initializer)).get(BoardViewModel.class);
        LocalPlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(LocalPlayerViewModel.initializer)).get(LocalPlayerViewModel.class);
        PlayerListViewModel playerListViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        GameProgressViewModel gameProgressViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameProgressViewModel.initializer)).get(GameProgressViewModel.class);

        boardViewModel.getBoardMutableLiveData().observe(this, board -> {
            this.board = board;
            uiDrawer.updateUiBoard(board);
        });

        localPlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.localPlayer = player;
            uiDrawer.updateUiPlayerResources(playerResourcesFragment, localPlayer);
        });

        gameProgressViewModel.getGameProgressDtoMutableLiveData().observe(this, gameProgressDto -> {
            if (gameProgressDto.getGameMoveDto() instanceof RollDiceDto) {
                MessageBanner.makeBanner(this, MessageType.INFO, "Dice got rolled: " + ((RollDiceDto) gameProgressDto.getGameMoveDto()).getDiceRoll()).show();
            }
            if (gameProgressDto.getGameMoveDto() instanceof EndTurnMoveDto) {
                if (((EndTurnMoveDto) gameProgressDto.getGameMoveDto()).getNextPlayer().getInGameID() == localPlayer.getInGameID()) {
                    MessageBanner.makeBanner(this, MessageType.INFO, "Your Turn!").show();
                    gameEffectManager.vibrate();
                    gameEffectManager.playSound(R.raw.pop);
                } else {
                    MessageBanner.makeBanner(this, MessageType.INFO, "Turn ended! Next Player: " + ((EndTurnMoveDto) gameProgressDto.getGameMoveDto()).getNextPlayer().getDisplayName()).show();
                    gameEffectManager.vibrate();
                }
            }
        });

        playerListViewModel.getPlayerMutableLiveData().observe(this, playerList ->{
            if(!playerList.isEmpty()) {
                uiDrawer.updateUiPlayerScores(playerScoresFragment, playerList);
            }
        });
    }

    @Override
    public void onButtonClicked(ButtonType button) {
        currentButtonFragmentListener.onButtonEvent(button);
        switch (button) {
            case ROAD:
            case VILLAGE:
            case CITY:
                uiDrawer.showPossibleMoves(button);
                break;
            case HELP:
                showHelpFragment();
                break;
            case EXIT:
                uiDrawer.removeAllPossibleMovesFromUI();
                break;
            case PROGRESS_CARD: {
                try {
                    movemaker.makeMove(new BuyProgressCardDto());
                } catch (Exception e) {
                    MessageBanner.makeBanner(this, MessageType.ERROR, "Can't do that right now!").show();
                }
            }
            break;
            default:
                break;
        }
        gameEffectManager.playSound(R.raw.tap);
        lastButtonClicked = button;
    }

    private void showHelpFragment() {
        HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentById(R.id.helpFragment);
        if (helpFragment == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.helpFragment, new HelpFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().remove(helpFragment).commit();
        }
    }

    public void setCurrentButtonFragmentListener(OnButtonEventListener listener) {
        currentButtonFragmentListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameEffectManager.release();
    }

    public void makeAllRobberViewsClickableComingFromProgressCard() {
        for (ImageView robberView : robberViews) {
            robberView.setVisibility(View.VISIBLE);
            robberView.setOnClickListener(v -> {
                int hexagonID = robberView.getId() - TOTAL_HEXAGONS * 2 - 1;
                moveRobberComingFromProgressCard(hexagonID);
            });
        }
    }

    private void moveRobberComingFromProgressCard(int hexagonID) {
        try {
            // TODO: temporary always true
            MoveRobberDto moveRobberDto = new MoveRobberDto(hexagonID, true);
            UseProgressCardDto useProgressCardDto = new UseProgressCardDto(ProgressCardType.KNIGHT, null, null);
            movemaker.makeMove(moveRobberDto);
            movemaker.makeMove(useProgressCardDto);
        } catch (Exception e) {
            Log.d("Robber", "Fehler: " + e);
            MessageBanner.makeBanner(this, MessageType.ERROR, "An error occurred!" + e.getMessage()).show();
        }
    }

}