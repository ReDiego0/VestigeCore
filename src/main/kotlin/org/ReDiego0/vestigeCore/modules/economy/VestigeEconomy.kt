package org.ReDiego0.vestigeCore.modules.economy

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import java.util.UUID

class VestigeEconomy(
    private val plugin: VestigeCore,
    private val manager: EconomyManager
) : Economy {

    private fun msg(path: String): String {
        return ChatColor.translateAlternateColorCodes('&', plugin.config.getString(path, "Mensaje no configurado")!!)
    }

    private fun fmt(amount: Double): String {
        val symbol = plugin.config.getString("economy.currency.symbol", "$")
        return "$symbol${String.format("%.0f", amount)}"
    }

    private fun getUUID(name: String): UUID {
        val player = Bukkit.getPlayer(name)
        if (player != null) return player.uniqueId

        val offline = Bukkit.getOfflinePlayer(name)
        if (offline.hasPlayedBefore()) return offline.uniqueId

        return UUID.nameUUIDFromBytes(name.lowercase().toByteArray())
    }

    override fun isEnabled(): Boolean = true
    override fun getName(): String = "VestigeCore Economy"
    override fun hasBankSupport(): Boolean = false
    override fun fractionalDigits(): Int = 0
    override fun format(amount: Double): String = fmt(amount)
    override fun currencyNamePlural(): String = plugin.config.getString("economy.currency.name-plural", "Créditos")!!
    override fun currencyNameSingular(): String = plugin.config.getString("economy.currency.name-singular", "Crédito")!!

    override fun hasAccount(player: OfflinePlayer): Boolean = manager.hasAccount(player.uniqueId)
    override fun hasAccount(player: OfflinePlayer, worldName: String?): Boolean = hasAccount(player)
    override fun createPlayerAccount(player: OfflinePlayer): Boolean {
        manager.createAccount(player.uniqueId)
        return true
    }
    override fun createPlayerAccount(player: OfflinePlayer, worldName: String?): Boolean = createPlayerAccount(player)
    override fun getBalance(player: OfflinePlayer): Double = manager.getBalance(player.uniqueId)
    override fun getBalance(player: OfflinePlayer, world: String?): Double = getBalance(player)
    override fun has(player: OfflinePlayer, amount: Double): Boolean = getBalance(player) >= amount
    override fun has(player: OfflinePlayer, worldName: String?, amount: Double): Boolean = has(player, amount)

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        if (amount < 0) return EconomyResponse(0.0, getBalance(player), EconomyResponse.ResponseType.FAILURE, msg("economy.messages.error-negative"))
        manager.deposit(player.uniqueId, amount)
        return EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null)
    }
    override fun depositPlayer(player: OfflinePlayer, worldName: String?, amount: Double): EconomyResponse = depositPlayer(player, amount)

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        if (amount < 0) return EconomyResponse(0.0, getBalance(player), EconomyResponse.ResponseType.FAILURE, msg("economy.messages.error-negative"))
        if (manager.withdraw(player.uniqueId, amount)) {
            return EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null)
        }
        return EconomyResponse(0.0, getBalance(player), EconomyResponse.ResponseType.FAILURE, msg("economy.messages.insufficient-funds"))
    }
    override fun withdrawPlayer(player: OfflinePlayer, worldName: String?, amount: Double): EconomyResponse = withdrawPlayer(player, amount)

    override fun hasAccount(playerName: String): Boolean = manager.hasAccount(getUUID(playerName))
    override fun hasAccount(playerName: String, worldName: String?): Boolean = hasAccount(playerName)

    override fun createPlayerAccount(playerName: String): Boolean {
        manager.createAccount(getUUID(playerName))
        return true
    }
    override fun createPlayerAccount(playerName: String, worldName: String?): Boolean = createPlayerAccount(playerName)

    override fun getBalance(playerName: String): Double = manager.getBalance(getUUID(playerName))
    override fun getBalance(playerName: String, world: String?): Double = getBalance(playerName)

    override fun has(playerName: String, amount: Double): Boolean = getBalance(playerName) >= amount
    override fun has(playerName: String, worldName: String?, amount: Double): Boolean = has(playerName, amount)

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = getUUID(playerName)
        if (amount < 0) return EconomyResponse(0.0, manager.getBalance(uuid), EconomyResponse.ResponseType.FAILURE, "Negative amount")
        manager.deposit(uuid, amount)
        return EconomyResponse(amount, manager.getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null)
    }
    override fun depositPlayer(playerName: String, worldName: String?, amount: Double): EconomyResponse = depositPlayer(playerName, amount)

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val uuid = getUUID(playerName)
        if (amount < 0) return EconomyResponse(0.0, manager.getBalance(uuid), EconomyResponse.ResponseType.FAILURE, "Negative amount")
        if (manager.withdraw(uuid, amount)) {
            return EconomyResponse(amount, manager.getBalance(uuid), EconomyResponse.ResponseType.SUCCESS, null)
        }
        return EconomyResponse(0.0, manager.getBalance(uuid), EconomyResponse.ResponseType.FAILURE, "Insufficient funds")
    }
    override fun withdrawPlayer(playerName: String, worldName: String?, amount: Double): EconomyResponse = withdrawPlayer(playerName, amount)

    override fun createBank(name: String?, player: String?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun deleteBank(name: String?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun bankBalance(name: String?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun bankHas(name: String?, amount: Double): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun bankDeposit(name: String?, amount: Double): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun isBankOwner(name: String?, player: String?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun isBankMember(name: String?, player: String?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse = EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No Banks")
    override fun getBanks(): MutableList<String> = mutableListOf()
}