package com.gestankbratwurst.spigotsandbox.example;

import com.gestankbratwurst.spigotsandbox.bareboneframework.GUIHandler;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExampleGUI implements GUIHandler {

  @Override
  public void prepareInv(final Inventory inventory) {
    for (int slot = 0; slot < inventory.getSize(); slot++) {
      inventory.setItem(slot, this.getSlotItem(slot));
    }
  }

  private ItemStack getSlotItem(final int slotID) {
    final ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    final ItemMeta meta = itemStack.getItemMeta();
    meta.setDisplayName("§eSlotID: §f" + slotID);
    itemStack.setItemMeta(meta);
    itemStack.setAmount(slotID);
    return itemStack;
  }

  @Override
  public void handleClick(final InventoryClickEvent event) {
    event.getWhoClicked().sendMessage("§eYou clicked slot §f" + event.getSlot());
    event.setCancelled(true);
  }

}
