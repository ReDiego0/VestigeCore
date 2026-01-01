package org.ReDiego0.vestigeCore.modules.climate.impl

import org.ReDiego0.vestigeCore.modules.climate.Climate
import org.ReDiego0.vestigeCore.modules.climate.ClimateType
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

object InviernoNuclear : Climate {
    override val name: String = "Invierno Nuclear"
    override val displayName: String = "§bInvierno Nuclear"
    override val type: ClimateType = ClimateType.HOSTILE
    override val duration: Int = 300

    override fun applyVisuals(world: World) {
        world.setStorm(true)
        world.isThundering = true
    }

    override fun applyEffects(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 40, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 40, 0, false, false))

        val loc = player.location
        val highestY = player.world.getHighestBlockYAt(loc)

        if (loc.blockY >= highestY) {
            val currentFreeze = player.freezeTicks
            player.freezeTicks = (currentFreeze + 15).coerceAtMost(player.maxFreezeTicks)
        } else {
            if (player.freezeTicks > 0) player.freezeTicks -= 5
        }

        player.world.spawnParticle(Particle.WHITE_ASH, loc.add(0.0, 1.0, 0.0), 10, 0.5, 0.5, 0.5, 0.0)

        if (Random.nextInt(100) < 2) {
            player.playSound(loc, Sound.ITEM_ELYTRA_FLYING, 0.5f, 0.5f)
        }
    }
}