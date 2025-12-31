package org.ReDiego0.vestigeCore.modules.aqua.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.aqua.TownyAquaManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TownyBankCommand(private val townyManager: TownyAquaManager) : VestigeSubCommand {

    override fun getName(): String {
        return "bank"
    }

    override fun getPermission(): String {
        return "vestige.town.bank"
    }

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) {
            sender.sendMessage("§cEste comando solo pueden usarlo jugadores dentro del juego.")
            return
        }

        // Estructura: /vcore bank <deposit|balance> [cantidad]
        if (args.isEmpty()) {
            sender.sendMessage("§e--- Banco de Aqua ---")
            sender.sendMessage("§f/vcore bank balance §7- Ver saldo de tu ciudad")
            sender.sendMessage("§f/vcore bank deposit <cantidad> §7- Depositar Aqua")
            return
        }

        val action = args[0].lowercase()

        when (action) {
            "deposit", "depositar" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cUso: /vcore bank deposit <cantidad>")
                    return
                }

                val amount = args[1].toIntOrNull()
                if (amount == null || amount <= 0) {
                    sender.sendMessage("§cPor favor, escribe una cantidad válida.")
                    return
                }

                val message = townyManager.depositToTown(sender, amount)
                sender.sendMessage(message)
            }

            "balance", "saldo", "ver" -> {
                val message = townyManager.getBalanceMessage(sender)
                sender.sendMessage(message)
            }

            else -> {
                sender.sendMessage("§cAcción desconocida. Usa 'deposit' o 'balance'.")
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (args.size == 1) return listOf("deposit", "balance")
        return emptyList()
    }
}