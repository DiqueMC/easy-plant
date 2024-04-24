package com.diquemc.easyplant;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerListener implements Listener {
    private final Map<Material, Material> crops;

    public PlayerListener() {
        this.crops = Map.ofEntries(
                Map.entry(Material.WHEAT_SEEDS, Material.WHEAT),
                Map.entry(Material.CARROT, Material.CARROTS),
                Map.entry(Material.POTATO, Material.POTATOES),
                Map.entry(Material.BEETROOT_SEEDS, Material.BEETROOTS),
                Map.entry(Material.MELON_SEEDS, Material.MELON_STEM),
                Map.entry(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM),
                Map.entry(Material.TORCHFLOWER_SEEDS, Material.TORCHFLOWER_CROP),
                Map.entry(Material.PITCHER_POD, Material.PITCHER_CROP)
        );

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlant(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() || player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getBlockFace() != BlockFace.UP) {
            return;
        }
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (block == null || block.getType() != Material.FARMLAND) {
            return;
        }

        if (item == null || !this.crops.containsKey(event.getMaterial())) {
            return;
        }
        plant(item, event.getClickedBlock());

    }


    private void plant(ItemStack cropStack, Block startingBlock) {
        Queue<Block> blockQueue = new LinkedList<>();
        blockQueue.add(startingBlock);
        List<Block> visitedBlocks = new ArrayList<>();
        Material cropToPlant = this.crops.get(cropStack.getType());

        while (blockQueue.peek() != null && cropStack.getAmount() >= 1) {
            Block currentBlock = blockQueue.remove();

            if (!visitedBlocks.contains(currentBlock) && currentBlock.getType() == Material.FARMLAND) {
                Location locationAbove = currentBlock.getLocation().add(0, 1, 0);
                if (locationAbove.getBlock().getType().equals(Material.AIR)) {
                    blockQueue.add(currentBlock.getLocation().add(1, 0, 0).getBlock());
                    blockQueue.add(currentBlock.getLocation().add(-1, 0, 0).getBlock());
                    blockQueue.add(currentBlock.getLocation().add(0, 0, 1).getBlock());
                    blockQueue.add(currentBlock.getLocation().add(0, 0, -1).getBlock());
                    locationAbove.getBlock().setType(cropToPlant);
                    cropStack.setAmount(cropStack.getAmount() - 1);
                }
            }
            visitedBlocks.add(currentBlock);
        }
    }
}
