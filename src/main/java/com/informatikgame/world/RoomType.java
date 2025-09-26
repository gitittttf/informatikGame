package com.informatikgame.world;

public enum RoomType {
    //enemy list für jeden room
    ZOMBIE_ROOM(new EnemyType[]{
        EnemyType.MINI_ZOMBIE,
        EnemyType.MINI_ZOMBIE
    }),
    INTRO_ROOM(new EnemyType[]{
        EnemyType.MINI_ZOMBIE
    }),
    FLOOR_ROOM(new EnemyType[]{
        EnemyType.MINI_ZOMBIE,
        EnemyType.MINI_ZOMBIE
    }),
    LIBRARY_ROOM(new EnemyType[]{
        EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE,
        EnemyType.SCIENTIST
    }),
    DINING_HALL(new EnemyType[]{
        EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE,
        EnemyType.MINI_ZOMBIE, EnemyType.SCIENTIST,
        EnemyType.SCIENTIST
    }),
    LABORATORY(new EnemyType[]{
        EnemyType.SCIENTIST, EnemyType.SCIENTIST,
        EnemyType.SCIENTIST, EnemyType.SCIENTIST
    }),
    CORRIDOR(new EnemyType[]{
        EnemyType.BIG_ZOMBIE, EnemyType.BIG_ZOMBIE,
        EnemyType.BIG_ZOMBIE
    }),
    PANTRY(new EnemyType[]{
        EnemyType.MINI_ZOMBIE,
        EnemyType.MINI_ZOMBIE
    }),
    FINAL_ROOM(new EnemyType[]{EnemyType.ENDBOSS});

    public EnemyType[] enemiesInRoom;

    RoomType(EnemyType[] enemiesInRoom) {
        this.enemiesInRoom = enemiesInRoom;
    }
}
