package top.itsglobally.circlenetwork.circlepractice.managers;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import top.itsglobally.circlenetwork.circlepractice.data.Arena;
import top.itsglobally.circlenetwork.circlepractice.data.GameArena;
import top.itsglobally.circlenetwork.circlepractice.utils.RandomUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ArenaManager extends Managers {

    private final SlimePlugin slime = plugin.getPlm().getSlimePlugin;

    public ArenaManager() {

    }
    public void cloneArena(Arena a, String newWorldName) {
        this.cloneArena(a.getWorldName(), newWorldName);
    }
    public void cloneArena(String sourceWorldName, String newWorldName) {

        try {
            SlimeLoader loader = slime.getLoader("file");

            SlimeWorld world = slime.loadWorld(loader, sourceWorldName, false, gameArenaProps());

            world = world.clone(newWorldName);

            slime.generateWorld(world);
        } catch (UnknownWorldException | IOException | CorruptedWorldException |
                 NewerFormatException | WorldInUseException ex) {
            ex.printStackTrace();
        }
    }

    public GameArena createGameArena(Arena a) {
        String newWorldName = "arena_" + UUID.randomUUID().toString().substring(0, 8);
        Collection<Arena> arenas = plugin.getCm().getArenas();
        List<Arena> list = new ArrayList<>(arenas);
        Arena na = list.get(RandomUtil.getRandomInt(0, list.toArray().length -1));
        cloneArena(na, newWorldName);
        GameArena ga = new GameArena(na.getName(), newWorldName);
        ga.convertFromArena(na);
        return ga;
    }

    private SlimePropertyMap gameArenaProps() {
        SlimePropertyMap props = new SlimePropertyMap();
        props.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        props.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        props.setString(SlimeProperties.DIFFICULTY, "hard");
        return props;
    }
}
