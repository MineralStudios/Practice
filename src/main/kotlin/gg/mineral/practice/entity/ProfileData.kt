package gg.mineral.practice.entity

import java.util.*

open class ProfileData(open val name: String) {
    override fun hashCode() = Objects.hash(name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ProfileData
        return name == that.name
    }
}
