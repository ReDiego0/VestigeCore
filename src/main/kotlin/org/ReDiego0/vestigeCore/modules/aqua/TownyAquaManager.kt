package org.ReDiego0.vestigeCore.modules.aqua

import com.palmergames.bukkit.towny.TownyAPI
import org.ReDiego0.vestigeCore.VestigeCore
import org.ReDiego0.vestigeCore.modules.aqua.data.AquaDatabase
import org.bukkit.entity.Player

class TownyAquaManager(
    private val plugin: VestigeCore,
    private val aquaManager: AquaManager,
    private val database: AquaDatabase
) {

    private val townyAPI = TownyAPI.getInstance()

    fun depositToTown(player: Player, amount: Int): String {
        val resident = townyAPI.getResident(player)
        if (resident == null || !resident.hasTown()) {
            return "§cNo perteneces a ninguna ciudad."
        }
        val town = resident.townOrNull ?: return "§cError de ciudad."
        if (!aquaManager.takeAqua(player, amount)) {
            return "§cNo tienes suficiente Aqua Pura (Item) en tu inventario."
        }

        database.addBalance(town.uuid, amount)
        plugin.logger.info("Jugador ${player.name} convirtió $amount Aqua Items en saldo para ${town.name}")

        return "§bHas depositado $amount Aqua. Nuevo saldo de la ciudad: ${database.getBalance(town.uuid)}"
    }

    fun getBalanceMessage(player: Player): String {
        val resident = townyAPI.getResident(player) ?: return "§cError."
        if (!resident.hasTown()) return "§cNo tienes ciudad."

        val town = resident.townOrNull!!
        val balance = database.getBalance(town.uuid)
        return "§b[Banco de ${town.name}] §f$balance Aqua (Virtual)"
    }
}