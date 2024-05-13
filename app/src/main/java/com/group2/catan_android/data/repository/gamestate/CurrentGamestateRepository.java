package com.group2.catan_android.data.repository.gamestate;

import android.util.Log;

import com.group2.catan_android.data.live.game.ConnectionDto;
import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.HexagonDto;
import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.data.live.game.IntersectionDto;
import com.group2.catan_android.data.repository.LiveDataReceiver;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.CurrentGameState;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class CurrentGamestateRepository implements LiveDataReceiver<CurrentGameStateDto>, CurrentgamestateProvider {

    private final BehaviorSubject<CurrentGameState> currentGameStateBehaviorSubject;
    private final BehaviorSubject<Player> activePlayerBehaviorSubject;
    private final BehaviorSubject<List<Player>> playerListBehaviorSubject;
    private Board board;
    private List<Player> players;
    private Player activePlayer;
    CurrentGameState currentGameState;
    HashMap<Integer, Player> playerHashMap;
    private Flowable<CurrentGameStateDto> liveDataIn;
    private static CurrentGamestateRepository instance;
    private int activePlayerIngameID;

    Disposable d;

    private CurrentGamestateRepository() {
        this.currentGameStateBehaviorSubject = BehaviorSubject.create();
        this.activePlayerBehaviorSubject = BehaviorSubject.create();
        this.playerListBehaviorSubject = BehaviorSubject.create();
        board = new Board();
        playerHashMap = new HashMap<>();
    }

    public static CurrentGamestateRepository getInstance() {
        if (instance == null) {
            instance = new CurrentGamestateRepository();
        }
        return instance;
    }

    public void setActivePlayerIngameID(int inGameID) {
        this.activePlayerIngameID=inGameID;
    }

    @Override
    public Observable<CurrentGameState> getCurrentGameStateObservable() {
        return currentGameStateBehaviorSubject;
    }

    @Override
    public Observable<Player> getCurrentActivePlayerObservable() {
        return activePlayerBehaviorSubject;
    }

    @Override
    public Observable<List<Player>> getAllPlayerObservable() {
        return playerListBehaviorSubject;
    }

    @Override
    public void setLiveData(Flowable<CurrentGameStateDto> liveDataIn) {
        this.liveDataIn = liveDataIn;
        cleanup();
        wireDataSources();
    }


    private void cleanup() {
        currentGameStateBehaviorSubject.onNext(new CurrentGameState());
        activePlayerBehaviorSubject.onNext(new Player());
        playerListBehaviorSubject.onNext(new ArrayList<Player>());
        if (d != null)
            d.dispose();
    }

    private void wireDataSources() {
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(CurrentGameStateDto -> {
                    Log.d("received something", "received something");
                    players = getPlayersFromDto(CurrentGameStateDto.getPlayerOrder());
                    board.setHexagonList(getHexagonListFromDto(CurrentGameStateDto.getHexagons()));
                    board.setIntersections(generateIntersectionsFromDto(CurrentGameStateDto.getIntersections()));
                    board.setAdjacencyMatrix(generateAdjacencyMatrixFromDto(CurrentGameStateDto.getConnections()));
                    board.setSetupPhase(CurrentGameStateDto.isSetupPhase());
                    currentGameState = new CurrentGameState(players, board);
                    activePlayer = playerHashMap.get(activePlayerIngameID);
                    activePlayerBehaviorSubject.onNext(activePlayer);
                    playerListBehaviorSubject.onNext(players);
                    currentGameStateBehaviorSubject.onNext(currentGameState);
                });
    }

    private ArrayList<Player> getPlayersFromDto(List<IngamePlayerDto> playerOrder) {
        ArrayList<Player> playersList = new ArrayList<>();
        for (IngamePlayerDto playerDto : playerOrder) {
            Player player = new Player(playerDto.getDisplayName(), playerDto.getVictoryPoints(), playerDto.getResources(), playerDto.getColor());
            playersList.add(player);
            playerHashMap.put(playerDto.getInGameID(), player);
        }
        return playersList;
    }

    private Connection[][] generateAdjacencyMatrixFromDto(List<ConnectionDto> connectionList) {
        Connection[][] connections = new Connection[54][54];
        int[][] connectedIntersections = new int[2][72];
        connectedIntersections[0] = new int[]{0, 1, 2, 3, 4, 5, 0, 2, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 7, 9, 11, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 27, 30, 29, 32, 31, 34, 33, 36, 35, 16, 18, 20, 22, 24, 26, 39, 38, 41, 40, 43, 42, 45, 44, 28, 30, 32, 34, 36, 48, 47, 50, 49, 52, 51, 39, 41, 43, 45};
        connectedIntersections[1] = new int[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 14, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 21, 23, 25, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 29, 28, 31, 30, 33, 32, 35, 34, 37, 36, 27, 29, 31, 33, 35, 37, 40, 39, 42, 41, 44, 43, 46, 45, 38, 40, 42, 44, 46, 49, 48, 51, 50, 53, 52, 47, 49, 51, 53};

        int[] connectionIntersections = new int[2];
        Connection con = new Connection();
        for (ConnectionDto connectionDto : connectionList) {
            connectionIntersections[0] = connectedIntersections[0][connectionDto.getId()];
            connectionIntersections[1] = connectedIntersections[1][connectionDto.getId()];
            if (connectionDto.getOwner() == null) {

                connections[connectionIntersections[0]][connectionIntersections[1]] = con;
                connections[connectionIntersections[1]][connectionIntersections[0]] = con;
            } else {
                connections[connectionIntersections[0]][connectionIntersections[1]] = new Road(playerHashMap.get(connectionDto.getOwner().getInGameID()), connectionDto.getId());
                connections[connectionIntersections[1]][connectionIntersections[0]] = new Road(playerHashMap.get(connectionDto.getOwner().getInGameID()), connectionDto.getId());
            }
        }

        return connections;
    }

    public Intersection[][] generateIntersectionsFromDto(List<IntersectionDto> intersectionDtos) {
        Intersection[][] intersections = new Intersection[6][11];
        Map<Integer, Intersection> idToIntersectionMap = new HashMap<>();
        Intersection intersection;
        // Create Intersection objects and map their IDs to the objects
        for (IntersectionDto dto : intersectionDtos) {
            if (dto.getBuildingType().equals(BuildingType.EMPTY.toString())) {
                intersection = new Intersection();
            } else {
                BuildingType buildingType;
                switch (dto.getBuildingType()) {
                    case "CITY":
                        buildingType = BuildingType.CITY;
                        break;
                    case "VILLAGE":
                        buildingType = BuildingType.VILLAGE;
                        break;
                    default:
                        buildingType = BuildingType.EMPTY;
                        break;
                }
                if(dto.getOwner()==null)intersection = new Building(null, buildingType,dto.getId());
                else intersection = new Building(playerHashMap.get(dto.getOwner().getInGameID()), buildingType,dto.getId());
            }
            idToIntersectionMap.put(dto.getId(), intersection);
        }
        intersections = board.getIntersections();
        int counter=0;
        // Fill the intersections array using the ID-to-Intersection map
        for (int i = 0; i < intersections.length; i++) {
            for (int j = 0; j < intersections[i].length; j++) {
                if(intersections[i][j]!=null) {
                    intersections[i][j] = idToIntersectionMap.get(counter);
                    counter++;
                }
            }
        }

        // Update the board's intersections with the translated array
        return intersections;
    }


    private ArrayList<Hexagon> getHexagonListFromDto(List<HexagonDto> hexagons) {
        ArrayList<Hexagon> hexagonsList = new ArrayList<>();
        for (HexagonDto hexagonDto : hexagons) {
            //TODO add robber to DTO
            hexagonsList.add(new Hexagon(hexagonDto.getHexagonType(), hexagonDto.getResourceDistribution(), hexagonDto.getValue(), hexagonDto.getId(), hexagonDto.isHasRobber()));
        }

        // Custom comparator to sort by ID
        Comparator<Hexagon> idComparator = Comparator.comparing(Hexagon::getId);

        hexagonsList.sort(idComparator);
        return hexagonsList;
    }
}
