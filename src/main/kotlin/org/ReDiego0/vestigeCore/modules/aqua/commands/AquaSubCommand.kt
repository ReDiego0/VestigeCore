package org.ReDiego0.vestigeCore.modules.aqua.commands

import org.ReDiego0.vestigeCore.commands.VestigeSubCommand
import org.ReDiego0.vestigeCore.modules.aqua.AquaManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AquaSubCommand(private val aquaManager: AquaManager) : VestigeSubCommand {

    override fun getName(): String {
        return "aqua"
    }

    override fun getPermission(): String {
        return "vestige.admin.aqua"
    }

    override fun execute(sender: CommandSender, args: List<String>) {
        // Estructura: /vcore aqua give <player> <amount>
        if (args.isEmpty()) {
            sender.sendMessage("§cUso: /vcore aqua give <jugador> <cantidad>")
            return
        }

        if (args[0].equals("give", ignoreCase = true)) {
            if (args.size < 3) {
                sender.sendMessage("§cUso: /vcore aqua give <jugador> <cantidad>")
                return
            }

            val target = Bukkit.getPlayer(args[1])
            if (target == null) {
                sender.sendMessage("§cEl jugador ${args[1]} no está conectado.")
                return
            }

            val amount = args[2].toIntOrNull()
            if (amount == null || amount <= 0) {
                sender.sendMessage("§cLa cantidad debe ser un número positivo.")
                return
            }

            aquaManager.giveAqua(target, amount)

            sender.sendMessage("§aHas entregado $amount de Aqua Pura a ${target.name}.")
            target.sendMessage("§bHas recibido $amount de Aqua Pura del sistema.")
        } else {
            sender.sendMessage("§cSubcomando desconocido. Usa 'give'.")
        }
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (args.size == 1) return listOf("give")
        if (args.size == 2) return Bukkit.getOnlinePlayers().map { it.name }
        return emptyList()
    }
}