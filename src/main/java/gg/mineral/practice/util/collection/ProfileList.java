package gg.mineral.practice.util.collection;

import java.util.Collection;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.entity.Profile;

public class ProfileList extends GlueList<Profile> {

    /**
     *
     */
    String name;
    private static final long serialVersionUID = 1L;

    public ProfileList(String name) {
        super();
        this.name = name;
    }

    public ProfileList() {
        super();
    }

    public ProfileList(int initialCapacity, String name) {
        super(initialCapacity);
        this.name = name;
    }

    public ProfileList(Collection<? extends Profile> c, String name) {
        super(c);
        this.name = name;
    }

    public ProfileList(Collection<? extends Profile> c) {
        super(c);
    }

    public String getName() {
        return name;
    }

    public boolean contains(Profile p) {

        if (p == null) {
            return false;
        }

        for (Profile profile : this) {
            if (p.getUUID().equals(profile.getUUID())) {
                return true;
            }
        }

        return false;
    }

    public Profile removeFirst() {
        return remove(0);
    }
}
