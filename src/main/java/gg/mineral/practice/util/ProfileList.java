package gg.mineral.practice.util;

import java.util.Collection;

import gg.mineral.practice.entity.Profile;

public class ProfileList extends GlueList<Profile> {

    private static final long serialVersionUID = 1L;

    public ProfileList(String name) {
        super();
    }

    public ProfileList() {
        super();
    }

    public ProfileList(int initialCapacity, String name) {
        super(initialCapacity);
    }

    public ProfileList(Collection<? extends Profile> c, String name) {
        super(c);
    }

    public ProfileList(Collection<? extends Profile> c) {
        super(c);
    }

    public boolean contains(Profile profile) {

        if (profile == null) {
            return false;
        }

        for (Profile profile2 : this) {
            if (!profile.equals(profile2)) {
                continue;
            }

            return true;
        }

        return false;
    }

    public Profile removeFirst() {
        return remove(0);
    }
}
