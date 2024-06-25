package com.group2.catan_android.data.service;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.group2.catan_android.R;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.fragments.PlayerResourcesFragment;
import com.group2.catan_android.fragments.PlayerScoresFragment;
import com.group2.catan_android.fragments.enums.ButtonType;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.enums.ResourceCost;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UiDrawer extends AppCompatActivity {
    private Board board;
    private Player localPlayer;
    private final CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
    private final CompositeDisposable disposable;

    // drawables measurements
    final int HEXAGON_HEIGHT = 230;
    final int HEXAGON_WIDTH = (int) ((float) HEXAGON_HEIGHT / 99 * 86); // 99:86 is the aspect ratio of a hexagon with equal sites
    final int HEXAGON_WIDTH_HALF = HEXAGON_HEIGHT / 2;
    final int HEXAGON_WIDTH_QUARTER = HEXAGON_WIDTH / 4;
    final int HEXAGON_HEIGHT_QUARTER = HEXAGON_HEIGHT / 4;

    // number of total elements
    final int TOTAL_HEXAGONS = 19;
    final int TOTAL_CONNECTIONS = 72;
    final int TOTAL_INTERSECTIONS = 54;

    // storing of possible moves
    private final List<ImageView> possibleRoads;
    private final List<ImageView> possibleVillages;
    private final List<ImageView> possibleCities;
    private final List<ImageView> possibleRobberMoves;
    private boolean showingPossibleRoads = false;
    private boolean showingPossibleVillages = false;
    private boolean showingPossibleCities = false;
    private boolean showingPossibleRobberMoves = false;

    private boolean hasRolledSeven=false;

    private final Activity gameActivityContext;

    public UiDrawer(Activity gameActivityContext) {
        board = new Board();
        disposable = new CompositeDisposable();
        setupListeners();
        this.possibleRoads = new ArrayList<>();
        this.possibleCities = new ArrayList<>();
        this.possibleVillages = new ArrayList<>();
        this.possibleRobberMoves = new ArrayList<>();
        this.gameActivityContext = gameActivityContext;
    }


    public void updateUiBoard(Board board) {
        removeAllPossibleMovesFromUI();

        updatePossibleMoves();

        Connection[][] adjacencyMatrix = board.getAdjacencyMatrix();
        Intersection[][] intersections = board.getIntersections();
        List<Hexagon> hexagonList = board.getHexagonList();

        updateConnections(adjacencyMatrix);
        updateIntersections(intersections);
        updateHexagons(hexagonList);
        if(hasRolledSeven)showPossibleMoves(ButtonType.ROBBER);
    }

    public void setHasRolledSeven(boolean hasRolledSeven){
        this.hasRolledSeven=hasRolledSeven;
    }

    private void updateHexagons(List<Hexagon> hexagonList) {
        for (Hexagon hexagon : hexagonList) {
            int hexagonViewID = hexagon.getId() + 1;
            int rollValueViewID = hexagon.getId() + TOTAL_HEXAGONS + 1;
            int robberViewID = rollValueViewID + TOTAL_HEXAGONS;

            ImageView hexagonView = gameActivityContext.findViewById(hexagonViewID);
            TextView rollValueView = gameActivityContext.findViewById(rollValueViewID);
            ImageView robberView = gameActivityContext.findViewById(robberViewID);

            switch (hexagon.getHexagontype()) {
                case HILLS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_hills));
                    break;
                case FOREST:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_forest));
                    break;
                case MOUNTAINS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_mountains));
                    break;
                case PASTURE:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_pasture));
                    break;
                case FIELDS:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_fields));
                    break;
                default:
                    hexagonView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.hexagon_desert));
                    break;
            }

            setHexagonContent(hexagon, robberView, rollValueView);
        }
    }

    private void setHexagonContent(Hexagon hexagon, ImageView robberView, TextView rollValueView) {
        rollValueView.setText(String.format(Locale.getDefault(), "%d", hexagon.getRollValue()));
        robberView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.robber));

        if (hexagon.isHavingRobber()) {
            robberView.setColorFilter(Color.BLACK);
            rollValueView.setVisibility(View.INVISIBLE);
            robberView.setVisibility(View.VISIBLE);
            robberView.setClickable(true);
        } else {
            robberView.setColorFilter(Color.WHITE);
            robberView.setVisibility(View.INVISIBLE);
            rollValueView.setVisibility(View.VISIBLE);
            robberView.setClickable(false);

        }
    }

    private void updateIntersections(Intersection[][] intersections) {
        for (int row = 0; row < intersections.length; row++) {
            for (int col = 0; col < intersections[row].length; col++) {
                if (intersections[row][col] instanceof Building) {

                    int id = (((Building) intersections[row][col]).getId() + 3 * TOTAL_HEXAGONS + TOTAL_CONNECTIONS + 1);
                    ImageView intersectionView = gameActivityContext.findViewById(id);

                    if (intersections[row][col].getType() == BuildingType.VILLAGE) {
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.village));
                        intersectionView.clearAnimation();
                    } else if (intersections[row][col].getType() == BuildingType.CITY) {
                        intersectionView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.city));
                        intersectionView.setScaleX(1.5F);
                        intersectionView.setScaleY(1.5F);
                        intersectionView.clearAnimation();
                    }
                    intersectionView.setColorFilter(intersections[row][col].getPlayer().getColor());
                }
            }
        }
    }

    private void updateConnections(Connection[][] adjacencyMatrix) {
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix[row].length; col++) {

                if (adjacencyMatrix[row][col] instanceof Road) {
                    int id = (((Road) adjacencyMatrix[row][col]).getId() + TOTAL_HEXAGONS * 3 + 1);

                    ImageView connectionView = gameActivityContext.findViewById(id);
                    connectionView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, R.drawable.steet_red));
                    connectionView.setColorFilter((adjacencyMatrix[row][col]).getPlayer().getColor());
                    connectionView.clearAnimation();
                }
            }
        }
    }

    public void updateUiPlayerResources(PlayerResourcesFragment playerResourcesFragment, Player player) {
        playerResourcesFragment.updateResources(player.getResources());
    }

    public void updateUiPlayerScores(PlayerScoresFragment playerScoresFragment, List<Player> players){
        playerScoresFragment.updateScores(players);
    }

    public void updatePossibleMoves() {
        possibleRoads.clear();
        possibleVillages.clear();
        possibleCities.clear();
        possibleRobberMoves.clear();

        updatePossibleRoads();
        updatePossibleVillagesOrCities();
        updatePossibleRobberMoves();
    }

    private void updatePossibleRobberMoves() {

        for (Hexagon hexagon : board.getHexagonList()) {
            if (hexagon.isHavingRobber()) continue;
            int robberViewID = hexagon.getId() + TOTAL_HEXAGONS + 1 + TOTAL_HEXAGONS;
            ImageView robberView = gameActivityContext.findViewById(robberViewID);
            possibleRobberMoves.add(robberView);
        }
    }

    private void updatePossibleVillagesOrCities() {
        for (int intersectionsID = 0; intersectionsID < TOTAL_INTERSECTIONS; intersectionsID++) {
            if (board.checkPossibleVillage(localPlayer, intersectionsID)) {
                int id = (intersectionsID + TOTAL_HEXAGONS * 3 + TOTAL_CONNECTIONS + 1);
                ImageView intersectionView = gameActivityContext.findViewById(id);
                possibleVillages.add(intersectionView);
            }

            if (!board.isSetupPhase() && board.checkPossibleCity(localPlayer, intersectionsID)) {
                int id = (intersectionsID + TOTAL_HEXAGONS * 3 + TOTAL_CONNECTIONS + 1);
                ImageView intersectionView = gameActivityContext.findViewById(id);
                possibleCities.add(intersectionView);
            }
        }
    }

    private void updatePossibleRoads() {
        for (int connectionID = 0; connectionID < TOTAL_CONNECTIONS; connectionID++) {
            if (board.checkPossibleRoad(localPlayer, connectionID)) {
                int id = (connectionID + TOTAL_HEXAGONS * 3 + 1);
                ImageView connectionView = gameActivityContext.findViewById(id);
                possibleRoads.add(connectionView);
            }
        }
    }

    public void showPossibleMoves(ButtonType button) {
        hideAllPossibleMovesFromUI();

        switch (button) {
            case ROAD:
                showingPossibleVillages = showingPossibleCities = showingPossibleRobberMoves = false;
                showingPossibleRoads = drawPossibleMovesToUI(showingPossibleRoads, possibleRoads, R.drawable.possible_street, button);
                break;
            case VILLAGE:
                showingPossibleRoads = showingPossibleCities = showingPossibleRobberMoves = false;
                showingPossibleVillages = drawPossibleMovesToUI(showingPossibleVillages, possibleVillages, R.drawable.village, button);
                break;
            case CITY:
                showingPossibleRoads = showingPossibleVillages = showingPossibleRobberMoves = false;
                showingPossibleCities = drawPossibleMovesToUI(showingPossibleCities, possibleCities, R.drawable.city, button);
                break;
            case ROBBER:
                showingPossibleRoads = showingPossibleVillages = showingPossibleCities = false;
                showingPossibleRobberMoves = drawPossibleMovesToUI(showingPossibleRobberMoves, possibleRobberMoves, R.drawable.robber, button);
                break;
            default:
                break;
        }
    }

    public boolean drawPossibleMovesToUI(boolean showingMoves, List<ImageView> possibleMoveViews, int drawable, ButtonType button) {

        if (showingMoves) {
            if (button == ButtonType.CITY) {
                removePossibleMovesFromUI(possibleMoveViews, R.drawable.village);
            } else {
                removePossibleMovesFromUI(possibleMoveViews);
            }
            return false;
        }

        boolean resourceSufficient = true;
        switch (button) {
            case ROAD:
                resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.ROAD.getCost());
                break;
            case VILLAGE:
                resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.VILLAGE.getCost());
                break;
            case CITY:
                resourceSufficient = localPlayer.resourcesSufficient(ResourceCost.CITY.getCost());
                break;
            default:
                break;
        }

        drawPossibleMoves(possibleMoveViews, resourceSufficient, drawable);
        return true;
    }

    private void drawPossibleMoves(List<ImageView> possibleMoveViews, boolean resourceSufficient, int drawable) {
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, drawable));
            possibleMoveView.startAnimation(AnimationUtils.loadAnimation(gameActivityContext, R.anim.pulse_animation));
            possibleMoveView.setVisibility(View.VISIBLE);
            possibleMoveView.setClickable(true);
            if (resourceSufficient || board.isSetupPhase()) {
                possibleMoveView.setColorFilter(Color.WHITE);
            } else {
                possibleMoveView.setColorFilter(Color.BLACK);
            }
        }
    }

    public void removePossibleMovesFromUI(List<ImageView> possibleMoveViews) {
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(null);

        }
    }

    public void removeAllPossibleMovesFromUI() {
        removePossibleMovesFromUI(possibleRoads);
        removePossibleMovesFromUI(possibleVillages);
        removePossibleMovesFromUI(possibleCities, R.drawable.village);
        removePossibleRobberMovesFromUi(possibleRobberMoves);
        showingPossibleRoads = showingPossibleVillages = showingPossibleCities = showingPossibleRobberMoves =  false;
    }

    public void hideAllPossibleMovesFromUI() {
        removePossibleMovesFromUI(possibleRoads);
        removePossibleMovesFromUI(possibleVillages);
        removePossibleMovesFromUI(possibleCities, R.drawable.village);
        removePossibleRobberMovesFromUi(possibleRobberMoves);
    }

    public void removePossibleMovesFromUI(List<ImageView> possibleMoveViews, int drawable) {
        for (ImageView possibleMoveView : possibleMoveViews) {
            possibleMoveView.setImageDrawable(ContextCompat.getDrawable(gameActivityContext, drawable));
            possibleMoveView.setColorFilter(localPlayer.getColor());
            possibleMoveView.clearAnimation();
        }
    }

    public void removePossibleRobberMovesFromUi(List<ImageView> possibleMovesViews){
        for(ImageView possibleMoveView : possibleMovesViews){
            possibleMoveView.setVisibility(View.INVISIBLE);
            possibleMoveView.setClickable(false);
            possibleMoveView.bringToFront();
            possibleMoveView.clearAnimation();
        }
    }

    void setupListeners() {
        Disposable gameStateDisposable = currentGamestateRepository.getCurrentGameStateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentGameState -> {
                    this.board = currentGameState.getBoard();
                });
        Disposable activePlayerDisposable = currentGamestateRepository.getCurrentLocalPlayerObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(activePlayer -> {
                    this.localPlayer = activePlayer;
                });
        disposable.add(gameStateDisposable);
        disposable.add(activePlayerDisposable);
    }

    public boolean showingPossibleRobberMoves(){
        return showingPossibleRobberMoves;
    }

    public void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, ImageView[] intersectionViews, ImageView[] connectionViews, TextView[] rollValueViews, ImageView[] robberViews, int layoutWidth, int layoutHeight) {
        ViewConstrainer viewConstrainer = new ViewConstrainer();
        viewConstrainer.constrainViews(constraintLayout,hexagonViews,intersectionViews,connectionViews,rollValueViews,robberViews,layoutWidth,layoutHeight);
    }
}
