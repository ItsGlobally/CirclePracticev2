package top.itsglobally.circlenetwork.circlepractice.managers;

import com.grinderwolf.swm.api.SlimePlugin;
import org.bukkit.Bukkit;
import top.itsglobally.circlenetwork.circlepractice.CirclePractice;
import top.itsglobally.circlenetwork.circlepractice.data.GlobalInterface;

public class PluginManager implements GlobalInterface {
    public CirclePractice getPlugin = plugin;
    public SlimePlugin getSlimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

    public PluginManager() {

    }
}
