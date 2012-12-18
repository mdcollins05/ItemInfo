package com.blockmovers.plugins.iteminfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemInfo extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    private Configuration config = new Configuration(this);
    private List playerTool = new ArrayList();

    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        getServer().getPluginManager().registerEvents(this, this);

        config.loadConfiguration();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }

    public String getInfo(ItemStack item) {
        String itemName = item.getType().toString();
        String itemID = Integer.toString(item.getTypeId());
        String itemData = "";
        String itemIDD = "";

        itemIDD = "(ID: " + itemID + ")";

        if (Byte.toString(item.getData().getData()) != null) {
            itemData = Byte.toString(item.getData().getData());
            itemData = itemData.replaceAll("[^0-9]", "");
            if (!itemData.equals("0")) {
                itemIDD = "(ID: " + itemID + ":" + itemData + ")";
            }
        }

        return this.replaceText(this.config.toolCommand, "", itemName, itemIDD);
    }

    public String getInfo(Block i) {
        Material mat = Material.getMaterial(i.getTypeId());
        ItemStack item = new ItemStack(mat.getId(), mat.getMaxStackSize(), mat.getMaxDurability(), i.getData());
        return this.getInfo(item);
    }

    public String replaceText(String string, String item, String name, String data) {
        string = string.replaceAll("\\$d", data);
        string = string.replaceAll("\\$i", item);
        string = string.replaceAll("\\$n", name);
        string = string.replaceAll("&(?=[0-9a-f])", "\u00A7");
        return string;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (!this.playerTool.contains(p)) {
            return;
        }

        Integer itemInHand = p.getItemInHand().getTypeId();

        if (!itemInHand.equals(this.config.toolID)) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        p.sendMessage(this.getInfo(clickedBlock));
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            if (args.length >= 1) {
                if (args[0].equals("tool") || args[0].equals("t")) {
                    if (p.hasPermission("iteminfo.tool")) {
                        if (this.playerTool.contains(p)) {
                            this.playerTool.remove(p);
                            p.sendMessage(this.replaceText(config.toolDisable, "", "", ""));
                        } else {
                            this.playerTool.add(p);
                            String toolName = Material.getMaterial(this.config.toolID).toString();
                            p.sendMessage(this.replaceText(config.toolEnable, toolName, "", ""));
                        }
                        return true;
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                        return true;
                    }
                }
            } else {
                if (p.hasPermission("iteminfo.hand")) {
                    ItemStack item = p.getItemInHand();
                    p.sendMessage(this.getInfo(item));
                    return true;
                } else {
                    p.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                    return true;
                }
            }
        } else {
            cs.sendMessage(ChatColor.RED + "You must be in the world to use this command.");
            return true;
        }
        return true;
    }
}
