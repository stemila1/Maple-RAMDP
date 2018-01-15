package doorworld.stateGenerator;

import burlap.mdp.core.oo.state.OOState;
import doorworld.DoorWorld;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldRoom;
import doorworld.state.DoorWorldState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephanie Milani on 01/11/2018
 */

// TODO: Make method of creating rooms more general, so that the number of rooms can be passed into some
// method, along with the max X of the world and it can be divided up relatively easily. hardcoded values are
// okay for now

public class DoorWorldStateFactory {
    // createClassicState
    public static OOState createClassicState() {
        return generateThreeRoomsThreeDoors(0, 0, 8, 8);
    }

    // generateThreeRoomsThreeDoors
    public static OOState generateThreeRoomsThreeDoors(int minX, int minY, int maxX, int maxY) {
        int width = maxX - minX;
        int height = maxY - minY;
        int halfX = minX + (width/2);
        int halfY = minY + (height/2);

        // make the agent
        String agentDirection = DoorWorld.DIRECTIONS[0];
        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2, agentDirection);

        // make the rooms
        Map<String, DoorWorldRoom> rooms = new HashMap<String, DoorWorldRoom>();
        String room0 = DoorWorld.CLASS_ROOM + 0;
        String room1 = DoorWorld.CLASS_ROOM + 1;
        String room2 = DoorWorld.CLASS_ROOM + 2;

        String room0Color = DoorWorld.COLOR_BLUE;
        String room1Color = DoorWorld.COLOR_GREEN;
        String room2Color = DoorWorld.COLOR_YELLOW;

        // make the doors
        Map<String, DoorWorldDoor> doors = new HashMap<String, DoorWorldDoor>();
        String door0 = DoorWorld.CLASS_DOOR + 0;
        String door1 = DoorWorld.CLASS_DOOR + 1;
        String door2 = DoorWorld.CLASS_DOOR + 2;

        // hardcoded values for now
        int dx0 = 2;
        int dx1 = 4;
        int dx2 = 5;

        int dy0 = 4;
        int dy1 = 5;
        int dy2 = 4;

        // add the rooms
        rooms.put(room0, new DoorWorldRoom(room0, minX, maxX-1, minY, halfY, room0Color));
        rooms.put(room1, new DoorWorldRoom(room1, minX, halfX, halfY, maxY-1, room1Color));
        rooms.put(room2, new DoorWorldRoom(room2, halfX, maxX-1, halfY, maxY-1, room2Color));

        // add the doors
        doors.put(door0, new DoorWorldDoor(door0, dx0, dx0, dy0, dy0, DoorWorld.VAL_UNLOCKED));
        doors.put(door1, new DoorWorldDoor(door1, dx1, dx1, dy1, dy1, DoorWorld.VAL_UNLOCKED));
        doors.put(door2, new DoorWorldDoor(door2, dx2, dx2, dy2, dy2, DoorWorld.VAL_LOCKED));

