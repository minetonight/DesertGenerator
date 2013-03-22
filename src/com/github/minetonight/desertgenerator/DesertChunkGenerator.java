package com.github.minetonight.desertgenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class DesertChunkGenerator extends ChunkGenerator {

	@Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList();
    }
	
	/** This needs to be set to return true to override minecraft's default behaviour*/
    @Override
    public boolean canSpawn(World world, int x, int z) {
        //TODO return getChunkType(x, y) != LAVA;
        return true;
    }
    
    public static void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }
	
    public static byte getBlock(byte[][] result, int x, int y, int z) {
        if (result[y >> 4] == null) {
            return (byte)0;
        }
        return result[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
    }
    
    public static final double RADUIS = 47.0d;
	
    
    @Override
    public byte[][] generateBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
    	byte[][] result = new byte[world.getMaxHeight() / 16][]; //world height / chunk part height (=16, look above)
		
    	ChunkType chunkType = getChunkType(world, random, cx, cz);
//    	System.out.println("chunkType=" + chunkType);
//    	System.out.println("------");

    	switch (chunkType) {
		case DEFAULT:
			generateDesert(result);
			break;
		case LAVA:
			generateLavaSea(result);
			break;
		case ABYSS:
			generateAbyss(world, random, cx, cz, result);
			break;
		default:
			generateDesert(result);
			break;
		}
    	//TODO FIXME
    	generateAbyss(world, random, cx, cz, result);
    	return result;
    }


	private ChunkType getChunkType(World world, Random random, int cx, int cz) {
		
		//create world coordinates
//		int wxmin = (cx << 4);
//		int wzmin = (cz << 4);
//		int wxmax = wxmin + 16;
//		int wzmax = wzmin + 16;
		
		double eq = cx*cx + cz*cz;
		
		
		if (eq > (RADUIS/16.0)) {
			return ChunkType.LAVA;
		}
		else if ((RADUIS/16.0)-8 < eq || eq < (RADUIS/16.0)+8) {
			return ChunkType.ABYSS;
		}
		
		return ChunkType.DEFAULT;
	}

	private void generateDesert(byte[][] result) {
		int y = 0;
    	for(int x = 0; x < 16; x++){
    		for(int z = 0; z < 16; z++) {
    			for (y = 50; y > 0; y--) {
    				setBlock(result, x, y, z, (byte)Material.SAND.getId());
				}
    			
    			setBlock(result, x, y, z, (byte)Material.BEDROCK.getId());
    		}
    	}
	}
	
	private void generateAbyss(World world, Random random, int cx, int cz, byte[][] result) {
		int y = 0;
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++) {
				
				//create world coordinates
				int wx = (cx << 4) + x;
				int wz = (cz << 4) + z;
				
				double eq = wx*wx + wz*wz;
				double border = RADUIS * RADUIS;
				
				if (eq <= border) { 
					y = 50;
//					System.out.println("cx=" + cx);
//					System.out.println("cz=" + cz);
//					System.out.println("wx=" + wx);
//					System.out.println("wz=" + wz);
//					System.out.println("---------------");
				}
				else {
					y = (int) (50 - Math.sqrt(eq - border)); // TODO on large dist - steep edge???
					y -= random.nextInt(2);
				}

				if (y < 35) {
					for (y = 35; y > 0; y--) {
						setBlock(result, x, y, z, (byte)Material.LAVA.getId());
					}
				} else {
					for (;y > 0; y--) {
						setBlock(result, x, y, z, (byte)Material.SAND.getId());
					}
				}
				
				setBlock(result, x, y, z, (byte)Material.BEDROCK.getId());
			}
		}
	}
	
	private void generateLavaSea(byte[][] result) {
		int y = 0;
    	for(int x = 0; x < 16; x++){
    		for(int z = 0; z < 16; z++) {
    			for (y = 35; y > 0; y--) {
    				setBlock(result, x, y, z, (byte)Material.LAVA.getId());
				}
    			
    			setBlock(result, x, y, z, (byte)Material.BEDROCK.getId());
    		}
    	}
	}
    
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
    	int x = random.nextInt(10);
    	int z = random.nextInt(10);
    	int y = world.getHighestBlockYAt(x, z);
    	return new Location(world, x, y, z);
    }
}
