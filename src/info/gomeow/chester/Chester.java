package info.gomeow.chester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.jmegahal.JMegaHal;

public class Chester extends JavaPlugin implements Listener {

	JMegaHal hal = new JMegaHal();
	
	public void firstRun() {
		hal.add("Hello World");
		hal.add("Can I have some coffee?");
		hal.add("Please slap me");
	}
	
	public void startChester() {
		// load any previously saved brain
		ObjectInputStream in = null;
		try {
			File dir = new File("plugins"+File.separator+"Chester");
			if(!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(this.getDataFolder(),"chester.brain");
		    in = new ObjectInputStream(new FileInputStream(file));
			hal = (JMegaHal) in.readObject();
		} catch (FileNotFoundException e) {
			firstRun();
		} catch (IOException e) {
			firstRun();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().header("Trigger word is case insensitive.");
		getConfig().options().copyHeader(true);
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("triggerword","chester");
		getConfig().addDefault("nickname","<_Chester_>");
		saveConfig();
		
		startChester();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String chester = getConfig().getString("triggerword");
		if(event.getPlayer().hasPermission("chester.trigger")) {
			hal.add(event.getMessage());
			try {
				File dir = new File("plugins"+File.separator+"Chester");
				if(!dir.exists()) {
					dir.mkdirs();
				}
		    	ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File(this.getDataFolder(),"chester.brain")));
				out.writeObject(hal);
		        out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(event.getMessage().replaceAll("(?i)"+chester,"").length()!=event.getMessage().length()) {
				getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	
					@Override
					public void run() {
						Bukkit.broadcastMessage(getConfig().getString("nickname")+" "+hal.getSentence());
					}
					
				}, 20L);
			}
		}
	}
	
}
