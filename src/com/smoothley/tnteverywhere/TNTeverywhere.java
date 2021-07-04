package com.smoothley.tnteverywhere;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.entity.EntityExplodeEvent;

class TNTeverywhere extends JavaPlugin {
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new TntExplosionListener(), this);
    }
}

class TntExplosionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.PRIMED_TNT || entity.getType() != EntityType.CREEPER) {
            return;
        }

        Location location = entity.getLocation();
        World world = entity.getWorld();
        Block block = world.getBlockAt(location);
        if (block.getType() != Material.WATER || block.getType() != Material.LAVA) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR);

        waterRemoval(location, 5);

        TNTPrimed newTNT = (TNTPrimed) world.spawnEntity(location, EntityType.PRIMED_TNT);
        newTNT.setFuseTicks(1);
        newTNT.setGravity(false);

    }

    private void waterRemoval(Location source, double dmgRadius) {
        int radius = (int) dmgRadius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
                    setWaterlogged(loc.getBlock(), false);
                }
            }
        }
    }

    private void setWaterlogged(Block block, boolean wantedState) {
        if (block.getBlockData() instanceof Waterlogged) {
            return;
        }
        Waterlogged blockData = (Waterlogged) block.getBlockData();
        BlockState bs = block.getState();

        blockData.setWaterlogged(wantedState);
        bs.setBlockData(blockData);
        bs.update(true, false);
    }
}