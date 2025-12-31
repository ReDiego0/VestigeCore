package org.ReDiego0.vestigeCore.modules.economy.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.economy.EconomyManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PayCommand(private val manager: EconomyManager) : VestigeSubCommand {

    override fun getName(): String = "pay"
    override fun getPermission(): String = "vestige.player.pay"

    override fun execute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) {
            sender.sendMessage("§cSolo los jugadores pueden realizar pagos.")
            return
        }

        // Estructura: /vcore pay <jugador> <cantidad>
        if (args.size < 2) {
            sender.sendMessage("§cUso: /vcore pay <jugador> <cantidad>")
            return
        }

        val targetName = args[0]
        if (targetName.equals(sender.name, ignoreCase = true)) {
            sender.sendMessage("§cNo puedes pagarte a ti mismo.")
            return
        }

        val amount = args[1].toDoubleOrNull()
        if (amount == null || amount <= 0) {
            sender.sendMessage("§cLa cantidad debe ser un número positivo.")
            return
        }

        if (amount % 1 != 0.0) {
            sender.sendMessage("§cSolo se permiten cantidades enteras.")
            return
        }

        val senderBalance = manager.getBalance(sender.uniqueId)
        if (senderBalance < amount) {
            sender.sendMessage("§cNo tienes suficientes créditos. Tienes: $${senderBalance.toLong()}")
            return
        }

        val target = Bukkit.getOfflinePlayer(targetName)
        if (!manager.hasAccount(target.uniqueId)) {
            sender.sendMessage("§cEl jugador $targetName no tiene cuenta (nunca ha entrado al servidor).")
            return
        }

        manager.withdraw(sender.uniqueId, amount) // Quitar al emisor
        manager.deposit(target.uniqueId, amount)  // Dar al receptor

        manager.saveAccounts()

        val displayAmount = amount.toLong()
        sender.sendMessage("§aHas enviado $$displayAmount a ${target.name ?: targetName}.")

        if (target.isOnline) {
            target.player?.sendMessage("§eHas recibido $$displayAmount de ${sender.name}.")
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (args.size == 1) return Bukkit.getOnlinePlayers().map { it.name }
        return emptyList()
    }
}