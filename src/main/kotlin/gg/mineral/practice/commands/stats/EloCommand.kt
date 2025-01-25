package gg.mineral.practice.commands.stats

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.inventory.menus.EloMenu
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import java.util.*

@Command(name = "elo")
class EloCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg profileData: Optional<ProfileData>) {
        // TODO: Support multiple ranked queuetypes
        queuetypes.values.first { it?.ranked == true }.let { queuetype ->
            profileData.ifPresentOrElse({ eloProfile ->
                queuetype?.let { EloMenu(eloProfile, it) }?.let { profile.openMenu(it) }
            }, {
                queuetype?.let { EloMenu(profile, it) }?.let { profile.openMenu(it) }
            })
        }
    }
}
