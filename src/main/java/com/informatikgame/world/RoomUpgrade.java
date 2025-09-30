package com.informatikgame.world;

/**
 * Maps each room to its specific upgrade reward Based on the exit stories
 * defined in StoryDatabank
 */
public enum RoomUpgrade {
    INTRO_ROOM_UPGRADE(UpgradeType.LIFE_UPGRADE), // +5 HP
    FLOOR_ROOM_UPGRADE(UpgradeType.LIFE_UPGRADE), // +5 HP
    PANTRY_1_UPGRADE(UpgradeType.PANTRY_COMPOUND_UPGRADE), // +2 Damage und +5 HP
    LIBRARY_ROOM_UPGRADE(UpgradeType.SKILL_UPGRADE), // +1 Fintelevel und +1 Wuchtschlaglevel
    DINING_HALL_UPGRADE(UpgradeType.ATTACK_UPGRADE), // +2 Attack
    LABORATORY_UPGRADE(null), // Kein upgrade
    CORRIDOR_UPGRADE(UpgradeType.ARMOUR_UPGRADE), // +3 Armour
    FINAL_ROOM_UPGRADE(null); // Kein upgrade

    private final UpgradeType upgradeType;

    RoomUpgrade(UpgradeType upgradeType) {
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    /**
     * Get the room upgrade for a specific room type
     */
    public static RoomUpgrade getUpgradeForRoom(RoomType roomType) {
        return switch (roomType) {
            case INTRO_ROOM ->
                INTRO_ROOM_UPGRADE;
            case FLOOR_ROOM ->
                FLOOR_ROOM_UPGRADE;
            case PANTRY_1 ->
                PANTRY_1_UPGRADE;
            case LIBRARY_ROOM ->
                LIBRARY_ROOM_UPGRADE;
            case DINING_HALL ->
                DINING_HALL_UPGRADE;
            case LABORATORY ->
                LABORATORY_UPGRADE;
            case CORRIDOR ->
                CORRIDOR_UPGRADE;
            case FINAL_ROOM ->
                FINAL_ROOM_UPGRADE;
            default ->
                null;
        };
    }
}
