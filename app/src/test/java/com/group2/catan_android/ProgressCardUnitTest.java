package com.group2.catan_android;


import static org.mockito.Mockito.verify;

import android.graphics.Color;

import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;
import com.group2.catan_android.gamelogic.enums.ResourceCost;
import com.group2.catan_android.gamelogic.objects.ProgressCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProgressCardUnitTest {
    @Mock
    public Player player1;
    private ProgressCard progressCard;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    //TODO: Assert/Verify the cases after implementation
    @Test
    public void testKnightCardUse(){
        progressCard = new ProgressCard(ProgressCardType.KNIGHT, ResourceCost.PROGRESS_CARD);
        progressCard.player = player1;
        progressCard.use();
    }

    @Test
    public void testYOPCardUse(){
        progressCard = new ProgressCard(ProgressCardType.YEAR_OF_PLENTY, ResourceCost.PROGRESS_CARD);
        progressCard.player = player1;
        progressCard.use();
    }

    @Test
    public void testRoadBuildingCardUse(){
        progressCard = new ProgressCard(ProgressCardType.ROAD_BUILDING, ResourceCost.PROGRESS_CARD);
        progressCard.player = player1;
        progressCard.use();
    }

    @Test
    public void testMonopolyCardUse(){
        progressCard = new ProgressCard(ProgressCardType.MONOPOLY, ResourceCost.PROGRESS_CARD);
        progressCard.player = player1;
        progressCard.use();
    }

    @Test
    public void testVictoryPointCardUse(){
        progressCard = new ProgressCard(ProgressCardType.VICTORY_POINT, ResourceCost.PROGRESS_CARD);
        progressCard.player = player1;
        progressCard.use();
        verify(player1).increaseVictoryPoints(1);
    }

}
