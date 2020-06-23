package me.MangosteenLmao.AntiLagMachine;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;



public class AntiLagMachine extends JavaPlugin implements Listener {
	
	public static AntiLagMachine plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("lagcheck").setExecutor((CommandExecutor)new LagMachineCommand()); // registers command
		
		//the following lines run the command every 30 minutes
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {			
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lagcheck");
			}
			//first run is 5 minutes after restart, then after is every 30 minutes
		}, 6000L, 36000L);
	}
}
	
