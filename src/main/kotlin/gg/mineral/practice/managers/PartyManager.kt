package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.party.Party
import gg.mineral.practice.util.config.BoolProp
import gg.mineral.practice.util.config.IntProp
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.items.ItemStacks
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

object PartyManager {
    val config: FileConfiguration = FileConfiguration("parties.yml", "plugins/Practice")
    val parties: MutableMap<UUID, Party> = Object2ObjectOpenHashMap()
    var slot by IntProp(config, "Parties.Slot", 4)
    var displayItem by ItemStackProp(config, "Parties.DisplayItem", ItemStacks.DEFAULT_PARTY_DISPLAY_ITEM)
    var displayName by StringProp(config, "Parties.DisplayName", "Parties")
    var enabled by BoolProp(config, "Parties.Enable", true)

    fun registerParty(party: Party) {
        parties[party.uuid] = party
    }

    fun remove(party: Party) = parties.remove(party.uuid)

    fun contains(party: Party) = parties.containsKey(party.uuid)

    fun getParty(uuid: UUID) = parties[uuid]
}
