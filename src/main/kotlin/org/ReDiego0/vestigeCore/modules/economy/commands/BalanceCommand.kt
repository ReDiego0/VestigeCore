package org.ReDiego0.vestigeCore.modules.economy.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.economy.EconomyManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BalanceCommand(private val manager: EconomyManager) : VestigeSubCommand {

    override fun getName(): String = "balance"

    override fun getPermission(): String = "vestige.player.balance"

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) {
            sender.sendMessage("§cSolo los jugadores tienen cuenta de banco.")
            return
        }

        val balance = manager.getBalance(sender.uniqueId).toLong()
        sender.sendMessage("§aBilletera: §e$$balance Créditos")
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }
}