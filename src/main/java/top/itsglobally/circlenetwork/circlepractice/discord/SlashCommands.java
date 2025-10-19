package top.itsglobally.circlenetwork.circlepractice.discord;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import top.itsglobally.circlenetwork.circlepractice.listeners.IListener;
import top.nontage.nontagelib.annotations.AutoListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoListener
public class SlashCommands implements SlashCommandProvider, IListener {

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return new HashSet<>(List.of(
                new PluginSlashCommand(plugin, new CommandData("ping", "A classic match of ping pong"))
        ));
    }

    @SlashCommand(path = "ping")
    public void ping(SlashCommandEvent event) {
        event.reply("Pong!").queue();
    }
}