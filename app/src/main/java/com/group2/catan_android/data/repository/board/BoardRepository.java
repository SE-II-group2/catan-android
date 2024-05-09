package com.group2.catan_android.data.repository.board;

import android.database.Observable;

import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.game.ConnectionDto;
import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.GameProgressDto;
import com.group2.catan_android.data.live.game.HexagonDto;
import com.group2.catan_android.data.live.game.IntersectionDto;
import com.group2.catan_android.data.repository.LiveDataReceiver;
import com.group2.catan_android.gamelogic.Board;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class BoardRepository implements LiveDataReceiver<CurrentGameStateDto>, BoardProvider {

    private final BehaviorSubject <Board> boardBehaviorSubject;
    private Board board;
    private Flowable<CurrentGameStateDto> liveDataIn;

    private static BoardRepository instance;

    Disposable d;
    private BoardRepository(){
        this.boardBehaviorSubject = BehaviorSubject.create();
        board = new Board();
    }
    public static BoardRepository getInstance(){
        if(instance == null){
            instance = new BoardRepository();
        }
        return instance;
    }

    @Override
    public Observable<Board> getBoardObservable() {
        return null;
    }

    @Override
    public void setLiveData(Flowable<CurrentGameStateDto> in) {
        this.liveDataIn = liveDataIn;
        cleanup();
        wireDataSources();
    }

    private void cleanup(){
        boardBehaviorSubject.onNext(board);
        if(d != null)
            d.dispose();
    }

    private void wireDataSources(){
        d = liveDataIn
                .doOnComplete(this::cleanup)
                .subscribe(CurrentGameStateDto -> {
                    board.setHexagonList(getHexagonListFromDto(CurrentGameStateDto.getHexagons()));
                    board.setIntersections(generateIntersectionsFromDto(CurrentGameStateDto.getIntersections()));
                    board.setAdjacencyMatrix(generateAdjacencyMatrixFromDto(CurrentGameStateDto.getConnections()));
                    boardBehaviorSubject.onNext(board);
                });
    }

    private Connection[][] generateAdjacencyMatrixFromDto(List<ConnectionDto> connectionList) {
        Connection[][] connections = new Connection[54][54];
        int[][] connectedIntersections = new int[2][72];
        connectedIntersections[0] = new int[]{0, 1, 2, 3, 4, 5, 0, 2, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 7, 9, 11, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 27, 30, 29, 32, 31, 34, 33, 36, 35, 16, 18, 20, 22, 24, 26, 39, 38, 41, 40, 43, 42, 45, 44, 28, 30, 32, 34, 36, 48, 47, 50, 49, 52, 51, 39, 41, 43, 45};
        connectedIntersections[1] = new int[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 14, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 21, 23, 25, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 29, 28, 31, 30, 33, 32, 35, 34, 37, 36, 27, 29, 31, 33, 35, 37, 40, 39, 42, 41, 44, 43, 46, 45, 38, 40, 42, 44, 46, 49, 48, 51, 50, 53, 52, 47, 49, 51, 53};

        int[] connectionIntersections = new int[2];
        Connection con = new Connection();
        for(ConnectionDto connectionDto : connectionList){
            connectionIntersections[0] = connectedIntersections[0][connectionDto.getId()];
            connectionIntersections[1] = connectedIntersections[1][connectionDto.getId()];
            if(connectionDto.getOwner()==null){

                connections[connectionIntersections[0]][connectionIntersections[1]] = con;
                connections[connectionIntersections[1]][connectionIntersections[0]] = con;
            }
            else {
                //TODO Set player properly
                Player player = new Player("","","",1);
                connections[connectionIntersections[0]][connectionIntersections[1]] = new Road(player);
                connections[connectionIntersections[1]][connectionIntersections[0]] = new Road(player);
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
            if(dto.getBuildingType().equals(BuildingType.EMPTY)){
                intersection = new Intersection();
            }else {
                BuildingType buildingType;
                switch (dto.getBuildingType()){
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
                //TODO Add player properly
                intersection = new Building(new Player("", "", "", 1), buildingType);
            }
            idToIntersectionMap.put(dto.getId(), intersection);
        }

        // Fill the intersections array using the ID-to-Intersection map
        for (int i = 0; i < intersections.length; i++) {
            for (int j = 0; j < intersections[i].length; j++) {
                if (idToIntersectionMap.containsKey(intersections[i][j])) {
                    intersections[i][j] = idToIntersectionMap.get(intersections[i][j]);
                }
            }
        }

        // Update the board's intersections with the translated array
        return intersections;
    }


    private ArrayList<Hexagon> getHexagonListFromDto(List<HexagonDto> hexagons) {
        ArrayList<Hexagon> hexagonsList= new ArrayList<>();
        for(HexagonDto hexagonDto : hexagons){
            hexagonsList.add(new Hexagon(hexagonDto.getLocation(), hexagonDto.getResourceDistribution(), hexagonDto.getValue(), hexagonDto.getId()));
        }

        // Custom comparator to sort by ID
        Comparator<Hexagon> idComparator = Comparator.comparing(Hexagon::getId);

        hexagonsList.sort(idComparator);
        return hexagonsList;
    }
}
