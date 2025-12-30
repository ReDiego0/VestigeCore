package org.ReDiego0.vestigeCore.modules.economy

import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class EconomyManager(private val plugin: VestigeCore) {

    private val accounts = HashMap<UUID, Double>()
    private val file = File(plugin.dataFolder, "data/economy.yml")
    private val configData: YamlConfiguration

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        configData = YamlConfiguration.loadConfiguration(file)
        loadAccounts()
    }

    private fun loadAccounts() {
        configData.getKeys(false).forEach { uuidStr ->
            val balance = configData.getDouble(uuidStr)
            try {
                accounts[UUID.fromString(uuidStr)] = balance
            } catch (e: IllegalArgumentException) {
                plugin.logger.warning("[Economy] UUID invalida encontrada: $uuidStr")
            }
        }
    }

    fun saveAccounts() {
        accounts.forEach { (uuid, balance) ->
            configData.set(uuid.toString(), Math.round(balance).toDouble())
        }
        configData.save(file)
    }

    fun getBalance(uuid: UUID): Double {
        return accounts.getOrDefault(uuid, plugin.config.getDouble("economy.settings.starting-balance", 0.0))
    }

    fun setBalance(uuid: UUID, amount: Double) {
        accounts[uuid] = amount
    }

    fun hasAccount(uuid: UUID): Boolean = accounts.containsKey(uuid)

    fun createAccount(uuid: UUID) {
        if (!hasAccount(uuid)) {
            accounts[uuid] = plugin.config.getDouble("economy.settings.starting-balance", 0.0)
        }
    }

    fun deposit(uuid: UUID, amount: Double) = setBalance(uuid, getBalance(uuid) + amount)

    fun withdraw(uuid: UUID, amount: Double): Boolean {
        val current = getBalance(uuid)
        if (current >= amount) {
            setBalance(uuid, current - amount)
            return true
        }
        return false
    }
}