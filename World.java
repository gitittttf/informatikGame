import java.util.ArrayList;

/**
 * The world class represents a collection of rooms in a game environment. It
 * manages the current room, allows navigation between rooms, and provides
 * methods to access and modify room-related information.
 *
 * Key features: - Stores a list of room objects and tracks the current room. -
 * Allows advancing to the next room and adding new rooms. - Provides accessors
 * and mutators for room properties.
 */
public class World {

    // Variabeln
    final int room_count;
    ArrayList<Room> roomList;
    Room current_room;
    int current_room_number;

    /**
     * Constructs a new world with the specified number of rooms and a list of
     * rooms.
     *
     * @param room_count The total number of rooms in the world.
     * @param roomList The list of rooms that make up the world. The first room
     * in the list will be set as the current room.
     */
    
    public World(RoomType[] roomesInWorld) {
        this.room_count = roomesInWorld.length;
        this.roomList = new ArrayList(roomesInWorld.length);
        for (RoomType room : roomesInWorld) {
            roomList.add(new Room(room));
        }
        this.current_room_number = 0;
        this.current_room = roomList.get(current_room_number);
    }

    /**
     * Advances the game to the next room if the current room number is less
     * than the total room count. Increments the current room number and updates
     * the current room reference. If already at the last room, no action is
     * taken.
     */
    public void advance_to_next_room() {
        if (current_room_number < room_count) {
            this.current_room_number++;
            this.current_room = roomList.get(current_room_number + 1);
        }
        else {
            //TODO
        }
    }
}