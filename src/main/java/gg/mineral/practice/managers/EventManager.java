package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.events.Event;
import lombok.Getter;

public class EventManager {
    @Getter
    static GlueList<Event> events = new GlueList<>();

    public static void registerEvent(Event event) {
        events.add(event);
    }

    public static void remove(Event event) {
        events.remove(event);
    }

    public static Event getEventByName(String s) {
        for (Event event : events) {
            if (event.getHost().equalsIgnoreCase(s)) {
                return event;
            }
        }
        return null;
    }
}
