
package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static zedly.zenchantments.MaterialList.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Pierce.class, Switch.class, Shred.class})
public final class Tunnel extends Zenchantment {
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();

        if (!SHRED_PICKS.contains(block.getType()) && !SHRED_SHOVELS.contains(block.getType())) {
            return false;
        }

        final ItemStack hand = event.getPlayer().getInventory().getItem(slot);
        final Player player = event.getPlayer();

        // Determine the face the player is looking at
        BlockFace face = getTargetBlockFace(player);

        // Break blocks in a 3x3 area
        breakArea3x3(
            block,
            face,
            level,
            player,
            WorldConfigurationProvider.getInstance().getConfigurationForWorld(block.getWorld()),
            hand.getType(),
            slot
        );

        return true;
    }

    private BlockFace getTargetBlockFace(final @NotNull Player player) {
        // Get the direction the player is facing
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        // If looking up or down significantly, mine horizontally
        if (pitch < -45) {
            return BlockFace.UP;
        } else if (pitch > 45) {
            return BlockFace.DOWN;
        }

        // Otherwise determine horizontal direction
        // This will create a vertical 3x3 wall
        return BlockFace.NORTH;
    }

    private void breakArea3x3(
        final @NotNull Block centerBlock,
        final @NotNull BlockFace face,
        final int level,
        final @NotNull Player player,
        final @NotNull WorldConfiguration config,
        final @NotNull Material itemType,
        final EquipmentSlot usedHand
    ) {
        Set<Block> blocksToBreak = new HashSet<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block relative = centerBlock.getRelative(x, y, z);
                    blocksToBreak.add(relative);
                }
            }
        }

        // Break all blocks in the area
        for (Block block : blocksToBreak) {
            if (MaterialList.AIR.contains(block.getType())) {
                continue;
            }

            // Check if the tool can break this block type
            if ((Tool.PICKAXE.contains(itemType) && !SHRED_PICKS.contains(block.getType()))
                || (Tool.SHOVEL.contains(itemType) && !SHRED_SHOVELS.contains(block.getType()))
            ) {
                continue;
            }

            Material originalType = block.getType();
            WorldInteractionUtil.breakBlock(block, player);

            // Play break sound
            Sound sound = getBreakSound(originalType);
            if (sound != null) {
                requireNonNull(block.getLocation().getWorld())
                    .playSound(block.getLocation(), sound, 0.5f, 1);
            }

            // Damage the tool
            Utilities.damageItemStackRespectUnbreaking(player, 1, usedHand);
        }
    }

    private Sound getBreakSound(final @NotNull Material material) {
        switch (material) {
            case GRASS_BLOCK:
                return Sound.BLOCK_GRASS_BREAK;
            case DIRT:
            case GRAVEL:
            case CLAY:
                return Sound.BLOCK_GRAVEL_BREAK;
            case SAND:
                return Sound.BLOCK_SAND_BREAK;
            case AIR:
                return null;
            default:
                return Sound.BLOCK_STONE_BREAK;
        }
    }
}
