package org.ReDiego0.vestigeCore.modules.climate.impl

import org.ReDiego0.vestigeCore.modules.climate.Climate
import org.ReDiego0.vestigeCore.modules.climate.ClimateType
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

object SobrecargaOxigeno : Climate {
    override val name: String = "Sobrecarga de O2"
    override val displayName: String = "§bSobrecarga de O2"
    override val type: ClimateType = ClimateType.BENEFICIAL
    override val duration: Int = 240

    override fun applyVisuals(world: World) {
        world.setStorm(false)
        world.isThundering = false
    }

    override fun applyEffects(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 40, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 40, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 40, 0, false, false))

        val dust = Particle.DustOptions(Color.fromRGB(135, 206, 250), 0.8f)
        player.world.spawnParticle(Particle.DUST, player.location.add(0.0, 1.0, 0.0), 3, 0.3, 0.5, 0.3, dust)

        if (Random.nextInt(100) < 1) {
            player.playSound(player.location, Sound.BLOCK_BEACON_AMBIENT, 0.5f, 2.0f)
        }
    }
}