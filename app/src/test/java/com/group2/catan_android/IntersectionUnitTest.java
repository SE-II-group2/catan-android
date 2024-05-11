package com.group2.catan_android;

import com.group2.catan_android.gamelogic.enums.BuildingType;
import com.group2.catan_android.gamelogic.objects.Intersection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class IntersectionUnitTest {
    private Intersection intersection;
    @BeforeEach
    public void setUp() {
        intersection = new Intersection();
    }

    @Test
    public void testInterSectionGetter(){
        Assertions.assertNull(intersection.getPlayer());
        Assertions.assertEquals(BuildingType.EMPTY, intersection.getType());
    }
}
