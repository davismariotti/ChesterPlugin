package info.gomeow.chester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.jmegahal.JMegaHal;

public class Chester extends JavaPlugin implements Listener {

    JMegaHal hal = new JMegaHal();

    public void firstRun(File f) {
        try {
            f.createNewFile();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        hal.add("Hello World");
        hal.add("Can I have some coffee?");
        hal.add("Please slap me");
    }

    public void transfer(ObjectInputStream in) throws ClassNotFoundException, IOException {
        hal = (JMegaHal) in.readObject();
        if(in != null) {
            in.close();
        }
    }
    
    public void startChester() {
        try {
            File chesterFile = new File(this.getDataFolder(), "brain.chester");
            File dir = new File("plugins" + File.separator + "Chester");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            File old = new File(this.getDataFolder(), "chester.brain");
            if(old.exists()) {
                transfer(new ObjectInputStream(new FileInputStream(old)));
            }
            if(chesterFile.exists()) {
                FileReader fr = new FileReader(chesterFile);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    hal.add(line);
                }
                br.close();
            } else {
                firstRun(chesterFile);
            }
        } catch(IOException ioe) {} catch(ClassNotFoundException cnfe) {}
    }
    
    public String clean(String string){
        if (string != null && string.length() > 300) {
            string = string.substring(0, 300);
         }
        String newstring = string.replaceAll("<.*?>", "").replaceAll("\\[.*?\\]", "");
        return newstring;  
    }

    public void write(String sentence) {
        File chesterFile = new File(this.getDataFolder(), "brain.chester");
        try {
            FileWriter fw = new FileWriter(chesterFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sentence + "\n");
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        startChester();
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        String chester = getConfig().getString("triggerword");
        write(clean(event.getMessage()));
        if(event.getPlayer().hasPermission("chester.trigger")) {
            getServer().getScheduler().runTask(this, new Runnable() {

                @Override
                public void run() {
                    hal.add(event.getMessage());
                }

            });
            if(event.getMessage().replaceAll("(?i)" + chester, "").length() != event.getMessage().length()) {
                getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("nickname")) + ChatColor.RESET + " " + hal.getSentence());
                    }

                }, 20L);
            }
        }
    }

}
