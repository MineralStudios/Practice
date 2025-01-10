package gg.mineral.practice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerStatus {
    FOLLOWING {
        @Override
        public boolean canFly(Profile profile) {
            return true;
        }
    }, SPECTATING {
        @Override
        public boolean canFly(Profile profile) {
            return true;
        }
    }, FIGHTING {
        @Override
        public boolean canFly(Profile profile) {
            return false;
        }
    }, KIT_EDITOR {
        @Override
        public boolean canFly(Profile profile) {
            return false;
        }
    },
    KIT_CREATOR {
        @Override
        public boolean canFly(Profile profile) {
            return false;
        }
    },
    IDLE {
        @Override
        public boolean canFly(Profile profile) {
            return profile.getPlayer().hasPermission("practice.fly");
        }
    },
    QUEUEING {
        @Override
        public boolean canFly(Profile profile) {
            return profile.getPlayer().hasPermission("practice.fly");
        }
    };

    public abstract boolean canFly(Profile profile);
}
