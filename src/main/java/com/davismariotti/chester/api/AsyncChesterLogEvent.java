package com.davismariotti.chester.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncChesterLogEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private String message;
    private boolean cancel = false;

    public AsyncChesterLogEvent(Player player, String message) {
        super(true);
        this.player = player;
        this.message = message;
    }

    /**
     * @return The player who triggered Chester
     */
    public Player getPlayer() {
        return player;
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
     * Sets the message that will be logged into chester's log files.
     * <br>
     * This message can contain color codes
     *
     * @param message The new message to be logged.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
