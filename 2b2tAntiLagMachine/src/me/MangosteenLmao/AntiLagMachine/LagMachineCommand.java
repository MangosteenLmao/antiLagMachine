package me.MangosteenLmao.AntiLagMachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

public class LagMachineCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("lagcheck")) {
			int limit = 1000; //limit, you can change
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
									if (m == Material.REDSTONE_WIRE || 
										m == Material.PISTON_BASE || 
										m == Material.PISTON_EXTENSION || 
										m == Material.PISTON_STICKY_BASE || 
										m == Material.REDSTONE_TORCH_ON ||
										m == Material.REDSTONE_TORCH_OFF ||
										m == Material.OBSERVER || 
										m == Material.REDSTONE_BLOCK|| 
										m == Material.PISTON_MOVING_PIECE ||
										m == Material.REDSTONE_COMPARATOR ||
										m == Material.REDSTONE_COMPARATOR_OFF || 
										m == Material.REDSTONE_COMPARATOR_ON ||
										m == Material.DIODE ||
										m == Material.DIODE_BLOCK_OFF ||
										m == Material.DIODE_BLOCK_ON ||
										m == Material.SLIME_BLOCK) {
												
											coordinates.add(w.getBlockAt(x, y, z).getLocation()); //adds to array
			
											count++; 
										}
									}
								}
							}
								
								Bukkit.getServer().getLogger().info("Finished check for: " + name + uuidString);
								if (!(player.isOp()) && count >= limit) {
									Bukkit.getServer().getLogger().info(name + " was kicked for building a lag machine!");
									player.kickPlayer(ChatColor.GOLD + "You have lost connection to the server");	
									
									Iterator<Location> itr = coordinates.iterator(); //iterates through all locations in the array
									
									while (itr.hasNext()) {
										Location place = itr.next();
										place.getBlock().setType(Material.AIR); //replaces all locations with air
									}
								}
								else if (count <= limit) {
									Bukkit.getServer().getLogger().info(name + uuidString + " was clean!");
								}
								
								count = 0;
							}
				}
			}
		}
		return false;
	}
}