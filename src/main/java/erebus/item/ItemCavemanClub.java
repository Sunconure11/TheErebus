package erebus.item;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import erebus.ModMaterials;
import erebus.ModTabs;
import erebus.world.feature.structure.AntHillMazeDungeon;

public class ItemCavemanClub extends ItemSword {

	public ItemCavemanClub() {
		super(ModMaterials.toolCAVEMANCLUB);
		setCreativeTab(ModTabs.gears);
	}

	@Override
	public boolean getIsRepairable(ItemStack tool, ItemStack material) {
		return Items.bone == material.getItem() ? true : super.getIsRepairable(tool, material);
	}
	
	// Remove onItemUse method completely after testing is over!!!!
		@Override
		public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
			Random rand = new Random();
			if (!world.isRemote && player.isSneaking()) {
				new AntHillMazeDungeon().generate(world, rand, x, y + 1, z);
				return true;
			}
			return true;
		}
}
