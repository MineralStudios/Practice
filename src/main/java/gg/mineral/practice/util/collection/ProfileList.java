package gg.mineral.practice.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.entity.Profile;
import lombok.Getter;

public class ProfileList extends ConcurrentLinkedQueue<Profile> {

    /**
     *
     */
    @Getter
    String name;
    private static final long serialVersionUID = 1L;

    public ProfileList(String name) {
        super();
        this.name = name;
    }

    public ProfileList() {
        super();
    }

    public ProfileList(Collection<? extends Profile> c, String name) {
        super(c);
        this.name = name;
    }

    public ProfileList(Collection<? extends Profile> c) {
        super(c);
    }

    @Override
    public boolean add(Profile profile) {
        if (contains(profile))
            return false;
        return super.add(profile);
    }

    public boolean contains(Profile p) {

        if (p == null)
            return false;

        for (Profile profile : this)
            if (p.getUuid().equals(profile.getUuid()))
                return true;

        return false;
    }

    public List<Profile> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex)
            throw new IndexOutOfBoundsException();

        List<Profile> subList = new GlueList<>();
        Iterator<Profile> iterator = iterator();

        for (int i = 0; i < fromIndex; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        for (int i = fromIndex; i < toIndex; i++) {
            if (iterator.hasNext()) {
                subList.add(iterator.next());
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        return subList;
    }

    public Profile removeFirst() {
        return poll();
    }

    public Profile getFirst() {
        return peek();
    }
}
