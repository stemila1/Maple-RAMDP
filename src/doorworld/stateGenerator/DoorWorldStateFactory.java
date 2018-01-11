package doorworld.stateGenerator;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import doorworld.DoorWorld;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldRoom;
import doorworld.state.DoorWorldState;
import doorworld.state.DoorWorldWall;

import java.util.ArrayList;
import java.util.List;

// TODO: Make method of creating rooms more general, so that the number of rooms can be passed into some
// method, along with the max X of the world and it can be divided up relatively easily. hardcoded values are
// okay for now

public class DoorWorldStateFactory {
    public static OOState createClassicState() {
        return generateThreeRooms();
    }

    public static OOState generateThreeRooms() {

        DoorWorldAgent agent = new DoorWorldAgent(DoorWorld.CLASS_AGENT + 0, 1, 2);

        List<DoorWorldWall> walls = new ArrayList();
        // the perimeter
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 0, 0, 0, 8, false));
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 1, 0, 0, 8, true));
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 2, 0, 8, 8, true));
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 3, 8, 0, 8, false));

        // additional walls
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 4, 4, 0, 4, false));
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 5, 0, 4, 4, true));
        walls.add(new DoorWorldWall(DoorWorld.CLASS_WALL + 6, 4, 4, 4, true));

        return new DoorWorldState(agent, walls);
    }
}
