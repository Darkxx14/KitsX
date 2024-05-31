package dev.darkxx.kitsx.menus;

import dev.darkxx.kitsx.KitsX;
import dev.darkxx.kitsx.menus.config.MenuConfig;
import dev.darkxx.utils.menu.xmenu.GuiBuilder;
import dev.darkxx.utils.menu.xmenu.ItemBuilderGUI;
import dev.darkxx.utils.text.color.ColorizeText;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;

public class AutoRekitMenu extends GuiBuilder {

    private static final KitsX PLUGIN = KitsX.getInstance();
    private static final MenuConfig CONFIG = new MenuConfig(PLUGIN, "menus/auto-rekit.yml");
    private static final Logger LOGGER = PLUGIN.getLogger();


    public AutoRekitMenu() {
        super(CONFIG.getConfig().getInt("auto-rekit.size"));
    }

    public static GuiBuilder openAutoRekitMenu(Player player) {
        GuiBuilder inventory = new GuiBuilder(CONFIG.getConfig().getInt("auto-rekit.size", 27), CONFIG.getConfig().getString("auto-rekit.title", "Auto Rekit"));

        addFilterItems(inventory);
        addToggleAutoRekitItem(inventory, player);
        addKitItems(inventory, player);

        return inventory;
    }

    private static void addFilterItems(GuiBuilder inventory) {
        List<Integer> filterSlots = CONFIG.getConfig().getIntegerList("auto-rekit.filter.slots");

        ItemStack filter = createItem("auto-rekit.filter", Material.BLACK_STAINED_GLASS_PANE);
        for (int slot : filterSlots) {
            inventory.setItem(slot, filter);
        }
    }

    private static void addToggleAutoRekitItem(GuiBuilder inventory, Player player) {
        String configPath = "auto-rekit.toggle-auto-rekit";
        Material defaultMaterial = Material.NETHERITE_SWORD;

        int slot = CONFIG.getConfig().getInt(configPath + ".slot");
        ItemStack toggleAutoRekit = createItem(configPath, defaultMaterial);

        setToggleAutoRekitLore(toggleAutoRekit, player);

        inventory.setItem(slot, toggleAutoRekit, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(KitsX.getInstance(), () -> {
                KitsX.getAutoRekitUtil().toggle(player);
                setToggleAutoRekitLore(toggleAutoRekit, player);
                player.getOpenInventory().setItem(slot, toggleAutoRekit);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });
    }

    private static void setToggleAutoRekitLore(ItemStack toggleAutoRekit, Player player) {
        String configPath = "auto-rekit.toggle-auto-rekit";

        List<String> lore = new ArrayList<>();
        for (String line : CONFIG.getConfig().getStringList(configPath + ".lore")) {
            lore.add(ColorizeText.hex(line.replace("%status%", KitsX.getAutoRekitUtil().status(player))));
        }
        ItemMeta meta = toggleAutoRekit.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(ColorizeText.hex(Objects.requireNonNull(CONFIG.getConfig().getString(configPath + ".name"))));
        toggleAutoRekit.setItemMeta(meta);
    }

    private static void addKitItems(GuiBuilder inventory, Player player) {
        addItemGroup(inventory, "auto-rekit.kits", Material.END_CRYSTAL, player);
    }

    private static void addItemGroup(GuiBuilder inventory, String configPath, Material defaultMaterial, Player player) {
        List<Integer> slots = CONFIG.getConfig().getIntegerList(configPath + ".slots");

        for (int i = 0; i < slots.size(); i++) {
            int slot = slots.get(i);
            int kitNumber = i + 1;

            ItemStack item = createItem(configPath, defaultMaterial, kitNumber);
            inventory.setItem(slot, item, p -> {
                String kitSelected = PLUGIN.getConfig().getString("messages.auto-rekit-kit-selected");
                assert kitSelected != null;
                player.sendMessage(ColorizeText.hex(kitSelected).replace("%kit%", String.valueOf(kitNumber)));
                KitsX.getAutoRekitUtil().setKit(player, "Kit " + kitNumber);
            });
        }
    }

    private static ItemStack createItem(String configPath, Material defaultMaterial) {
        return createItem(configPath, defaultMaterial, 0);
    }

    private static ItemStack createItem(String configPath, Material defaultMaterial, int kitNumber) {
        String itemMaterial = CONFIG.getConfig().getString(configPath + ".material", defaultMaterial.name());
        String itemName = CONFIG.getConfig().getString(configPath + ".name", "").replace("%kit%", String.valueOf(kitNumber));
        List<String> loreList = CONFIG.getConfig().getStringList(configPath + ".lore");
        List<String> finalLore = new ArrayList<>();
        for (String lore : loreList) {
            finalLore.add(ColorizeText.hex(lore.replace("%i%", String.valueOf(kitNumber))));
        }
        List<String> flagList = CONFIG.getConfig().getStringList(configPath + ".flags");
        List<ItemFlag> flags = new ArrayList<>();
        for (String flag : flagList) {
            flags.add(ItemFlag.valueOf(flag));
        }

        ItemStack item = new ItemBuilderGUI(Material.valueOf(itemMaterial))
                .name(ColorizeText.hex(itemName))
                .lore(finalLore.toArray(new String[0]))
                .flags(flags.toArray(new ItemFlag[0]))
                .build();

        List<Map<?, ?>> enchantmentList = CONFIG.getConfig().getMapList(configPath + ".enchantments");
        for (Map<?, ?> enchantmentMap : enchantmentList) {
            String type = (String) enchantmentMap.get("type");
            int level = (Integer) enchantmentMap.get("level");
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(type.toLowerCase()));
            if (enchantment != null) {
                item.addUnsafeEnchantment(enchantment, level);
            } else {
                LOGGER.warning("Invalid enchantment type " + type);
            }
        }

        return item;
    }
}