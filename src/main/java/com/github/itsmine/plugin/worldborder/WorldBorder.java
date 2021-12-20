package com.github.itsmine.plugin.worldborder;

import com.github.itsmine.plugin.worldborder.commands.BorderCommand;
import com.github.itsmine.plugin.worldborder.commands.WorldReset;
import com.github.itsmine.plugin.worldborder.listeners.ClickInventory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class WorldBorder extends JavaPlugin {

    public static World[] worlds = new World[4];

    @Override
    public void onEnable() {
        /* Todo
         * World 생성 -> 불러오기
         * 현재 생성된 월드들을 정적 필드에 넣기
         *
         * worlds 배열에 현재 생성된 월드들을 간편하게 넣을수 있는 방법은 없나?
         */

        Objects.requireNonNull(getCommand("mf")).setExecutor(new BorderCommand(this));
        Objects.requireNonNull(getCommand("wr")).setExecutor(new WorldReset(this));
        getServer().getPluginManager().registerEvents(new ClickInventory(), this);
        worlds[0] = Bukkit.createWorld(new WorldCreator("world"));
        worlds[1] = Bukkit.createWorld(new WorldCreator("world2"));
        worlds[2] = Bukkit.createWorld(new WorldCreator("newWorld"));
        worlds[3] = Bukkit.createWorld(new WorldCreator("copyworld"));

    }

    @Override
    public void onDisable() {

    }
}
