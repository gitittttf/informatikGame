import java.util.ArrayList;

public class Room {
    EnemyType[] enemiesInRoom;
    
    // Constructor
    public Room(RoomType roomType)
    {
        this.enemiesInRoom = roomType.enemiesInRoom;
    }
    public Room(EnemyType[] enemiesInRoom) {
        this.enemiesInRoom = enemiesInRoom;
    }
}