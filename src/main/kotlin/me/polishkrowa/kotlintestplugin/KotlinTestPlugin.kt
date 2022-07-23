package me.polishkrowa.kotlintestplugin

import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class KotlinTestPlugin : JavaPlugin(), Listener {

    override fun onEnable() {
        println("Enabled")
        Bukkit.getPluginManager().registerEvents(this, this)

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name == "test") {
            //send red test message
            sender.sendMessage(ChatColor.RED.toString() + "Test")
            (sender as Player).velocity = sender.location.direction.multiply(2.0)


            return true
        } else if (command.name == "launch") {
            if (args.isNotEmpty()) {
                sender.sendMessage(ChatColor.RED.toString() + "Whoooo!")
                (sender as Player).velocity = sender.location.direction.multiply(args[0].toIntOrNull() ?: 0)
            } else
                sender.sendMessage(ChatColor.RED.toString() + "no args ")
            return true
        } else if (command.name == "heal") {
            if (args.isNotEmpty() && args[0].equals("confirm", true)) {
                if (sender is Player)
                    sender.health = 20.0
                sender.sendMessage(ChatColor.GREEN.toString() + "Healed!")
            } else {
                val componant = TextComponent("Would you like to be healed?")
                componant.isBold = true
                componant.color = net.md_5.bungee.api.ChatColor.YELLOW
                componant.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/heal confirm")
                componant.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Click to confirm"))

                sender.spigot().sendMessage(componant)
            }
        } else if (command.name == "boots") {
            val item = getBoots()

            if (sender !is Player) {
                sender.sendMessage(ChatColor.RED.toString() + "You must be a player to use this command")
                return true
            }

            sender.inventory.addItem(item)
            sender.sendMessage(ChatColor.GREEN.toString() + "Boots added!")
            return true
        }

        return false
    }

    private fun getBoots() : ItemStack {
        val item = ItemStack(Material.DIAMOND_BOOTS)
        val meta = item.itemMeta
        meta?.setDisplayName("Insane Boots")
        meta?.lore = listOf(ChatColor.YELLOW.toString() + "This is a test boots")
        meta?.isUnbreakable = true
        item.itemMeta = meta
        return item
    }

    @EventHandler
    fun onPlayerJump(event: PlayerMoveEvent) {
        if (event.from.y < (event.to?.y ?: -1000.0) && event.player.inventory.boots?.itemMeta?.displayName == "Insane Boots" && event.from.subtract(0.0,0.5,0.0).block.type!=Material.AIR) {
            event.player.velocity = event.player.location.direction.multiply(2.0)
        }
    }


    @EventHandler
    fun onPlayerLand(event: EntityDamageEvent) {
        if (event.entity is Player && (event.entity as Player).inventory.boots?.itemMeta?.displayName == "Insane Boots" && event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_AIR) {
            if (event.item?.type == Material.TRIDENT) {
                (event.player.world.spawnEntity(event.player.location, EntityType.FIREBALL) as Fireball).direction = event.player.location.direction

            }

        }
    }
}