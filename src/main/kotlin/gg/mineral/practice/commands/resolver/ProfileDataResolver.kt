package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.managers.ProfileManager
import org.bukkit.command.CommandSender

class ProfileDataResolver : ArgumentResolver<CommandSender, ProfileData>() {
    private fun String.isValidUsername(): Boolean {
        val regex = "^(?=.{3,16}\$)(?![._])(?!.*[._]{2})[a-zA-Z0-9._]+(?<![._])\$".toRegex()
        return this.matches(regex)
    }

    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<ProfileData>,
        string: String
    ): ParseResult<ProfileData> {
        if (!string.isValidUsername())
            return ParseResult.failure("Invalid username")
        
        val profile = ProfileManager.getProfileData(string)
        return ParseResult.success(profile)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<ProfileData>,
        context: SuggestionContext
    ): SuggestionResult = ProfileManager.profiles.values.stream()
        .map { it.name }.collect(SuggestionResult.collector())
}