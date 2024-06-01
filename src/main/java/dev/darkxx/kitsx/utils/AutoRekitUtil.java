/*
 * This file is part of KitsX
 *
 * KitsX
 * Copyright (c) 2024 XyrisPlugins
 *
 * KitsX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KitsX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package dev.darkxx.kitsx.utils;

import dev.darkxx.kitsx.KitsX;
import dev.darkxx.kitsx.api.AutoRekitAPI;
import dev.darkxx.kitsx.utils.config.ConfigManager;
import dev.darkxx.kitsx.utils.config.MenuConfig;
import dev.darkxx.utils.text.color.ColorizeText;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoRekitUtil implements AutoRekitAPI {

    private static ConfigManager configManager;
    private final MenuConfig CONFIG = new MenuConfig(KitsX.getInstance(), "menus/autorekit-menu.yml");

    public AutoRekitUtil(ConfigManager configManager) {
        AutoRekitUtil.configManager = configManager;
    }

    public static void of(JavaPlugin plugin) {
        configManager = ConfigManager.get(plugin);
        configManager.create("data/autorekit.yml");
    }

    @Override
    public void set(Player player, Boolean enabled, String kitName) {
        String playerName = player.getUniqueId().toString();
        configManager.set("data/autorekit.yml", playerName + ".auto-rekit.enabled", enabled);
        configManager.set("data/autorekit.yml", playerName + ".auto-rekit.kit", kitName);
        save();
    }

    @Override
    public void setKit(Player player, String kitName) {
        String playerName = player.getUniqueId().toString();
        configManager.set("data/autorekit.yml", playerName + ".auto-rekit.kit", kitName);
        save();
    }

    @Override
    public void toggle(Player player) {
        String playerName = player.getUniqueId().toString();
        boolean currentSetting = configManager.getConfig("data/autorekit.yml").getBoolean(playerName + ".auto-rekit.enabled");
        configManager.set("data/autorekit.yml", playerName + ".auto-rekit.enabled", !currentSetting);
        save();
    }

    @Override
    public boolean get(Player player) {
        String playerName = player.getUniqueId().toString();
        return configManager.getConfig("data/autorekit.yml").getBoolean(playerName + ".auto-rekit.enabled", false);
    }

    @Override
    public String getKit(Player player) {
        String playerName = player.getUniqueId().toString();
        return configManager.getConfig("data/autorekit.yml").getString(playerName + ".auto-rekit.kit", "Kit 1");
    }

    @Override
    public boolean isEnabled(Player player) {
        String playerName = player.getUniqueId().toString();
        return configManager.getConfig("data/autorekit.yml").getBoolean(playerName + ".auto-rekit.enabled", false);
    }

    @Override
    public boolean hasAutoRekit(Player player) {
        String playerName = player.getUniqueId().toString();
        return configManager.contains("data/autorekit.yml", playerName + ".auto-rekit");
    }

    @Override
    public String status(Player player) {
        boolean autoRekitEnabled = get(player);
        String enabled = CONFIG.getConfig().getString("auto-rekit.placeholders.enabled", "&#7cff6eEnabled");
        String disabled = CONFIG.getConfig().getString("auto-rekit.placeholders.disabled", "&#ffa6a6Disabled");
        return autoRekitEnabled ? ColorizeText.hex(enabled) : ColorizeText.hex(disabled);
    }

    private void save() {
        configManager.saveConfig("data/autorekit.yml");
    }
}