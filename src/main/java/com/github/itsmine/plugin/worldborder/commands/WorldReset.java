package com.github.itsmine.plugin.worldborder.commands;

import com.github.itsmine.plugin.worldborder.WorldBorder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WorldReset implements CommandExecutor, TabCompleter {
    WorldBorder plugin;
    World[] worlds = WorldBorder.worlds;

    public WorldReset(Plugin plugin) {
        this.plugin = (WorldBorder) plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        World world = Bukkit.createWorld(new WorldCreator("world"));
        World world2 = Bukkit.createWorld(new WorldCreator("world2"));
        World newWorld = Bukkit.createWorld(new WorldCreator("newWorld"));
        World copyworld = Bukkit.createWorld(new WorldCreator("copyworld"));

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        if(args.length > 0) {
            if (args[0].equalsIgnoreCase("goworld")) {
                player.teleport(new Location(world, x, y, z));
            } else if (args[0].equalsIgnoreCase("goworld2")) {
                player.teleport(new Location(world2, x, y, z));
            } else if (args[0].equalsIgnoreCase("gonewworld")) {
                player.teleport(new Location(newWorld, x, y, z));
            } else if (args[0].equalsIgnoreCase("gocopyworld")) {
                player.teleport(new Location(copyworld, x, y, z));
            } else if(args[0].equalsIgnoreCase("worldopen")) {
                Inventory inv = createInventory(player);
                player.openInventory(inv);
            }
            return false;
        }

        if(player.getWorld().getName().equalsIgnoreCase("world2")) {
            Bukkit.getOnlinePlayers().forEach(player1 -> teleport_world(player1, newWorld));

            if(unloadWorld(world2)) {
                copyWorld(copyworld, "world2");
            }
        } else if(player.getWorld().getName().equalsIgnoreCase("newWorld")) {
            Bukkit.getOnlinePlayers().forEach(player1 -> teleport_world(player1, world2));

            if(unloadWorld(newWorld)) {
                copyWorld(copyworld, "newWorld");
            }

        }

        return false;
    }

    public static void teleport_world(Player player, World afterWorld) {
        player.teleport(new Location(afterWorld, player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ()
        ));
        player.sendMessage(Component.text(player.getWorld().getName() + "로 이동하였습니다."));
    }

    private static void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean unloadWorld(World world) {
        return world!=null && Bukkit.getServer().unloadWorld(world, false);
    }

    public static void copyWorld(World originalWorld, String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        new WorldCreator(newWorldName).createWorld();
    }

    /**
     * 새로운 Inventory를 만든다.
     * @return inv - 새로 만든 Inventory 반환
     */
    public Inventory createInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("§aWorlds"));
        set_Worlds_Inventory(player, inv);
        return inv;
    }

    /**
     * 현재 활성화된 월드들을 인벤토리에 놓는것.
     * @param player 플레이어의 현재 월드를 가져온다.
     * @param inv 활성화된 인벤토리를 가져온다.
     */
    public void set_Worlds_Inventory(Player player, Inventory inv) {
        for(int i = 0; i < worlds.length; i++) {
            ItemStack item = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(Component.text("§a" + worlds[i].getName()));
            if(player.getWorld() == worlds[i]) {
                item.setType(Material.RED_STAINED_GLASS_PANE);
                itemMeta.setLore(Arrays.asList("현재 있는 월드입니다."));
            } else {
                item.setType(Material.GREEN_STAINED_GLASS_PANE);
                itemMeta.setLore(Arrays.asList("클릭시 해당 월드로 이동합니다"));
            }
            item.setItemMeta(itemMeta);
            inv.setItem(i, item);
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> tip = new ArrayList<>();
        if(args.length == 1) {
            tip.add("goworld");
            tip.add("goworld2");
            tip.add("gonewworld");
            tip.add("gocopyworld");
            tip.add("worldopen");

            return tip;
        }

        return null;
    }
}
