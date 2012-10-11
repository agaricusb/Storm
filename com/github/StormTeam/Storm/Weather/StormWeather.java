package com.github.StormTeam.Storm.Weather;

import com.github.StormTeam.Storm.Storm;
import java.util.Set;

/**
 * Base class of all Weathers
 */
public abstract class StormWeather {
    /**
     * Constructor. DO NOT CHANGE ARGUMENTS.
     * 
     * @param storm
     * @param world 
     */
    public StormWeather(Storm storm, String world) {
        this.storm = storm;
        this.world = world;
    }
    
    /**
     * Called when the weather is started.
     */
    public abstract void start();
    
    /**
     * Called when the weather is stopped.
     */
    public abstract void end();
    
    /**
     * Gets the texture of the weather.
     * 
     * @return URL to texture if there is a texture, else null.
     */
    public abstract String getTexture();
    
    /**
     * Gets a Set<String> of conflicting weathers.
     * 
     * @return A Set<String> of conflicting weathers.
     */
    public abstract Set<String> getConflicts();
    
    /**
     * Set to true to enable minecraft rain mode.
     */
    public boolean needRainFlag = false;
    
    /**
     * Set to true to enable minecraft thunder mode.
     */
    public boolean needThunderFlag  = false;
    
    /**
     * Stores the world name this class manages.
     */
    public String world;
    
    /**
     * The storm plugin object.
     */
    public Storm storm;
}
