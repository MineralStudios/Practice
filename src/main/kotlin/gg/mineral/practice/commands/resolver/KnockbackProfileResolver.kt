package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.server.combat.KnockbackProfile
import gg.mineral.server.combat.KnockbackProfileList
import org.bukkit.command.CommandSender

class KnockbackProfileResolver : ArgumentResolver<CommandSender, KnockbackProfile>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<KnockbackProfile>,
        string: String
    ): ParseResult<KnockbackProfile> {
        val knockbackProfile = KnockbackProfileList.getKnockbackProfileByName(string)
            ?: return ParseResult.failure(ErrorMessages.KNOCKBACK_DOES_NOT_EXIST.toString())

        return ParseResult.success(knockbackProfile)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<KnockbackProfile>,
        context: SuggestionContext
    ): SuggestionResult = KnockbackProfileList.getProfiles().values.stream()
        .map { it.name }.collect(SuggestionResult.collector())
}