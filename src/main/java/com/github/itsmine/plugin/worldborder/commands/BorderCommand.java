package com.github.itsmine.plugin.worldborder.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BorderCommand implements TabCompleter, CommandExecutor {

    com.github.itsmine.plugin.worldborder.WorldBorder plugin;
    public BorderCommand(Plugin plugin) {
        this.plugin = (com.github.itsmine.plugin.worldborder.WorldBorder) plugin;
    }

    private static int time = 60;
    private static int size = 10;
    private static int sec = 0;
    private static boolean isPlay = false;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("help")) {
                command_Help(player);
            }

            else if(args[0].equalsIgnoreCase("setting")) {
                if(args.length < 2) {
                    print_setting(player);
                } else {

                    if (args[1].equalsIgnoreCase("setlocation")) {
                        command_Setting_SetLocation(player);
                    } else if (args[1].equalsIgnoreCase("settime")) {
                        if (args.length > 2) {
                            try {
                                if(Integer.parseInt(args[2]) < 0) {
                                    player.sendMessage("잘못된 값입니다. 크기는 양수이여야 합니다.");
                                } else {
                                    time = Integer.parseInt(args[2]);
                                    player.sendMessage(time + "초 ");
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage("입력에러, time값이 정수가 아닙니다.");
                            }
                        } else {
                            player.sendMessage("§a사용법 : §6/mf §esetting settime <시간(초단위)> §f: 자기장이 현재 위치까지 줄어들 시간을 정한다");
                        }
                    } else if (args[1].equalsIgnoreCase("size")) {
                        if (args.length > 2) {
                            try {
                                if(Integer.parseInt(args[2]) < 0) {
                                    player.sendMessage("잘못된 값입니다. 크기는 양수이여야 합니다.");
                                } else {
                                    size = Integer.parseInt(args[2]);
                                    player.sendMessage(size + "크기");
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage("입력에러, size값이 정수가 아닙니다.");
                            }
                        } else {
                            player.sendMessage("§a사용법 : §6/mf §esetting size <시간(초단위)> §f: 자기장이 줄어들 크기를 정한다");
                        }
                    }
                }

            }

            else if (args[0].equalsIgnoreCase("play")) {
                command_Play(player);
            }

            else if (args[0].equalsIgnoreCase("reset")) {
                command_Reset(player);
            }

            else if (args[0].equalsIgnoreCase("stop")) {
                command_Stop(player);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("인게임 내에서만 사용가능합니다.");
        }
        return false;
    }

    public void command_Help(Player commander) {
        commander.sendMessage(Component.text(
         "§b------------------------------------------------------\n" +
                "§a일반 : §6/mf §esetting <settime/setlocation/size> §f: 자기장이 줄어드는 시간, 위치, 크기를 정한다.\n" +
                "§a일반 : §6/mf §eplay §f: 자기장을 줄어들게 한다.\n" +
                "§a일반 : §6/mf §ereset §f: 자기장을 리셋시킨다."
        ));
    }

    /**
     * input -> /mf setting setlocation
     * 월드보더를
     *
     * @param target 플레이어의 월드보더를 가져오기위해 사용.
     */
    public void command_Setting_SetLocation(Player target) {
        WorldBorder wb = target.getWorld().getWorldBorder();
        wb.setCenter(target.getLocation());
        target.sendMessage(Component.text(
      "§a현재위치 : §ex §f: " + (int) target.getLocation().getX() +
                        " §ey §f: " + (int) target.getLocation().getY() +
                        " §ez §f: " + (int) target.getLocation().getZ()
        ));
    }

    /**
     * input -> /mf play
     * <p>해당 월드보더 세팅옵션을 기준으로 월드보더를 세팅합니다.</p>
     * 월드보더 location이 지정되어있지 않는 (0, 0, 0)좌표일 경우, 현재 플레이어의 위치를 기준으로 합니다.
     * <p>Scheduler를 등록하여 월드보더의 축소 시간, 상태를 나타냅니다.</p>
     * @param player 플레이어의 월드보더를 가져오기위해 사용.
     */
    public void command_Play(@NotNull Player player) {
        WorldBorder wb = player.getWorld().getWorldBorder();
        Location wb_pos = wb.getCenter();
        if(wb_pos.getX() == 0 && wb_pos.getY() == 0 && wb_pos.getZ() == 0) {
            wb.setCenter(player.getLocation());
        }
        print_setting(player);
        wb.setSize(size, time);

        sec = time;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player p : player.getWorld().getPlayers()) {
                p.sendActionBar(Component.text("§c전장 축소중. . ." + "§a남은 시간 §f: " + sec));
            }
            if(sec < 0) {
                sec = time;
                for(Player p : player.getWorld().getPlayers()) {
                    p.sendActionBar(Component.text("§a축소 완료!"));
                }
                Bukkit.getScheduler().cancelTasks(plugin);

            } else {
                sec--;
            }
        }, 20L, 20L);
    }

    public boolean isBorderPlay() {
        return isPlay;
    }

    public boolean setBorderPlay(Boolean b) {
        return b;
    }

    public void command_Reset(Player player) {
        WorldBorder wb = player.getWorld().getWorldBorder();
        wb.setSize(500);
    }

    public void command_Stop(Player player) {
        Bukkit.getScheduler().cancelTasks(plugin);
        command_Reset(player);
        player.sendMessage("§a중지되었습니다.");
    }

    public void print_setting(Player player) {
        WorldBorder wb = player.getWorld().getWorldBorder();
        player.sendMessage(
                "§aWorldBorder §bsetting \n" +
                        "§b위치 §f: §ex §f:" + (int) wb.getCenter().getX() + " §ey §f:" + (int) wb.getCenter().getY() + " §ez §f:" + (int) wb.getCenter().getZ() + "\n" +
                        "§b시간 §f: §etime §f: " + time + "\n" +
                        "§b크기 §f: §esize §f: " + size
        );
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> help = new ArrayList<>();
        if(args.length == 1) {
            help.add("help");
            help.add("setting");
            help.add("play");
            help.add("reset");
            help.add("stop");
            return help;
        } else if(args.length == 2 && args[0].equalsIgnoreCase("setting")) {
            help.add("setlocation");
            help.add("settime");
            help.add("size");
            return help;
        }
        return help;
    }

}
