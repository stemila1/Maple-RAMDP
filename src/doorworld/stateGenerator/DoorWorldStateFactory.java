package doorworld.stateGenerator;

import burlap.mdp.core.oo.state.OOState;
import doorworld.DoorWorld;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldRoom;
import doorworld.state.DoorWorldState;

import java.util.HashMap;
import java.util.Map;

// TODO: Make method of creating rooms more general, so that the number of rooms can be passed into some
// method, along with the max X of the world and it can be divided up relatively easily. hardcoded values are
// okay for now

public class DoorWorldStateFactory {
    public static OOState createClassicState() {
        return generateThreeRoomsThreeDoors(0, 0, 8, 8);
    }

    public static OOState generateThreeRoomsThreeDoors(int minX, int minY, int maxX, int maxY) {
        int width = maxX - minX;
        int height = maxY - minY;
        int halfX = minX + (width/2);
        int halfY = minY + (height/2);
        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2);

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

        // add the doors -- all are unlocked for now
        doors.put(door0, new DoorWorldDoor(door0, dx0, dx0, dy0, dy0, "unlocked"));
        doors.put(door1, new DoorWorldDoor(door1, dx1, dx1, dy1, dy1, "unlocked"));
        doors.put(door2, new DoorWorldDoor(door2, dx2, dx2, dy2, dy2, "unlocked"));

        return new DoorWorldState(maxX, maxY, agent, rooms, doors);

    }
}
