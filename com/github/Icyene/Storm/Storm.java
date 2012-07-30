/*
 * Storm
 * Copyright (C) 2012 Icyene
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.Icyene.Storm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.Icyene.Storm.Hail.Hail;
import com.github.Icyene.Storm.Lightning.Lightning;
import com.github.Icyene.Storm.Rain.Acid.AcidRain;
import com.github.Icyene.Storm.Snow.Snow;

public class Storm extends JavaPlugin
{

    public final Logger log = Logger.getLogger("Minecraft");
    static final String prefix = "[Storm] ";
    public static boolean debug = true;
    
    @Override
    public void onEnable()
    {
	try {
	    Snow.load(this);
	    AcidRain.load(this);
	    Lightning.load(this);
	    Hail.load(this);
	      
	    //final World defWorld = Bukkit.getServer().getWorld("world"); 

	    
	    
	} catch (Exception e) {

	    e.printStackTrace();
	    crashDisable("Failed to initialize subplugins.");

	}
    }

    public void crashDisable(String crash)
    {
	StormUtil.log(Level.SEVERE, prefix + crash + " Storm disabled.");
	this.setEnabled(false);
    }
}



//final List<Triple<Double, Double, Double>> plots = PlotPoints.pointOnCircle(0.4, 1, 70, 1, 5, 0.2, 0.25, 8.0);
//
//Bukkit.getScheduler()
//.scheduleSyncRepeatingTask(this,
//	    new Runnable()
//	    {
//		@Override
//		public void run()
//		{
//		    
//		    for(Triple<Double, Double, Double> t : plots ) {
//			
//			PlotPoints.smoke(new Location(defWorld, t.x, t.y, t.z), 4, 200);
//		
//			
//		    }
//		    
//		}
//	    }, 0, 50);