package com.informatikgame.ui;

public class Story {

    public Story() {
    }

    public static void tellStory(String storyKey) {
        switch (storyKey.toUpperCase()) {
            case "ZOMBIE_ROOM" ->
                System.out.println(StoryDatabank.getStory(StoryDatabank.ZOMBIE_ROOM));
        }
    }
}
