package org.ReDiego0.vestigeCore.modules.economy.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.economy.EconomyManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class EconomySubCommand(private val manager: EconomyManager) : VestigeSubCommand {

    override fun getName(): String = "economy"
    override fun getPermission(): String = "vestige.admin.economy"

    private fun msg(sender: CommandSender, text: String) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', text))
    }

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sendHelp(sender)
            return
        }

        val action = args[0].lowercase()
        if (action == "check") {
            if (args.size < 2) {
                msg(sender, "&cUso: /vcore economy check <jugador>")
                return
            }
            val target = Bukkit.getOfflinePlayer(args[1])
            if (!manager.hasAccount(target.uniqueId)) {
                msg(sender, "&cCuenta no encontrada.")
                return
            }

            val balance = manager.getBalance(target.uniqueId).toLong()

            msg(sender, "&7Balance de &f${target.name}&7: &e$$balance")
            return
        }

        if (args.size < 3) {
            sendHelp(sender)
            return
        }

        val target = Bukkit.getOfflinePlayer(args[1])
        val rawAmount = args[2].toDoubleOrNull()

        if (rawAmount == null || rawAmount < 0) {
            msg(sender, "&cMonto inválido.")
            return
        }

        if (rawAmount % 1 != 0.0) {
            msg(sender, "&cSolo se permiten números enteros.")
            return
        }

        val amount = rawAmount
        val displayAmount = rawAmount.toLong()

        manager.createAccount(target.uniqueId)

        when (action) {
            "give" -> {
                manager.deposit(target.uniqueId, amount)
                msg(sender, "&a+$$displayAmount a ${target.name}")
            }
            "take" -> {
                if (manager.withdraw(target.uniqueId, amount))
                    msg(sender, "&e-$$displayAmount a ${target.name}")
                else
                    msg(sender, "&cFondos insuficientes.")
            }
            "set" -> {
                manager.setBalance(target.uniqueId, amount)
                msg(sender, "&aBalance de ${target.name} fijado en $$displayAmount")
            }
            else -> sendHelp(sender)
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (args.size == 1) {
            return listOf("give", "take", "set", "check").filter { it.startsWith(args[0].lowercase()) }
        }
        if (args.size == 2) {
            return Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], true) }
        }
        return emptyList()
    }

    private fun sendHelp(sender: CommandSender) {
        msg(sender, "&e/vcore economy <give|take|set|check> <user> [amount]")
    }
}