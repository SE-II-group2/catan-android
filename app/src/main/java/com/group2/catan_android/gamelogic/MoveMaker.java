package com.group2.catan_android.gamelogic;

import com.group2.catan_android.data.live.game.GameMoveDto;
import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.data.repository.moves.MoveSenderRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MoveMaker {
    private Board board;
    private String currentDisplayName;
    private MoveSenderRepository moveSenderRepository = MoveSenderRepository.getInstance();
    private List<DisplayablePlayer> turnoder;
    private boolean isSetupPhase = true;
    private static final int VICTORYPOINTSFORVICTORY = 10;
    private boolean gameover = false;
    private String token;
    private CurrentGamestateRepository currentGamestateRepository = CurrentGamestateRepository.getInstance();
    private CompositeDisposable disposable;
    private int[] resources;
    private boolean hasRolled=false;
    private boolean hasPlacedVillageInSetupPhase=false;

    public MoveMaker(String currentDisplayName) {
        this.currentDisplayName = currentDisplayName;
        board = new Board();
        token = TokenRepository.getInstance().getToken();
        disposable = new CompositeDisposable();
    }

    public void makeMove(GameMoveDto gameMove) throws Exception {
        switch (gameMove.getClass().getSimpleName()) {
            case "RollDiceDto":
                if(hasRolled)throw new Exception("Has already Rolled the dice this turn");
                moveSenderRepository.sendMove(gameMove, token);
                break;
            case "BuildRoadMoveDto":
                if(board.isSetupPhase() && hasPlacedVillageInSetupPhase);

                if(!resourcesSufficient(resources))throw new Exception("Not enough Resources!");

                break;
            case "BuildVillageMoveDto":

                break;
            case "EndTurnMoveDto":

                break;
            default:
                throw new Exception("Unknown Dto format");
        }
    }

    public void adjustResources(int[] resources) {
        if (resources != null && resources.length == 5) {
            for (int i = 0; i < resources.length; i++) {
                this.resources[i] += resources[i];
            }
        }

    }

    public boolean resourcesSufficient(int[] resourceCost){

        if(resourceCost != null && resourceCost.length == 5){
            for (int i = 0; i < resourceCost.length; i++) {
                if(this.resources[i] + resourceCost[i] < 0){
                    return false;
                }
            }
        }
        return true;
    }


}

