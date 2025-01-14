package gg.mineral.practice.commands.resolver

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.command.CommandSender

class GametypeResolver : ArgumentResolver<CommandSender, Gametype>() {
    override fun parse(
        invocation: Invocation<CommandSender>,
        argument: Argument<Gametype>,
        string: String
    ): ParseResult<Gametype> {
        val gametype = GametypeManager.getGametypeByName(string)
            ?: return ParseResult.failure(ErrorMessages.GAMETYPE_DOES_NOT_EXIST.toString())

        return ParseResult.success(gametype)
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<Gametype>,
        context: SuggestionContext
    ): SuggestionResult = GametypeManager.gametypes.values.stream()
        .map { it.name }.collect(SuggestionResult.collector())
}