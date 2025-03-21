package gg.mineral.practice.battle

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.collection.ProfileList

interface Battle {
    fun onStart(profile: Profile)

    fun onStart()

    fun onCountdownStart(profile: Profile)

    val ended: Boolean

    val participants: ProfileList
}