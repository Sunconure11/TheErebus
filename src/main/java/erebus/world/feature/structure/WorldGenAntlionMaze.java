package erebus.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import erebus.ModBlocks;
import erebus.world.feature.util.BasicShapeGen;


public class WorldGenAntlionMaze extends WorldGenerator  {

    private Block solid = ModBlocks.gneiss;

/*	public MazeSystem()
	{
		range = 8; //Radius of chunks that it will generate before trying to generate  8 chunks = 8*16 blocks
	}
	
	@Override
	protected void func_151538_a(World world, int localX, int localZ, int chunkX, int chunkZ, Block[] blocks)
	{
		if (rand.nextInt(20) == 0) // Should probably change this
		{*/
    public boolean generate(World world, Random rand, int x, int y, int z) {
			
			int sizeX = 48;
			int sizeY = y+4;
			int sizeZ = 48;

			//if (world.getBiomeGenForCoords(x, z).biomeID == ModBiomes.volcanicDesertID)
			//{
			        int mazeWidth = sizeX/2;
			        int mazeHeight = sizeZ/2;
			        if (mazeWidth < 2 || mazeHeight < 2 || sizeY < 1) {
			        	return false;
			        }
			        int[][] maze = null;
			        MazeGenerator generator = new PerfectMazeGenerator(mazeWidth, mazeHeight);
                    maze = generator.generateMaze();
			        for (int yy = y; yy < sizeY; yy++) {
			            switch (yy % 4) {
			             case 0:
			                   buildFloor(world, x, yy - 4, z, mazeWidth, mazeHeight, rand);
			                   buildRoof(world, x, yy, z, mazeWidth, mazeHeight);
			                   break;
			                case 1:
			                	buildLevel(world, x, yy - 4, z, mazeWidth, mazeHeight, maze, solid, 2);
			                    buildLevel(world, x, yy - 3, z, mazeWidth, mazeHeight, maze, solid, 1);
			                    buildLevel(world, x, yy - 2, z, mazeWidth, mazeHeight, maze, solid, 2);
			                    addTorch(world, x, yy - 3 , z, mazeWidth, mazeHeight, maze, rand);
			                    break;
			            }
			       }
			   
			        BasicShapeGen.createPyramid(world, Blocks.sandstone, 0, true, x+sizeX/2+8, z+sizeZ/2+8, 32, 32, y - 6);
			        return true;
			//}
		//}
	}

    private void buildRoof(World world, int x, int y, int z, int w, int h) {
        for (int i = 0; i <= h * 4; i++) {
            for (int j = 0; j <= w * 4; j++) {
                world.setBlock(x + j, y, z + i, ModBlocks.ghostSand);
            }
        }
    }
    
    private void buildFloor(World world, int x, int y, int z, int w, int h, Random rand) {
        for (int i = 0; i <= h * 4; i++) {
            for (int j = 0; j <= w * 4; j++) {
            	for(int k = 0; k <= 3; k++)
                world.setBlock(x + j, y + k, z + i, Blocks.air);
            }
        }
        for (int i = 0; i <= h * 4; i++) {
            for (int j = 0; j <= w * 4; j++) {
            	if (rand.nextInt(10) == 0)
            		world.setBlock(x + j, y, z + i, Blocks.lava);
            	else
            		world.setBlock(x + j, y, z + i, solid, 5, 2);
            }
        }
    }
    
    private void addTorch(World world, int x, int y, int z, int w, int h, int[][] maze, Random rand) {
        for (int i = 0; i < h; i++) {
            // draw the north edge
            for (int j = 0; j < w; j++) {
                if ((maze[j][i] & 1) == 0) {
                    if(rand.nextInt(25) == 0) {
                    	world.setBlock(x + 1 + j * 4, y, z + 1 + i * 4, Blocks.torch, 3, 2);
                    	if(rand.nextInt(2) == 0)
                    		world.setBlock(x + 1 + j * 4, y - 1, z + 1 + i * 4, Blocks.chest, 3, 2);
                    }
                }
            }

            for (int j = 0; j < w; j++) {
            	if ((maze[j][i] & 8) == 0) {
            		if(rand.nextInt(25) == 0) {
            			world.setBlock(x + 1 + j * 4, y, z + 1 + i * 4, Blocks.torch, 1, 2);
            			if(rand.nextInt(2) == 0)
            				world.setBlock(x + 1 + j * 4, y - 1, z + 1 + i * 4, Blocks.chest, 1, 2);
            		}
            	}
            }
        }
	}

    private void buildLevel(World world, int x, int y, int z, int w, int h, int[][] maze, Block blockType, int blockMeta) {
        for (int i = 0; i < h; i++) {
            // draw the north edge
            for (int j = 0; j < w; j++) {
                if ((maze[j][i] & 1) == 0) {
                    world.setBlock(x + j * 4, y, z + i * 4, blockType, blockMeta, 2);
                    world.setBlock(x + j * 4 + 1, y, z + i * 4, blockType, blockMeta, 2);
                    world.setBlock(x + j * 4 + 2, y, z + i * 4, blockType, blockMeta, 2);
                    world.setBlock(x + j * 4 + 3, y, z + i * 4, blockType, blockMeta, 2);
                } else {
                    world.setBlock(x + j * 4, y, z + i * 4, blockType, blockMeta, 2);
                }
            }
            // draw the west edge
            for (int j = 0; j < w; j++) {
                if ((maze[j][i] & 8) == 0) {
                    world.setBlock(x + j * 4, y, z + i * 4 + 1, blockType, blockMeta, 2);
                    world.setBlock(x + j * 4, y, z + i * 4 + 2, blockType, blockMeta, 2);
                    world.setBlock(x + j * 4, y, z + i * 4 + 3, blockType, blockMeta, 2);
                }
            }
            world.setBlock(x + w * 4, y, z + i * 4, blockType, blockMeta, 2);
            world.setBlock(x + w * 4, y, z + i * 4 + 1, blockType, blockMeta, 2);
            world.setBlock(x + w * 4, y, z + i * 4 + 2, blockType, blockMeta, 2);
            world.setBlock(x + w * 4, y, z + i * 4 + 3, blockType, blockMeta, 2);
        }
        // draw the bottom line
        for (int j = 0; j <= w * 4; j++) {
        	world.setBlock(x + j, y, z + h * 4, blockType, blockMeta, 2);
        }

    }

}
