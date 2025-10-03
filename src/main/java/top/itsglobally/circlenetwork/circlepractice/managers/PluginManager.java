package top.itsglobally.circlenetwork.circlepractice.managers;

import com.grinderwolf.swm.api.SlimePlugin;
import org.bukkit.Bukkit;
import top.itsglobally.circlenetwork.circlepractice.CirclePractice;

public class PluginManager extends Managers{
    public PluginManager() {

    }
    public CirclePractice getPlugin = plugin;
    public SlimePlugin getSlimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
}
