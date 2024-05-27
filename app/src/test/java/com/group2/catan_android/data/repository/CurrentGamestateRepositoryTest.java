package com.group2.catan_android.data.repository;

import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.FIELDS;
import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.FOREST;
import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.HILLS;
import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.MOUNTAINS;
import static com.group2.catan_android.gamelogic.enums.ResourceDistribution.PASTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.group2.catan_android.data.live.PlayerDto;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.live.game.ConnectionDto;
import com.group2.catan_android.data.live.game.CurrentGameStateDto;
import com.group2.catan_android.data.live.game.HexagonDto;
import com.group2.catan_android.data.live.game.IngamePlayerDto;
import com.group2.catan_android.data.live.game.IntersectionDto;
import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;
import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.Hexagontype;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;
import com.group2.catan_android.gamelogic.objects.Hexagon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;

public class CurrentGamestateRepositoryTest {
    private CurrentGamestateRepository currentGamestateRepository;
    private ArrayList<IngamePlayerDto> playerDtos;
    private Player localPlayer;
    private Player otherPlayer;

    @BeforeEach
    public void setUp() {
        currentGamestateRepository = CurrentGamestateRepository.getInstance();
        localPlayer = new Player("displayName", 0, new int[]{0, 0, 1, 1, 1}, -1);
        localPlayer.setInGameID(1);
        otherPlayer = new Player("Other displayName", 0, new int[]{0, 0, 1, 1, 1}, -1000);
        otherPlayer.setInGameID(2);
         playerDtos = new ArrayList<>();
        playerDtos.add(otherPlayer.toIngamePlayerDto());
        playerDtos.add(localPlayer.toIngamePlayerDto());
    }

    @Test
    void testLocalPlayerEmitsCorrectValue() {
        PublishProcessor<CurrentGameStateDto> liveIn = PublishProcessor.create();
        currentGamestateRepository.setLiveData(liveIn);
        currentGamestateRepository.setLocalPlayerIngameID(1);
        TestObserver<Player> testObserver = currentGamestateRepository.getCurrentLocalPlayerObservable().test();

        CurrentGameStateDto dto = new CurrentGameStateDto(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerDtos, true);

        liveIn.onNext(dto);
        List<Player> playerList = testObserver.values();

        //assume that if the displayName if correct all data is correct
        assertEquals(playerList.get(playerList.size() - 1).getDisplayName(), localPlayer.getDisplayName());

        testObserver.dispose();
    }

    @Test
    void testActivePlayerEmitsCorrectValue() {
        PublishProcessor<CurrentGameStateDto> liveIn = PublishProcessor.create();
        currentGamestateRepository.setLiveData(liveIn);
        currentGamestateRepository.setLocalPlayerIngameID(1);
        TestObserver<Player> testObserver = currentGamestateRepository.getActivePlayerObservable().test();

        CurrentGameStateDto dto = new CurrentGameStateDto(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), playerDtos, true);

        liveIn.onNext(dto);
        List<Player> playerList = testObserver.values();

        //assume that if the displayName if correct all data is correct
        assertEquals(playerList.get(playerList.size() - 1).getDisplayName(), otherPlayer.getDisplayName());

        testObserver.dispose();
    }

    @Test
    void testIntersectionsEmitCorrectValue(){

    }


    //########################################################################################

    private void createCurrentGamestateDto(){
        List<Hexagon> hexagonList = createPreSetupHexagonList();
        List<HexagonDto> hexagonDtoList = new ArrayList<>();

        for(Hexagon hexagon : hexagonList){
            hexagonDtoList.add(new HexagonDto(hexagon.getHexagontype(), hexagon.getDistribution(), hexagon.getRollValue(), hexagon.getId(), hexagon.isHasRobber()));
        }
    }

    private List<Hexagon> createPreSetupHexagonList() {
        //URL of picture of Board:
        //https://cdn.discordapp.com/attachments/1219917626424164376/1231297808997421297/image.png?ex=66367272&is=6623fd72&hm=5989f819604eda76f0d834755e973aaf04f18479a42c26912a5b8a0dc1576799&
        List<Hexagon> hexagonList = new ArrayList<>();
        List<Hexagontype> Hexagontypes = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy Hexagontypes and values lists to ensure original lists remain unchanged
        Collections.addAll(Hexagontypes, Hexagontype.PASTURE, Hexagontype.FOREST, Hexagontype.HILLS,
                Hexagontype.MOUNTAINS, Hexagontype.HILLS, Hexagontype.FOREST, Hexagontype.HILLS,
                Hexagontype.FOREST, Hexagontype.HILLS, Hexagontype.FIELDS, Hexagontype.PASTURE, Hexagontype.FIELDS,
                Hexagontype.FIELDS, Hexagontype.PASTURE, Hexagontype.DESERT, Hexagontype.FIELDS,
                Hexagontype.MOUNTAINS, Hexagontype.PASTURE, Hexagontype.FOREST);
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
