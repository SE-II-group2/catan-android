# SonarCloud
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SE-II-group2_catan-android&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SE-II-group2_catan-android)

# ProjectDescription
This project represents an Android Application for the Settlers of Catan. Part of an University Project

Introduction to Catan
---------------------

Catan is a strategy board game where players aim to be the first to reach 10 victory points. Players gather resources (brick, wood, ore, wheat, and sheep) from hexagon tiles on the board based on dice rolls, then use these resources to build roads, villages, and cities. Trading with other players and buying development cards are two additional aspects of the game, as well  as a   moveable robber, which blocks resource production of a hexagon.

Game Features
-------------

![](https://img.itch.zone/aW1nLzE2Njc3Mzc2LmdpZg==/original/b%2B3RJn.gif)

#### Create / Join Lobby

After clicking a button on the starting screen, players are redirected to a lobby screen where they have the possibility to create or join a lobby. Any username can be  chosen, since players always get a different  color. Through a refresh button players can  check for new lobbies available.

![](https://img.itch.zone/aW1nLzE2Njc3NDA2LnBuZw==/original/eiiXPw.png)

After joining or creating a  lobby, players are waiting in  their  lobby until the owner of the lobby starts the game. Up to 4 players can join a lobby and with the game being started it is not possible to join the lobby anymore.

![](https://img.itch.zone/aW1nLzE2Njc3NDEyLnBuZw==/original/dDbg%2Bf.png)

#### Reconnecting to Lobby

When a player gets     disconnected from the current game he is greyed out for all other players and able to rejoin the lobby through a button on the lobby screen.

![](https://img.itch.zone/aW1nLzE2Njc4MDY1LnBuZw==/original/voec6F.png)

Once a move is made by one of the other players the reconnected player receives the current game state and is back in the game.

#### Build Roads , Villages, Cities

One of the main featues of the game is the possibility to build roads, villages or cities. Villages and cities lead to victory points and therefore essential to win the game.

Building something is possible by clicking the build button and than selecting  the button of the object you want to build and than selecting your   desired object on the board. 

![](https://img.itch.zone/aW1nLzE2Njc4MjY3LmdpZg==/original/3QB9fi.gif)

During a special setup phase it is     allowed to place 2 villages with a road  next to it     anywhere. After that it is only possible to build roads and villages next to your already existing roads.   Cities are build on intersection where a village is already existing, therefore "upgrading" your village.  Villages are not allowed to be directly next to each other.

Cities and villages both give 1 victory point when built. All three objects have different resource costs.

#### Trading Resources

When resources are needed, the option to trade them might become important. 

![](https://img.itch.zone/aW1nLzE2Njc5NzA1LnBuZw==/original/JdcOt2.png)

By clicking the button, a pop up opens up where players can chose which resouces the would like to offer and  get in a trade.  A trade can either be a 4:1 trade with the bank, meaning to give a total of 4 resources in order to get 1 desired resource. The other option is to make a trade offer for all other players  .  Players that should   not receive an offer can also be deselected when making an of fer.

![](https://img.itch.zone/aW1nLzE2Njg3OTY5LnBuZw==/original/isvkyJ.png)

When confirming  to make an offer all players the where selected receive  this offer and can accept or decline the offer. The player who  accepts it first   then receives and loses the resources that where agreed in   the trade offer.

![](https://img.itch.zone/aW1nLzE2Njg3NTQ2LnBuZw==/original/8bilNI.png)

#### Buying / Using Progress Cards

In addition to building roads, villages, and cities,   so called   progress cards can be purchased providing different advantages. 

![](https://img.itch.zone/aW1nLzE2Njc3NTI2LnBuZw==/original/orGFuq.png)

When buying a card, one of five random card types,   will be stored to    a progress card inventory, which can be accessed through  a    button. 

![](https://img.itch.zone/aW1nLzE2Njc3NTM2LnBuZw==/original/TIq8SN.png)

Clicking a card in the inventory  activates it and depending on the type of card, additional choices may need to be made.  

![](https://img.itch.zone/aW1nLzE2Njc5NDk4LnBuZw==/original/3XLCag.png)

These are the five types that can be obtained:

Monopoly: Allows to claim all resources of a chosen type from all other players.

![](https://img.itch.zone/aW1nLzE2Njc5NTM1LnBuZw==/original/qi8kjf.png)

Year of Plenty: Provides you with two resources of choice.

![](https://img.itch.zone/aW1nLzE2Njc5NTQ0LnBuZw==/original/KAQHZ1.png)

Road Building: Grants the resources needed to build two roads.

Victory Point: Awards one additional victory point.

Knight: Enables to move the robber to any field desired.

#### Moving the robber

Not only the Knight progress card  allows players to move the robber  to a different hexagon but also when a 7 is rolled. The active player then has to pick a new position for the robber and can strategically block the resource production of a hexagon and therefore deny players who have villages or cities on that hexagon to gain resources from that hexagon.

![](https://img.itch.zone/aW1nLzE2Njc3OTM3LmdpZg==/original/ujrn8z.gif)

#### Cheat by secretly moving the robber

The only legal ways to move the robber are by using a progress card or rolling a seven. But  players now have the option to cheat by simply clicking the robber on the current hexagon   and moving it like it would be a legal move. If nobody recognise this it can be effienctly used to block resource production of hexagons where other players have buildings on. 

#### Accusing possible cheat

If somebody  thinks the robber might have been moved illegally it is possible to accuse a cheat by clicking the robber button.

![](https://img.itch.zone/aW1nLzE2Njc3OTc2LnBuZw==/original/UKgNis.png)

If the accuse is correct the  cheating player gets his resources reduced by half, whilst the player exposing the cheat gets those resources.  If  there was  no cheating the accusing player gets his resources reduced by half for making a wrong accuse.

#### Dice roll & ending turn

In every round the  active player can make as much moves as he wants, wether it is building something, buying and using progress cards are creating trade offers.  It is also possible to make zero moves in a round but it is mandatory to roll dice in every round.

![](https://img.itch.zone/aW1nLzE2Njc3OTk0LnBuZw==/original/zfNLqK.png)

This can be done by shaking the device. Pressing the button will only remind players that they have to shake the device. In case the shaking does not work (possibly due to a missing sensor), the the dice roll can be triggered with a long press. The roll value is then  being displayed through a message banner.  In order to end the current turn the end turn button needs to be clicked by the active player.

#### Final Scoreboard

Once one player reaches 10 victory points a winner has been found and  all players are redirected to a screen showing the final  scores with players ranked and colored in the correct  position.

![](https://img.itch.zone/aW1nLzE2Njc5MjA5LnBuZw==/original/HKUU3v.png)

Game Extras
-----------

#### Randomized Board

Each game started  generates a randomized board with hexagons and it's values being alinged in another way,    to make every game  different.

#### Vizualization of possible moves

When clicking on one of the buttons to build something, blinking animations show all possible places of where a player could build something. This feature makes it more convenient to play and build things  as players do not have to think about possible moves or if their resources are enough.

![](https://img.itch.zone/aW1nLzE2Njc4MDA1LmdpZg==/original/i%2FRLMw.gif)

White blinking indicating that it is possible and black blinking indicating that it would be possible but the player does not have enough resources.

![](https://img.itch.zone/aW1nLzE2Njc4MDE5LmdpZg==/original/VZkYfe.gif)

#### **Indicator for active player**

On the right of the player names and their scores  there is always dot next to one of the players indicating that he is the active player. This helps all players to always    easily see who's turn it is  even if they miss the message banner stating who's turn it is.

![](https://img.itch.zone/aW1nLzE2Njc4MDgzLnBuZw==/original/k6%2FlUn.png)

#### **Custom message banners**

Instead of normal android toasts , custom message banners are used to give players information about what is happening and possible errors of why their desired move was not possible.

![](https://img.itch.zone/aW1nLzE2Njc4MDg5LnBuZw==/original/fUb7rL.png)

![](https://img.itch.zone/aW1nLzE2Njc4MDk1LnBuZw==/original/bfth4h.png)

#### **Sound effects and vibration**

Building objects and clicking buttons lead to sound effects as well as a vibration effect reminding a player that he is now active.

#### **Help button**

By clicking the  help button, indicated as a questionmark, players get additional information about the costs  of each buyable object and with which button cheating can be accused.

![](https://img.itch.zone/aW1nLzE2Njc4MjA2LnBuZw==/original/iAZAES.png)

#### Leaving games

If a player decides, they no longer want to play, they can simply press the back button (or use the gesture) on their Android device. A confirmation dialog will appear. If he confirms, he will leave the game and also won't be able to reconnect to it. 

![](https://img.itch.zone/aW1nLzE2Njg5OTA1LnBuZw==/original/RZS6fs.png)
