package info.gomeow.chester.API;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChesterBroadcastEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Set<Player> recipients;
    private String message;

    public ChesterBroadcastEvent(String message) {
        this.message = message;
        recipients = new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));
    }

    /**
     * Gets the message that will be logged into chester's log files.
     * 
     * @return The message to be logged
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message that will be broadcasted.
     * <br>
     * Modifying this message is not recommended.
     * <br>
     * This message can contain color codes.
     *
     * @param message The new message to be logged.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets a set of recipients that this message will be displayed to.
     *
     * @return All Players who will see this chat message
     */
    public Set<Player> getRecipients() {
        return recipients;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
