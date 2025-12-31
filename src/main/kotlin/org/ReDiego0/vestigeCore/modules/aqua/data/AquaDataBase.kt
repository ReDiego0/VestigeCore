package org.ReDiego0.vestigeCore.modules.aqua.data

import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

class AquaDatabase(private val plugin: VestigeCore) {

    private val file = File(plugin.dataFolder, "town_balances.yml")
    private lateinit var config: YamlConfiguration

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            try {
                file.createNewFile()
            } catch (e: Exception) {
                plugin.logger.severe("No se pudo crear town_balances.yml!")
                e.printStackTrace()
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() {
        config.save(file)
    }

    fun getBalance(townUUID: UUID): Int {
        return config.getInt("towns.${townUUID}", 0)
    }

    fun addBalance(townUUID: UUID, amount: Int) {
        val current = getBalance(townUUID)
        config.set("towns.${townUUID}", current + amount)
        save()
    }

    fun removeBalance(townUUID: UUID, amount: Int): Boolean {
        val current = getBalance(townUUID)
        if (current < amount) return false
        config.set("towns.${townUUID}", current - amount)
        save()
        return true
    }

    fun getNextTaxTime(): Long {
        return config.getLong("system.next_tax_time", 0L)
    }

    fun setNextTaxTime(timestamp: Long) {
        config.set("system.next_tax_time", timestamp)
        save()
    }
}