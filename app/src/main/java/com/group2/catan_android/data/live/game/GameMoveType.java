package com.group2.catan_android.data.live.game;

// fixme see backend comments
public interface GameMoveType {
    String BUILDROADMOVE = "BUILD_ROAD_MOVE";
    String BUILDVILLAGEMOVE = "BUILD_VILLAGE_MOVE";
    String BUILDCITYMOVE = "BUILD_CITY_MOVE";
    String ENTTURNMOVE = "END_TURN_MOVE";
    String ROLLDICEMOVE = "ROLL_DICE_MOVE";
    String USEPROGRESSCARD = "USE_PROGRESS_CARD";
    String BUYPROGRESSCARD = "BUY_PROGRESS_CARD";
}
