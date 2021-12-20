package com.github.itsmine.plugin.worldborder.listeners;

import com.github.itsmine.plugin.worldborder.commands.WorldReset;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickInventory implements Listener {

    @EventHandler
    public void clickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equalsIgnoreCase("§aWorlds")) {
            ItemStack item = event.getCurrentItem();
            if(item == null || !item.hasItemMeta()) {
                event.setCancelled(false);
            } else {
                switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                    case "world" :
                        if(compareWorld(player, Bukkit.getWorld("world"))) {
                            player.sendMessage(Component.text("현재 있는 월드입니다."));
                        } else {
                            player.sendMessage(Component.text("월드를 이동했습니다."));
                            WorldReset.teleport_world(player, Bukkit.getWorld("world"));
                        }
                        break;
                    case "world2" :
                        if(compareWorld(player, Bukkit.getWorld("world2"))) {
                            player.sendMessage(Component.text("현재 있는 월드입니다."));
                        } else {
                            player.sendMessage(Component.text("월드를 이동했습니다."));
                            WorldReset.teleport_world(player, Bukkit.getWorld("world2"));
                        }
                        break;
                    case "newWorld" :
                        if(compareWorld(player, Bukkit.getWorld("newWorld"))) {
                            player.sendMessage(Component.text("현재 있는 월드입니다."));
                        } else {
                            player.sendMessage(Component.text("월드를 이동했습니다."));
                            WorldReset.teleport_world(player, Bukkit.getWorld("newWorld"));
                        }
                        break;
                    case "copyworld" :
                        if(compareWorld(player, Bukkit.getWorld("copyworld"))) {
                            player.sendMessage(Component.text("현재 있는 월드입니다."));
                        } else {
                            player.sendMessage(Component.text("월드를 이동했습니다."));
                            WorldReset.teleport_world(player, Bukkit.getWorld("copyworld"));
                        }
                        break;
                }
                event.setCancelled(true);
            }
        }
    }

    /**
     * player의 월드와 인자값 world가 같은지 비교하는 함수
     * @param player player의 월드를 가져오기 위해 필요
     * @param world player의 월드와 비교하기 위한 world
     * @return 같으면 true 다르면 false
     */
    public boolean compareWorld(Player player, World world) {
        return player.getWorld().equals(world);
    }
}
