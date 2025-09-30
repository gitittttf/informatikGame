package com.informatikgame.world;

import java.util.ArrayList;

/**
 * The world class represents a collection of rooms in a game environment. It
 * manages the current room, allows navigation between rooms, and provides
 * methods to access and modify room-related information.
 */
public class World {

    // Variabeln
    private final int room_count;
    private final ArrayList<Room> roomList;
    private Room current_room;
    private int current_room_number;

    /**
     * Constructs a new world with the specified rooms
     *
     * @param roomsInWorld THe list of rooms in the world.
     */
    public World(RoomType[] roomsInWorld) {
        this.room_count = roomsInWorld.length;
        this.roomList = new ArrayList<>(roomsInWorld.length);
        for (RoomType room : roomsInWorld) {
            roomList.add(new Room(room));
        }
        this.current_room_number = 0;
        this.current_room = roomList.get(current_room_number);
    }

    /**
     * Advances the game to the next room if the current room number is less
     * than the total room count
     */
    public void advance_to_next_room() {
        if (current_room_number < room_count - 1) {
            this.current_room_number++;
            this.current_room = roomList.get(this.current_room_number);
        } else {
            System.out.println("Kein weiterer Raum vorhanden!");
        }
    }

    /**
     * See if theres more rooms
     */
    public boolean hasNextRoom() {
        return current_room_number < room_count - 1;
    }

    /**
     * @return current progress
     */
    public String getProgress() {
        return "Raum " + (current_room_number + 1) + " von " + room_count;
    }

    // ===== GETTER METHODS =====
    public int getRoom_count() {
        return room_count;
    }

    public ArrayList<Room> getRoomList() {
        return roomList;
    }

    public Room getCurrent_room() {
        return current_room;
    }

    public int getCurrent_room_number() {
        return current_room_number;
    }

    // ===== SETTER METHODS =====
    public void setCurrent_room(Room room) {
        this.current_room = room;
    }

    public void setCurrent_room_number(int number) {
        this.current_room_number = number;
    }

}
