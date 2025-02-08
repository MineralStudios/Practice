package gg.mineral.practice.commands.spectator

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "follow")
class FollowCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg profileToFollow: Profile) {
        if (profile == profileToFollow) return profile.message(ErrorMessages.NOT_FOLLOW_SELF)
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        profile.following = profileToFollow
        profile.message(ChatMessages.FOLLOWING.clone().replace("%player%", profileToFollow.name))
    }
}
