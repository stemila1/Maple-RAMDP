package doorworld.stateGenerator;

import burlap.mdp.core.oo.state.OOState;
import doorworld.DoorWorld;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldRoom;
import doorworld.state.DoorWorldState;

import java.util.HashMap;
import java.util.Map;

// TODO: Make method of creating rooms more general, so that the number of rooms can be passed into some
// method, along with the max X of the world and it can be divided up relatively easily. hardcoded values are
// okay for now

public class DoorWorldStateFactory {
    public static OOState createClassicState() {
        return generateThreeRooms(0, 0, 8, 8);
    }

    public static OOState generateThreeRooms(int minX, int minY, int maxX, int maxY) {
        int width = maxX - minX;
        int height = maxY - minY;
        int halfX = minX + (width/2);
        int halfY = minY + (height/2);
        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2);

        Map<String, DoorWorldRoom> rooms = new HashMap<String, DoorWorldRoom>();
        String room0 = DoorWorld.CLASS_ROOM + 0;
        String room1 = DoorWorld.CLASS_ROOM + 1;
        String room2 = DoorWorld.CLASS_ROOM + 2;

        String room0Color = DoorWorld.COLOR_BLUE;
        String room1Color = DoorWorld.COLOR_GREEN;
        String room2Color = DoorWorld.COLOR_YELLOW;

        rooms.put(room0, new DoorWorldRoom(room0, minX, maxX-1, minY, halfY, room0Color));
        rooms.put(room1, new DoorWorldRoom(room1, minX, halfX, halfY, maxY-1, room1Color));
        rooms.put(room2, new DoorWorldRoom(room2, halfX, maxX-1, halfY, maxY-1, room2Color));

        return new DoorWorldState(maxX, maxY, agent, rooms);

    }
}
