package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender

class ProfileResolver : ArgumentResolver<CommandSender, Profile>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<Profile>,
        string: String
    ): ParseResult<Profile> {
        val profile = ProfileManager.getProfile(string)
            ?: return ParseResult.failure(ErrorMessages.PLAYER_NOT_ONLINE.toString())

        return ParseResult.success(profile)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<Profile>,
        context: SuggestionContext
    ): SuggestionResult = ProfileManager.profiles.values.stream()
        .map { it.name }.collect(SuggestionResult.collector())
}