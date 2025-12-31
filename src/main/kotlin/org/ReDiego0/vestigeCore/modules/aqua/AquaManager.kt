package org.ReDiego0.vestigeCore.modules.aqua
import org.ReDiego0.vestigeCore.VestigeCore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ua.valeriishymchuk.simpleitemgenerator.api.SimpleItemGenerator

class AquaManager(private val plugin: VestigeCore) {
    private val SIG_ITEM_ID = "aqua"
    fun getAquaItem(amount: Int): ItemStack? {
        if (!SimpleItemGenerator.get().hasKey(SIG_ITEM_ID)) {
            plugin.logger.warning("¡Alerta! No se encuentra el item '$SIG_ITEM_ID' en la config de SimpleItemGenerator.")
            return null
        }
        val itemOptional = SimpleItemGenerator.get().bakeItem(SIG_ITEM_ID, null)

        if (itemOptional.isPresent) {
            val item = itemOptional.get()
            item.amount = amount
            return item
        }
        return null
    }

    fun isAqua(item: ItemStack?): Boolean {
        if (item == null || item.type == Material.AIR) return false

        val key = SimpleItemGenerator.get().getCustomItemKey(item)
        return key.isPresent && key.get() == SIG_ITEM_ID
    }

    fun giveAqua(player: Player, amount: Int) {
        val item = getAquaItem(amount) ?: return
        val left = player.inventory.addItem(item)
        left.values.forEach { didNotFit ->
            player.world.dropItemNaturally(player.location, didNotFit)
        }
    }

    fun takeAqua(player: Player, amount: Int): Boolean {
        if (countAqua(player) < amount) return false

        var toRemove = amount
        for (item in player.inventory.contents) {
            if (item != null && isAqua(item)) {
                if (item.amount <= toRemove) {
                    toRemove -= item.amount
                    item.amount = 0 // Eliminar stack
                } else {
                    item.amount -= toRemove
                    toRemove = 0
                }
            }
            if (toRemove <= 0) break
        }
        return true
    }

    fun countAqua(player: Player): Int {
        var total = 0
        for (item in player.inventory.contents) {
            if (item != null && isAqua(item)) {
                total += item.amount
            }
        }
        return total
    }
}