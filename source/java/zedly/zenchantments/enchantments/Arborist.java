package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static zedly.zenchantments.MaterialList.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND , conflicting = {})
public final class Arborist extends Zenchantment {
    private static final Map<Material, Material> LEAF_TO_SAPLING = Map.ofEntries(
        Map.entry(OAK_LEAVES, OAK_SAPLING),
        Map.entry(BIRCH_LEAVES, BIRCH_SAPLING),
        Map.entry(SPRUCE_LEAVES, SPRUCE_SAPLING),
        Map.entry(ACACIA_LEAVES, ACACIA_SAPLING),
        Map.entry(DARK_OAK_LEAVES, DARK_OAK_SAPLING),
        Map.entry(JUNGLE_LEAVES, JUNGLE_SAPLING),
        Map.entry(AZALEA_LEAVES, AZALEA),
        Map.entry(FLOWERING_AZALEA_LEAVES, FLOWERING_AZALEA),
        Map.entry(MANGROVE_LEAVES, MANGROVE_PROPAGULE),
        Map.entry(CHERRY_LEAVES, CHERRY_SAPLING),
        Map.entry(PALE_OAK_LEAVES, PALE_OAK_SAPLING)
    );

    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();
        final Material material = block.getType();

        if (!LEAVES.contains(material)) {
            return false;
        }

        // Check probability based on level and power
        if (!(ThreadLocalRandom.current().nextInt(10) >= (9 - level) / (this.getPower() + 0.001))) {
            return false;
        }

        // Get the corresponding sapling from the map (null-safe, but all leaves should have entries)
        Material sapling = LEAF_TO_SAPLING.get(material);
        if (sapling != null && ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(sapling, 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(STICK, 1));
        }

        if (ThreadLocalRandom.current().nextInt(3) % 3 == 0) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(APPLE, 1));
        }

        if (ThreadLocalRandom.current().nextInt(65) == 25) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(GOLDEN_APPLE, 1));
        }

        return true;
    }
}
