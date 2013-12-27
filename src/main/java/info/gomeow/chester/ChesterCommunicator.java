package info.gomeow.chester;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jibble.jmegahal.JMegaHal;

import info.gomeow.chester.API.ChesterBroadcastEvent;
import info.gomeow.chester.API.AsyncChesterLogEvent;

public class ChesterCommunicator implements Runnable {
    private static final Random RAND = new Random();
    private final BlockingQueue<String> input = new LinkedBlockingQueue<String>();
    private final JMegaHal brain;
    private final List<String> triggers;
    private final Chester plugin;
    private final Thread thread;

    public ChesterCommunicator(Chester plugin, JMegaHal brain, List<String> triggers) {
        this.plugin = plugin;
        this.brain = brain;
        this.triggers = triggers;
        this.thread = new Thread(null, this, "Chester");
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = input.take();
                for (final String trigger : triggers) {
                    if (message.matches("^.*(?i)" + trigger + ".*$")) {
                        final String sentence = getSentence(trigger, message);

                        new BukkitRunnable() {
                            public void run() {
                                final ChesterBroadcastEvent cbe = new ChesterBroadcastEvent(sentence);
                                plugin.getServer().getPluginManager().callEvent(cbe);
                                String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("nickname")) + " ";
                                ChatColor color = ChatColor.getByChar(plugin.getConfig().getString("chatcolor"));
                                String msg = ChatColor.translateAlternateColorCodes('&', sentence);
                                for(Player plyer : cbe.getRecipients()) {
                                    plyer.sendMessage(name + color + msg);
                                }
                                System.out.println(ChatColor.stripColor(msg));
                            }
                        }.runTaskLater(plugin, 1L);
                        break;
                    }
                }
            }
        } catch (InterruptedException ex) {
            // We're done.
        }
    }

    private synchronized String getSentence(String trigger, String message) {
        String sentence = brain.getSentence();
        while (sentence.matches("^.*(?i)" + trigger + ".*$")) {
            sentence = brain.getSentence(message.replaceAll("(?i)" + trigger, "").split(" ")[RAND.nextInt(message.split(" ").length)]);
        }
        return sentence;
    }

    /**
     * Adds a sentence to the JMegaHal object provided when this
     * ChesterCommunicator was instantiated. Any changes to the JMegaHal
     * object should be handled through this method as it is synchronized
     * with all other access to the JMegaHal object.
     *
     * @param sentence the sentence to add
     */
    public synchronized void addSentenceToBrain(String sentence) {
        brain.add(sentence);
    }

    /**
     * Adds a message to Chester's handling Queue. This method is thread safe
     * and should be called after a {@link AsyncChesterLogEvent} has been fired.
     *
     * @param sentence the sentence to queue
     */
    public void queueMessage(String sentence) {
        input.add(sentence);
    }

    /**
     * Stops the handling of messages by the Consumer task.
     */
    public void stop() {
        this.thread.interrupt();
    }
}
