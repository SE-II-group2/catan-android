package com.group2.catan_android.data.repository;

import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.FIELDS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.data.live.game.ConnectionDto;
import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.HexagonDto;
import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.data.live.game.IntersectionDto;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.CurrentGameState;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.enums.Hexagontype;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Intersection;
import com.group2.catan_android.gamelogic.objects.Road;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class CurrentGamestateRepositoryTest {
    private CurrentGamestateRepository currentGamestateRepository;
    private ArrayList<IngamePlayerDto> playerDtos;
    private Player localPlayer;
    private Player otherPlayer;
    private CurrentGameStateDto currentGameStateDto;
    private  PublishProcessor<CurrentGameStateDto> liveIn;
    TestObserver<CurrentGameState> testObserver;
    CurrentGameState currentGameState;
    @BeforeEach
    void setUp() {
        currentGamestateRepository = CurrentGamestateRepository.getInstance();
        List<ProgressCardType> progressCards = new ArrayList<>();
        localPlayer = new Player("displayName", 0, new int[]{0, 0, 1, 1, 1}, -1, progressCards);
        localPlayer.setInGameID(1);
        otherPlayer = new Player("Other displayName", 0, new int[]{0, 0, 1, 1, 1}, -1000, progressCards);
        otherPlayer.setInGameID(2);
        playerDtos = new ArrayList<>();
        playerDtos.add(otherPlayer.toIngamePlayerDto());
        playerDtos.add(localPlayer.toIngamePlayerDto());
        createCurrentGamestateDto();

        liveIn = PublishProcessor.create();
        currentGamestateRepository.setLiveData(liveIn);
        currentGamestateRepository.setLocalPlayerIngameID(1);
        testObserver = currentGamestateRepository.getCurrentGameStateObservable().test();
        liveIn.onNext(currentGameStateDto);
        List<CurrentGameState> values = testObserver.values();
        currentGameState = values.get(values.size() - 1);
    }

    @AfterEach
    void cleanup(){
        testObserver.dispose();
    }

    @Test
    void testLocalPlayerEmitsCorrectValue() {
        TestObserver<Player> testObserverPlayer = currentGamestateRepository.getCurrentLocalPlayerObservable().test();

        liveIn.onNext(currentGameStateDto);
        List<Player> playerList = testObserverPlayer.values();

        //assume that if the displayName is correct all data is correct
        assertEquals(playerList.get(playerList.size() - 1).getDisplayName(), localPlayer.getDisplayName());

        testObserverPlayer.dispose();
    }

    @Test
    void testActivePlayerEmitsCorrectValue() {
        TestObserver<Player> testObserverPlayer = currentGamestateRepository.getActivePlayerObservable().test();

        liveIn.onNext(currentGameStateDto);
        List<Player> playerList = testObserverPlayer.values();

        //assume that if the displayName if correct all data is correct
        assertEquals(playerList.get(playerList.size() - 1).getDisplayName(), otherPlayer.getDisplayName());

        testObserverPlayer.dispose();
    }

    @Test
    void testHexagonsEmitCorrectValue() {
        int lastRollvalue = 0;
        // If all Hexagons are in ascending order as they were generated, assume correct transmission
        for (Hexagon hexagon : currentGameState.getBoard().getHexagonList()) {
            if (hexagon.getHexagontype().equals(Hexagontype.DESERT)) {
                assertEquals(0, hexagon.getRollValue());
                continue;
            }
            assert (lastRollvalue <= hexagon.getRollValue());
            lastRollvalue = hexagon.getRollValue();
        }
        // Pick one random Hexagon, if it is completely correct we can assume all hexagons are correct
        Hexagon hexagon = currentGameState.getBoard().getHexagonList().get(12);
        // FIELDS, 9
        assertEquals(Hexagontype.FIELDS, hexagon.getHexagontype());
        assertEquals(9, hexagon.getRollValue());
        Assertions.assertArrayEquals(FIELDS.getDistribution(), hexagon.getDistribution().getDistribution());
        assertEquals(12, hexagon.getId());
        assertEquals(0, hexagon.getNumOfAdjacentBuildings());

    }

    @Test
    void testIntersectionsEmitCorrectValue() {
        Intersection[][] intersections = currentGameState.getBoard().getIntersections();

        int lastIndex = -1, buildingCounter = 0, intersectionCounter = 0;
        // Test if all intersections that arent null have a buildingType and count number of empty intersections and buildings
        // If number of intersections transferred is correct and each intersection has one field set we can assume that they got transported correctly
        for (Intersection[] intersectionRow : intersections) {
            for (Intersection intersection : intersectionRow) {
                if (intersection == null) continue;
                if (intersection instanceof Building) {
                    assertEquals(BuildingType.VILLAGE, intersection.getType());
                    assert (lastIndex < ((Building) intersection).getId());
                    lastIndex = ((Building) intersection).getId();
                    buildingCounter++;
                } else {
                    intersectionCounter++;
                    assertEquals(BuildingType.EMPTY, intersection.getType());
                }
            }
        }

        assertEquals(50, intersectionCounter); // 54 Intersections - 4 Buildings = 50 "Empty" intersections
        assertEquals(4, buildingCounter);
        assertEquals(14, lastIndex);

        // Test one intersection for all fields. If this is correct, we can assume all intersections have all fields correct
        Building building = (Building) intersections[0][2];
        assertEquals(0, building.getId());
        assertEquals("displayName", building.getPlayer().getDisplayName());
        assertEquals(BuildingType.VILLAGE, building.getType());
    }

    @Test
    void testConnectionsEmitCorrectValue() {
        Connection[][] adjacencyMatrix = currentGameState.getBoard().getAdjacencyMatrix();

        int connectionCounter = 0, roadCounter = 0;

        for (Connection[] connectionRow : adjacencyMatrix) {
            for (Connection connection : connectionRow) {
                if (connection == null) continue;
                if (connection instanceof Road) {
                    assert (connection.getPlayer().getInGameID() == 1 || connection.getPlayer().getInGameID() == 2);
                    roadCounter++;
                } else {
                    connectionCounter++;
                }
            }
        }

        assertEquals(8, roadCounter); // as there are 2 entries for each connection, multiply the actual number that should be by 2
        assertEquals(136, connectionCounter);

        assert (adjacencyMatrix[4][5] instanceof Road);
        Road road = (Road) adjacencyMatrix[4][5];
        assertEquals(4, road.getId());
        assertEquals(otherPlayer.getDisplayName(), road.getPlayer().getDisplayName());
    }


    //########################################################################################

    private void createCurrentGamestateDto() {
        ArrayList<Hexagon> hexagonList = (ArrayList<Hexagon>) createPreSetupHexagonList();
        List<HexagonDto> hexagonDtoList = new ArrayList<>();

        for (Hexagon hexagon : hexagonList) {
            hexagonDtoList.add(new HexagonDto(hexagon.getHexagontype(), hexagon.getDistribution(), hexagon.getRollValue(), hexagon.getId(), hexagon.isHavingRobber()));
        }

        //Add board and make moves there, then get intersections and adjacency matrix and convert to DTO list
        Board board = new Board();
        board.setHexagonList(hexagonList);
        board.setSetupPhase(true);
        board.addNewVillage(localPlayer, 0);
        board.addNewRoad(localPlayer, 0);
        board.addNewVillage(otherPlayer, 4);
        board.addNewRoad(otherPlayer, 4);
        board.addNewVillage(localPlayer, 10);
        board.addNewRoad(localPlayer, 7);
        board.addNewVillage(otherPlayer, 14);
        board.addNewRoad(otherPlayer, 9);

        List<IntersectionDto> intersectionDtoList = createPreSetupIntersectionList(board);
        List<ConnectionDto> connectionDtoList = createPreSetupConnectionList(board);
        currentGameStateDto = new CurrentGameStateDto(hexagonDtoList, intersectionDtoList, connectionDtoList, playerDtos, otherPlayer.toIngamePlayerDto(),  false);
    }

    private List<ConnectionDto> createPreSetupConnectionList(Board board) {
        List<ConnectionDto> connectionDtos = new ArrayList<>();
        Map<String, Boolean> visitedConnections = new HashMap<>();

        for (int i = 0; i < board.getAdjacencyMatrix().length; i++) {
            for (int j = i + 1; j < board.getAdjacencyMatrix()[i].length; j++) {
                Connection connection = board.getAdjacencyMatrix()[i][j];
                if (connection != null && !visitedConnections.containsKey(i + "-" + j)) {
                    connectionDtos.add(new ConnectionDto((connection.getPlayer() == null) ? null : connection.getPlayer().toIngamePlayerDto(), board.translateIntersectionsToConnection(i, j)));
                    visitedConnections.put(i + "-" + j, true);
                    visitedConnections.put(j + "-" + i, true);  // Mark both [i][j] and [j][i] as visited
                }
            }
        }
        Comparator<ConnectionDto> connectionDtoComparator = Comparator.comparingInt(ConnectionDto::getId);
        connectionDtos.sort(connectionDtoComparator);

        return connectionDtos;
    }

    private List<IntersectionDto> createPreSetupIntersectionList(Board board) {
        List<IntersectionDto> intersectionDtos = new ArrayList<>();
        int id = 0;
        for (Intersection[] intersectionRow : board.getIntersections()) {
            for (Intersection intersection : intersectionRow) {
                if (intersection != null) {
                    intersectionDtos.add(new IntersectionDto((intersection.getPlayer() == null) ? null : intersection.getPlayer().toIngamePlayerDto(), intersection.getType().name(), id++));
                }
            }
        }
        return intersectionDtos;
    }

    private List<Hexagon> createPreSetupHexagonList() {
        //URL of picture of Board:
        //https://cdn.discordapp.com/attachments/1219917626424164376/1231297808997421297/image.png?ex=66367272&is=6623fd72&hm=5989f819604eda76f0d834755e973aaf04f18479a42c26912a5b8a0dc1576799&
        List<Hexagon> hexagonList = new ArrayList<>();
        List<Hexagontype> Hexagontypes = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy Hexagontypes and values lists to ensure original lists remain unchanged
        Collections.addAll(Hexagontypes, Hexagontype.PASTURE, Hexagontype.FOREST, Hexagontype.HILLS, Hexagontype.MOUNTAINS, Hexagontype.HILLS, Hexagontype.FOREST, Hexagontype.HILLS, Hexagontype.FOREST, Hexagontype.HILLS, Hexagontype.FIELDS, Hexagontype.PASTURE, Hexagontype.FIELDS, Hexagontype.FIELDS, Hexagontype.PASTURE, Hexagontype.DESERT, Hexagontype.FIELDS, Hexagontype.MOUNTAINS, Hexagontype.PASTURE, Hexagontype.FOREST);
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        for (int i = 0; i < Hexagontypes.size(); i++) {
            Hexagontype Hexagontype = Hexagontypes.get(i);
            int value;
            if (Hexagontype == com.group2.catan_android.gamelogic.enums.Hexagontype.DESERT) {
                value = 0; // Desert Hexagontype should have value 0
            } else {
                value = values.remove(0);
            }

            ResourceDistribution resourceDistribution;

            switch (Hexagontype) {
                case FIELDS:
                    resourceDistribution = ResourceDistribution.FIELDS;
                    break;
                case PASTURE:
                    resourceDistribution = ResourceDistribution.PASTURE;
                    break;
                case FOREST:
                    resourceDistribution = ResourceDistribution.FOREST;
                    break;
                case HILLS:
                    resourceDistribution = ResourceDistribution.HILLS;
                    break;
                case MOUNTAINS:
                    resourceDistribution = ResourceDistribution.MOUNTAINS;
                    break;
                default:
                    resourceDistribution = ResourceDistribution.DESERT;
                    break;
            }

            hexagonList.add(new Hexagon(Hexagontype, resourceDistribution, value, i, false));
        }

        return hexagonList;
    }
}
