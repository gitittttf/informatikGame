package com.informatikgame.world;

public class Room {

    EnemyType[] enemiesInRoom;
    String roomName;

    // Constructor mit RoomType
    public Room(RoomType roomType) {
        this.enemiesInRoom = roomType.enemiesInRoom;
        this.roomName = roomType.name();
    }

    // Constructor mit direkten Parametern
    public Room(EnemyType[] enemiesInRoom, String roomName) {
        this.enemiesInRoom = enemiesInRoom;
        this.roomName = roomName;
    }

    // ===== GETTER METHODEN =====
    public EnemyType[] getEnemiesInRoom() {
        return enemiesInRoom;
    }

    public String getRoomName() {
        return roomName;
    }

    // ===== SETTER METHODEN =====
    public void setEnemiesInRoom(EnemyType[] enemies) {
        this.enemiesInRoom = enemies;
    }

    public void setRoomName(String name) {
        this.roomName = name;
    }
}
