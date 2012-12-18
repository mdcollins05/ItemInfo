/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.iteminfo;

/**
 *
 * @author MattC
 */
public class Configuration {

    ItemInfo plugin = null;
    public Integer toolID = null;
    public String toolEnable = null;
    public String toolDisable = null;
    public String toolCommand = null;

    public Configuration(ItemInfo plugin) {
        this.plugin = plugin;
    }

    public void loadConfiguration() {
        plugin.getConfig().addDefault("toolID", 352);
        plugin.getConfig().addDefault("toolEnable", "&3[&bII&3]&f Enabled. Right click on blocks with a $i to get info.");
        plugin.getConfig().addDefault("toolDisable", "&3[&bII&3]&f Disabled.");
        plugin.getConfig().addDefault("toolCommand", "&3[&bII&3]&f That is: $n $d");
        plugin.getConfig().options().copyDefaults(true);
        //Save the config whenever you manipulate it
        plugin.saveConfig();

        this.setVars();
    }

    public void setVars() {
        toolID = plugin.getConfig().getInt("toolID");
        toolEnable = plugin.getConfig().getString("toolEnable");
        toolDisable = plugin.getConfig().getString("toolDisable");
        toolCommand = plugin.getConfig().getString("toolCommand");
    }
}
