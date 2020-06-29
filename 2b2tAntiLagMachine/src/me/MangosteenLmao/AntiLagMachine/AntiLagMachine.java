package me.MangosteenLmao.AntiLagMachine;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class AntiLagMachine extends JavaPlugin implements Listener, CommandExecutor {
	
	public static AntiLagMachine plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("lagcheck").setExecutor((CommandExecutor) this); // registers command
		this.getConfig().getInt("limit");
		this.getConfig().getList("materials");
		
		//the following lines run the command every 30 minutes
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {			
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lagcheck");
			}
			//first run is 5 minutes after restart, then after is every 30 minutes
		}, 6000L, 36000L);
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("lagcheck")) {
			int limit = this.getConfig().getInt("limit"); //limit, you can change in config.yml
			if (!(sender.isOp())) { //non opped players get this message
				sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is an error.");
			}
			else if (sender.isOp() || sender.hasPermission("lagcheck")) {
				Bukkit.getServer().getLogger().info("Checking for lag machines...");
				
				Collection<? extends Player> onlinePlayerList = Bukkit.getServer().getOnlinePlayers();
				
				if (onlinePlayerList == null) {
					Bukkit.getConsoleSender().sendMessage("No players online!");
				}
				
				else {
					for (Player player : onlinePlayerList) {
						int count = 0; //total count of lag machine blocks
						//the following pulls a list of materials from config.yml. Add materials in the following format: - "material name"
						List<Material> materials = Lists.newArrayList();
						for (String materialname : plugin.getConfig().getStringList("materials")) {
							materials.add(Material.getMaterial(materialname));	
						}
						String name = player.getDisplayName();
						UUID uuid = player.getUniqueId();
						String uuidString = " (" + uuid + ")";
						
						Chunk c = player.getLocation().getChunk();
						World w = player.getLocation().getWorld();
						
						ArrayList<Location> coordinates = new ArrayList<Location>(); //array of locations of blocks
						
						int cx = c.getX() << 4; // math to get block coordinates
						int cz = c.getZ() << 4;
															
						Bukkit.getLogger().info("Currently checking: " + name + uuidString);
						//following checks blocks in chunk
						
						for (int x = cx - 16; x <= cx + 32; x++) { // the minus 16 and plus 32 check adjacent chunks too, corners included
							for (int y = 0; y <= 256; y++) {
								for (int z = cz - 16; z <= cz + 32; z++) {
									Material m = w.getBlockAt(x,  y,  z).getType(); // down below: all blocks to check for
									for (Material mat : materials) {
										if (m == mat) {
											coordinates.add(w.getBlockAt(x, y, z).getLocation()); //adds to array
											count++; 
										} else {
											//do nothing: blank on purpose
										}
									}
								}
							}
						}
								
						Bukkit.getServer().getLogger().info("Finished check for: " + name + uuidString);
						if (!(player.isOp()) && count >= limit) {
							Bukkit.getServer().getLogger().info(name + " was kicked for building a lag machine!");
							player.kickPlayer(ChatColor.GOLD + "You have lost connection to the server");	
									
							Iterator<Location> itr = coordinates.iterator();// all locations in array
									
							while (itr.hasNext()) {
								Location place = itr.next();
								place.getBlock().setType(Material.AIR); //replaces all locations with air
							}
						}
						else if (count <= limit) {
							Bukkit.getServer().getLogger().info(name + uuidString + " was clean!");
						}
					}
				}
			}
		}
		return false;
	}
}
	
