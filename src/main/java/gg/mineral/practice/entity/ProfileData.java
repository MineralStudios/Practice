package gg.mineral.practice.entity;

import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ProfileData {
    @Getter
    UUID uuid;
    @Getter
    String name;

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }

    // Note: if you're implementing hashCode, it's a good practice to also
    // override equals so that it is consistent with hashCode. Here's an example:

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProfileData that = (ProfileData) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name);
    }
}