        return new DoorWorldState(maxX, maxY, agent, rooms, doors);

    }

    public static OOState createCustomState(int minX, int minY, int maxX, int maxY,
                                            String qual0, String qual1, String qual2) {
        int width = maxX - minX;
        int height = maxY - minY;
        int halfX = minX + (width/2);
        int halfY = minY + (height/2);

        // make the agent
        String agentDirection = DoorWorld.DIRECTIONS[0];
        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2, agentDirection);

        // make the rooms
        Map<String, DoorWorldRoom> rooms = new HashMap<String, DoorWorldRoom>();
        String room0 = DoorWorld.CLASS_ROOM + 0;
        String room1 = DoorWorld.CLASS_ROOM + 1;
        String room2 = DoorWorld.CLASS_ROOM + 2;

        String room0Color = DoorWorld.COLOR_BLUE;
        String room1Color = DoorWorld.COLOR_GREEN;
        String room2Color = DoorWorld.COLOR_YELLOW;

        // make the doors
        Map<String, DoorWorldDoor> doors = new HashMap<String, DoorWorldDoor>();
        String door0 = DoorWorld.CLASS_DOOR + 0;
        String door1 = DoorWorld.CLASS_DOOR + 1;
        String door2 = DoorWorld.CLASS_DOOR + 2;

        // hardcoded values for now
        int dx0 = 2;
        int dx1 = 4;
        int dx2 = 5;

        int dy0 = 4;
        int dy1 = 5;
        int dy2 = 4;

        // add the rooms
        rooms.put(room0, new DoorWorldRoom(room0, minX, maxX-1, minY, halfY, room0Color));
        rooms.put(room1, new DoorWorldRoom(room1, minX, halfX, halfY, maxY-1, room1Color));
        rooms.put(room2, new DoorWorldRoom(room2, halfX, maxX-1, halfY, maxY-1, room2Color));

        // add the doors
        doors.put(door0, new DoorWorldDoor(door0, dx0, dx0, dy0, dy0, qual0));
        doors.put(door1, new DoorWorldDoor(door1, dx1, dx1, dy1, dy1, qual1));
        doors.put(door2, new DoorWorldDoor(door2, dx2, dx2, dy2, dy2, qual2));

        return new DoorWorldState(maxX, maxY, agent, rooms, doors);

    }

    // generateNineRoomsTenDoors
    public static OOState generateNineRoomsTenDoors(int minX, int minY, int maxX, int maxY) {

        int width = maxX - minX;
        int height = maxY - minY;
        int thirdX = minX + (width/3);
        int thirdY = minY + (height/3);

        // make the agent
        String agentDirection = DoorWorld.DIRECTIONS[0];
        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2, agentDirection);

        // make the rooms
        Map<String, DoorWorldRoom> rooms = new HashMap<String, DoorWorldRoom>();
        String room0 = DoorWorld.CLASS_ROOM + 0;
        String room1 = DoorWorld.CLASS_ROOM + 1;
        String room2 = DoorWorld.CLASS_ROOM + 2;
        String room3 = DoorWorld.CLASS_ROOM + 3;
        String room4 = DoorWorld.CLASS_ROOM + 4;
        String room5 = DoorWorld.CLASS_ROOM + 5;
        String room6 = DoorWorld.CLASS_ROOM + 6;
        String room7 = DoorWorld.CLASS_ROOM + 7;
        String room8 = DoorWorld.CLASS_ROOM + 8;

        String room0Color = DoorWorld.COLOR_BLUE;
        String room1Color = DoorWorld.COLOR_GREEN;
        String room2Color = DoorWorld.COLOR_YELLOW;
        String room3Color = DoorWorld.COLOR_MAGENTA;
        String room4Color = DoorWorld.COLOR_RED;
        String room5Color = DoorWorld.COLOR_CYAN;
        String room6Color = DoorWorld.COLOR_ORANGE;
        String room7Color = DoorWorld.COLOR_LIGHT_GRAY;
        String room8Color = DoorWorld.COLOR_WHITE;

        // make the doors
        Map<String, DoorWorldDoor> doors = new HashMap<String, DoorWorldDoor>();
        String door0 = DoorWorld.CLASS_DOOR + 0;
        String door1 = DoorWorld.CLASS_DOOR + 1;
        String door2 = DoorWorld.CLASS_DOOR + 2;
        String door3 = DoorWorld.CLASS_DOOR + 3;
        String door4 = DoorWorld.CLASS_DOOR + 4;
        String door5 = DoorWorld.CLASS_DOOR + 5;
        String door6 = DoorWorld.CLASS_DOOR + 6;
        String door7 = DoorWorld.CLASS_DOOR + 7;
        String door8 = DoorWorld.CLASS_DOOR + 8;
        String door9 = DoorWorld.CLASS_DOOR + 9;

        // hardcoded values for now
        int dx0 = 2;
        int dx1 = 4;
        int dx2 = 5;
        int dx3 = 8;
        int dx4 = 8;
        int dx5 = 4;
        int dx6 = 5;
        int dx7 = 11;
        int dx8 = 10;
        int dx9 = 8;

        int dy0 = 4;
        int dy1 = 5;
        int dy2 = 4;
        int dy3 = 5;
        int dy4 = 9;
        int dy5 = 11;
        int dy6 = 8;
        int dy7 = 4;
        int dy8 = 8;
        int dy9 = 1;

        // add the rooms
        rooms.put(room0, new DoorWorldRoom(room0, minX, thirdX, minY, thirdY, room0Color));
        rooms.put(room1, new DoorWorldRoom(room1, minX, thirdX, thirdY, 2*thirdY, room1Color));
        rooms.put(room2, new DoorWorldRoom(room2, minX, thirdX, 2*thirdY, maxY-1, room2Color));
        rooms.put(room3, new DoorWorldRoom(room3, thirdX, 2*thirdX, minY, thirdY, room3Color));
        rooms.put(room4, new DoorWorldRoom(room4, thirdX, 2*thirdX, thirdY, 2*thirdY, room4Color));
        rooms.put(room5, new DoorWorldRoom(room5, thirdX, 2*thirdX, 2*thirdY, maxY-1, room5Color));
        rooms.put(room6, new DoorWorldRoom(room6, 2*thirdX, maxX-1, minY, thirdY, room6Color));
        rooms.put(room7, new DoorWorldRoom(room7, 2*thirdX, maxX-1, thirdY, 2*thirdY, room7Color));
        rooms.put(room8, new DoorWorldRoom(room8, 2*thirdX, maxX-1, 2*thirdY, maxY-1, room8Color));

        // add the doors
        doors.put(door0, new DoorWorldDoor(door0, dx0, dx0, dy0, dy0, DoorWorld.VAL_UNLOCKED));
        doors.put(door1, new DoorWorldDoor(door1, dx1, dx1, dy1, dy1, DoorWorld.VAL_UNLOCKED));
        doors.put(door2, new DoorWorldDoor(door2, dx2, dx2, dy2, dy2, DoorWorld.VAL_LOCKED));
        doors.put(door3, new DoorWorldDoor(door3, dx3, dx3, dy3, dy3, DoorWorld.VAL_LOCKED));
        doors.put(door4, new DoorWorldDoor(door4, dx4, dx4, dy4, dy4, DoorWorld.VAL_UNLOCKED));
        doors.put(door5, new DoorWorldDoor(door5, dx5, dx5, dy5, dy5, DoorWorld.VAL_UNLOCKED));
        doors.put(door6, new DoorWorldDoor(door6, dx6, dx6, dy6, dy6, DoorWorld.VAL_UNLOCKED));
        doors.put(door7, new DoorWorldDoor(door7, dx7, dx7, dy7, dy7, DoorWorld.VAL_UNLOCKED));
        doors.put(door8, new DoorWorldDoor(door8, dx8, dx8, dy8, dy8, DoorWorld.VAL_UNLOCKED));
        doors.put(door9, new DoorWorldDoor(door9, dx9, dx9, dy9, dy9, DoorWorld.VAL_UNLOCKED));

        return new DoorWorldState(maxX, maxY, agent, rooms, doors);
    }
}
