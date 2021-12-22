package ms.uk.eclipse.util;

import java.util.Collection;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.entity.Profile;

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

        for (Profile pl : this) {
            if (p.equals(pl)) {
                return true;
            }
        }

        return false;
    }

    public Profile removeFirst() {
        return remove(0);
    }
}
