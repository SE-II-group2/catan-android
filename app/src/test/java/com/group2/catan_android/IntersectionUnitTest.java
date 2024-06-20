package com.group2.catan_android;

import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.objects.Intersection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// fixme if you want to test getters set the values first
public class IntersectionUnitTest {
    private Intersection intersection;
    @BeforeEach
    void setUp() {
        intersection = new Intersection();
    }

    @Test
    void testInterSectionGetterAndSetter(){
        Assertions.assertNull(intersection.getPlayer());
        intersection.setBuildingType(BuildingType.EMPTY);
        Assertions.assertEquals(BuildingType.EMPTY, intersection.getType());
    }
}
