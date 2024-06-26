package com.group2.catan_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
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
import com.group2.catan_android.data.live.game.GameOverDto;
import com.group2.catan_android.data.live.game.MoveRobberDto;
import com.group2.catan_android.data.live.game.RollDiceDto;
import com.group2.catan_android.data.live.game.UseProgressCardDto;
import com.group2.catan_android.data.service.GameController;
import com.group2.catan_android.data.service.StompManager;
import com.group2.catan_android.data.service.UiDrawer;
import com.group2.catan_android.fragments.ButtonsOpenFragment;
import com.group2.catan_android.fragments.HelpFragment;
import com.group2.catan_android.fragments.TradeOfferFragment;
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
import com.group2.catan_android.util.ShakeListener;
import com.group2.catan_android.viewmodel.LocalPlayerViewModel;
import com.group2.catan_android.util.GameEffectManager;
import com.group2.catan_android.util.MessageBanner;
import com.group2.catan_android.util.MessageType;
import com.group2.catan_android.viewmodel.BoardViewModel;
import com.group2.catan_android.viewmodel.GameProgressViewModel;
import com.group2.catan_android.viewmodel.PlayerListViewModel;
import com.group2.catan_android.viewmodel.TradeViewModel;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;

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
    private ButtonsClosedFragment baseMenuFragment;
    private ButtonsOpenFragment buildMenuFragment;
    private TradeOfferFragment tradeOfferFragment;
    private UiDrawer uiDrawer;
    private OnButtonEventListener currentButtonFragmentListener; // listens to which button was clicked in the currently active button fragment
    private ButtonType lastButtonClicked; // stores the last button clicked, the "active button"

    private GameEffectManager gameEffectManager;
    private ShakeListener mShakeListener;
    private boolean mUsingShakeListener;
    private boolean hasRolledSeven = false;
    private boolean hasUsedProgressCard = false;
    private ImageView endTurnButton;
    private ImageView accuseCheatingButton;
    private ImageView rollDiceButton;
    private Random random;

    private boolean buildMenuOpened;


    CompositeDisposable disposable;

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
        gameEffectManager.loadSound(R.raw.dice_roll);

        setToFullScreen();

        createViews(constraintLayout);

        setUpShakeListener();

        if(savedInstanceState == null){
            createFragments();
        } else {
            currentButtonFragmentListener = buildMenuOpened ? buildMenuFragment : baseMenuFragment;
            Log.d("instance", "some saved instance used");
        }

        setupViewModels();

        setupEndTurnButton();

        setupDiceRollButton();

        setupAccuseCheatingButton();

        disposable = new CompositeDisposable();
        disposable.add(StompManager.getInstance().filterByType(GameOverDto.class).subscribe((gameOverDto -> navigateToGameOverActivity())));

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });
    }

    private void navigateToGameOverActivity() {
        Intent i = new Intent(getApplicationContext(), GameOverActivity.class);
        startActivity(i);
        finish();
    }

    private void setUpShakeListener() {
        mShakeListener = new ShakeListener(this);
        try {
            mShakeListener.resume();
            mShakeListener.doOnShake(() -> {
                rollDice();
                gameEffectManager.vibrate(200, 200);
                mShakeListener.pause();
            });
            mUsingShakeListener = true;
        } catch (UnsupportedOperationException e) {
            mUsingShakeListener = false;
        }
    }

    private void setToFullScreen() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void createFragments() {
        playerResourcesFragment = new PlayerResourcesFragment();
        playerScoresFragment = new PlayerScoresFragment();
        baseMenuFragment = new ButtonsClosedFragment();
        buildMenuFragment = new ButtonsOpenFragment();

        baseMenuFragment.setFragmentSwitcher(this::switchFragment);
        buildMenuFragment.setFragmentSwitcher(this::switchFragment);
        currentButtonFragmentListener = baseMenuFragment;
        buildMenuOpened = false;

        getSupportFragmentManager().beginTransaction().add(R.id.playerResourcesFragment, playerResourcesFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.playerScoresFragment, playerScoresFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.leftButtonsFragment, baseMenuFragment).commit();
    }

    private void createViews(ConstraintLayout constraintLayout) {
        ImageView[] hexagonViews = setupViews(constraintLayout, TOTAL_HEXAGONS, HEXAGON_WIDTH, HEXAGON_HEIGHT, 0, null);
        TextView[] rollValueViews = setupRollValueViews(constraintLayout);
        ImageView[] robberViews = setupViews(constraintLayout, TOTAL_HEXAGONS, HEXAGON_HEIGHT / 3, HEXAGON_HEIGHT / 3, TOTAL_HEXAGONS * 2, ClickableElement.ROBBER);
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
            }
        });
    }

    private void clickOnIntersection(int correctID) throws IllegalGameMoveException {
        if (lastButtonClicked != ButtonType.VILLAGE && lastButtonClicked != ButtonType.CITY) {
            throw new IllegalGameMoveException("Select the correct button to build a village or city!");
        }

        switch (lastButtonClicked) {
            case VILLAGE:
                movemaker.makeMove(new BuildVillageMoveDto(correctID), this::onServerError);
                break;
            case CITY:
                movemaker.makeMove(new BuildCityMoveDto(correctID));
                break;
            default:
                throw new IllegalGameMoveException("Select the correct button to build a village or city!");
        }
    }

    private void clickOnConnection(int correctID) throws IllegalGameMoveException {
        if (lastButtonClicked != ButtonType.ROAD) {
            throw new IllegalGameMoveException("Select the correct button to build a road!");
        }
        movemaker.makeMove(new BuildRoadMoveDto(correctID), this::onServerError);
    }

    private void clickOnRobber(int correctID) {
        lastButtonClicked = ButtonType.ROBBER;
        if (hasRolledSeven) {
            movemaker.makeMove(new MoveRobberDto(correctID, true));
            hasRolledSeven = false;
            uiDrawer.setHasRolledSeven(false);
            uiDrawer.removeAllPossibleMovesFromUI();
            baseMenuFragment.makeButtonsClickable();
            buildMenuFragment.makeButtonsClickable();
            makeActivityButtonsClickable();
            return;
        } else if (hasUsedProgressCard) {
            movemaker.makeMove(new UseProgressCardDto(ProgressCardType.KNIGHT, null, null, correctID));
            hasUsedProgressCard = false;
            uiDrawer.removeAllPossibleMovesFromUI();
        }
        if (!uiDrawer.showingPossibleRobberMoves()) {
            uiDrawer.showPossibleMoves(ButtonType.ROBBER);
        } else {
            try {
                movemaker.makeMove(new MoveRobberDto(correctID, false));
            } catch (Exception e) {
                uiDrawer.removeAllPossibleMovesFromUI();
                MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
            }
            lastButtonClicked = ButtonType.EXIT;
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
        endTurnButton = findViewById(R.id.endTurnButton);
        endTurnButton.setOnClickListener(v -> {
                try {
                    movemaker.makeMove(new EndTurnMoveDto(), this::onServerError);
                    movemaker.setHasRolled(false);
                    currentButtonFragmentListener.onButtonEvent(ButtonType.EXIT);
                } catch (Exception e) {
                    MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
                }
        });
    }

    private void setupDiceRollButton() {
        rollDiceButton = findViewById(R.id.diceRollButton);

        rollDiceButton.setOnClickListener(v -> {
            if(board.isSetupPhase()){
                MessageBanner.makeBanner(this, MessageType.ERROR, "Dice can not be rolled during setup phase!").show();
            }
            if (mUsingShakeListener) {
                MessageBanner.makeBanner(this, MessageType.WARNING, "Shake your device or make a long press to roll!").show();
            } else {
                rollDice();
            }
        });
        rollDiceButton.setOnLongClickListener(v -> {
            rollDice();
            if (mUsingShakeListener) mShakeListener.pause();
            return true;
        });
    }

    private void setupAccuseCheatingButton() {
        accuseCheatingButton = findViewById(R.id.accuseCheatingButton);
        accuseCheatingButton.setOnClickListener(v -> {
            try {
                movemaker.makeMove(new AccuseCheatingDto(localPlayer.toIngamePlayerDto()), this::onServerError);
                uiDrawer.removeAllPossibleMovesFromUI();
                lastButtonClicked = null;
            } catch (Exception e) {
                MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
            }
        });
    }

    private void setupViewModels() {
        // viewModels
        BoardViewModel boardViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(BoardViewModel.initializer)).get(BoardViewModel.class);
        LocalPlayerViewModel localPlayerViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(LocalPlayerViewModel.initializer)).get(LocalPlayerViewModel.class);
        PlayerListViewModel playerListViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(PlayerListViewModel.initializer)).get(PlayerListViewModel.class);
        GameProgressViewModel gameProgressViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(GameProgressViewModel.initializer)).get(GameProgressViewModel.class);
        TradeViewModel tradeViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(TradeViewModel.initializer)).get(TradeViewModel.class);

        boardViewModel.getBoardMutableLiveData().observe(this, newBoard -> {
            this.board = newBoard;
            uiDrawer.updateUiBoard(newBoard);
            lastButtonClicked = null;
        });

        localPlayerViewModel.getPlayerMutableLiveData().observe(this, player -> {
            this.localPlayer = player;
            uiDrawer.updateUiPlayerResources(playerResourcesFragment, localPlayer);
        });

        gameProgressViewModel.getGameProgressDtoMutableLiveData().observe(this, gameProgressDto -> {
            if (gameProgressDto.getGameMoveDto() instanceof RollDiceDto) {
                MessageBanner.makeBanner(this, MessageType.INFO, "Dice got rolled: " + ((RollDiceDto) gameProgressDto.getGameMoveDto()).getDiceRoll()).show();
                gameEffectManager.playSound(R.raw.dice_roll);
            }
            if (gameProgressDto.getGameMoveDto() instanceof EndTurnMoveDto) {
                if (((EndTurnMoveDto) gameProgressDto.getGameMoveDto()).getNextPlayer().getInGameID() == localPlayer.getInGameID()) {
                    MessageBanner.makeBanner(this, MessageType.INFO, "Your Turn!").show();
                    mShakeListener.resume();
                    gameEffectManager.vibrate();
                    gameEffectManager.playSound(R.raw.pop);
                } else {
                    MessageBanner.makeBanner(this, MessageType.INFO, "Turn ended! Next Player: " + ((EndTurnMoveDto) gameProgressDto.getGameMoveDto()).getNextPlayer().getDisplayName()).show();
                    gameEffectManager.vibrate();
                }
            }
            if (gameProgressDto.getGameMoveDto() instanceof AccuseCheatingDto) {
                MessageBanner.makeBanner(this, MessageType.INFO, "Player " + ((AccuseCheatingDto) gameProgressDto.getGameMoveDto()).getSender().getDisplayName() + " Accused somebody of cheating!").show();
            }
        });

        playerListViewModel.getPlayerMutableLiveData().observe(this, playerList -> {
            if (!playerList.isEmpty()) {
                uiDrawer.updateUiPlayerScores(playerScoresFragment, playerList);
            }
        });

        tradeViewModel.getTradeOfferDtoMutableLiveData().observe(this, tradeOfferDto -> {
            tradeOfferFragment = new TradeOfferFragment();
            tradeOfferFragment.setServerErrorContext(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.tradeOfferFragment, tradeOfferFragment).commit();
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
                uiDrawer.removeAllPossibleMovesFromUI();
                try {
                    movemaker.makeMove(new BuyProgressCardDto(), this::onServerError);
                    MessageBanner.makeBanner(this, MessageType.INFO, "You received a random card. Check your inventory!").show();
                } catch (Exception e) {
                    MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
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

    @Override
    public void onPause(){
        super.onPause();
        if(mUsingShakeListener)
            mShakeListener.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        mShakeListener.pause();
        gameEffectManager.release();
        MoveMaker.getInstance().clear();
    }

    public void makeAllRobberViewsClickableComingFromProgressCard() {
        this.hasUsedProgressCard = true;
        uiDrawer.showPossibleMoves(ButtonType.ROBBER);
    }

    private void rollDice() {
            try {
                if(random == null)
                    random = new Random();
                int diceRoll = random.nextInt(6) + 1 + random.nextInt(6) + 1;
                movemaker.makeMove(new RollDiceDto(diceRoll), this::onServerError);
                movemaker.setHasRolled(true);
                if (diceRoll == 7 && localPlayer.isActive()) {
                    makeActivityButtonsUnclickable();
                    baseMenuFragment.makeButtonsUnclickable();
                    buildMenuFragment.makeButtonsUnclickable();
                    hasRolledSeven = true;
                    uiDrawer.setHasRolledSeven(true);
                    uiDrawer.showPossibleMoves(ButtonType.ROBBER);
                    lastButtonClicked = ButtonType.ROBBER;
                }
                currentButtonFragmentListener.onButtonEvent(ButtonType.EXIT);
            } catch (Exception e) {
                MessageBanner.makeBanner(this, MessageType.ERROR, e.getMessage()).show();
            }
    }

    private void makeActivityButtonsClickable(){
        accuseCheatingButton.setClickable(true);
        endTurnButton.setClickable(true);
        rollDiceButton.setClickable(true);
        rollDiceButton.setLongClickable(true);
    }
    private void makeActivityButtonsUnclickable(){
        accuseCheatingButton.setClickable(false);
        endTurnButton.setClickable(false);
        rollDiceButton.setClickable(false);
        rollDiceButton.setLongClickable(false);
    }

    private void switchFragment(){
        if(buildMenuOpened){
            getSupportFragmentManager().beginTransaction().replace(R.id.leftButtonsFragment, baseMenuFragment).commit();
            currentButtonFragmentListener = baseMenuFragment;
            buildMenuOpened = false;
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.leftButtonsFragment, buildMenuFragment).commit();
            currentButtonFragmentListener = buildMenuFragment;
            buildMenuOpened = true;
        }
    }

    private void onServerError(Throwable t) {
        MessageBanner.makeBanner(this, MessageType.ERROR, "SERVER: " + t.getMessage()).show();
    }

    private void showExitConfirmationDialog() {
        AlertDialog dialog =  new AlertDialog.Builder(this)
                .setTitle("You are about to leave!")
                .setMessage("You will not be able to reconnect to this game")
                .setPositiveButton("Keep playing", null)
                .setNegativeButton("Leave", (dialog1, which) -> {
                    disposable.add(GameController.getInstance().leaveGame().subscribe(this::finish, throwable -> finish()));
                })
                .create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}