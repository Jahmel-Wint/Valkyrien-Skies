package org.valkyrienskies.mod.common.block;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.valkyrienskies.mod.common.physmanagement.relocation.DetectorManager;

@ParametersAreNonnullByDefault
public class BlockPhysicsInfuserCreative extends BlockPhysicsInfuser {

    public BlockPhysicsInfuserCreative(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
        ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.valkyrienskies.creative_physics_infuser_1"));
        tooltip.add(TextFormatting.RED + "" + TextFormatting.ITALIC + I18n.format("tooltip.valkyrienskies.creative_physics_infuser_2"));
    }

    @Override
    public DetectorManager.DetectorIDs getShipSpawnDetectorID() {
        return DetectorManager.DetectorIDs.BlockPosFinder;
    }

}