package gg.mineral.practice.util;

import gg.mineral.practice.entity.Profile;

public class IntValues {
    public static IntValue<Profile> HIT_DELAY_INT_VALUE = new IntValue<Profile>() {
        @Override
        public int get(Profile obj) {
            return obj.getMatchData().getNoDamageTicks();
        }

        @Override
        public void increment(Profile obj) {
            obj.getMatchData().setNoDamageTicks(get(obj) + 1);
        }

        @Override
        public void decrement(Profile obj) {
            obj.getMatchData().setNoDamageTicks(get(obj) - 1);
        }

    };
}
