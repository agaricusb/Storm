package com.github.StormTeam.Storm.Weather;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Tudor
 */

class WeatherEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    private World affectedWorld;
    private boolean weatherState;
    private String weatherName;

    public WeatherEvent(World world, boolean state, String weather) {
        this.affectedWorld = world;
        this.weatherState = state;
        this.weatherName = weather;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.isCancelled = flag;

    }

    public World getAffectedWorld() {
        return this.affectedWorld;
    }

    public String getWeather() {
        return this.weatherName;
    }

    public boolean getWeatherState() {
        return this.weatherState;
    }

}