package com.github.minetonight.desertgenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;


public class DesertGenerator extends JavaPlugin {

	@Override
	public void onLoad() {
		System.out.println("Ready to generate some desert");
	}
	
	@Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new DesertChunkGenerator();
    }
}
