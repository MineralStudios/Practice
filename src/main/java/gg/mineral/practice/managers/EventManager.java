package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.events.Event;

public class EventManager {
    static GlueList<Event> list = new GlueList<>();

    public static void registerEvent(Event event) {
        list.add(event);
    }

    public static void remove(Event event) {
        list.remove(event);
    }

    public GlueList<Event> getEvents() {
        return list;
    }

    public static Event getEventByName(String s) {
        for (Event event : list) {
            if (event.getHost().equalsIgnoreCase(s)) {
                return event;
            }
        }
        return null;
    }
}
