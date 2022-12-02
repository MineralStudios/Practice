package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.events.Event;

public class EventManager {
    GlueList<Event> list = new GlueList<>();

    public void registerEvent(Event event) {
        list.add(event);
    }

    public void remove(Event event) {
        list.remove(event);
    }

    public GlueList<Event> getEvents() {
        return list;
    }

    public Event getEventByName(String s) {
        for (Event t : list) {
            if (t.getHost().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
}
