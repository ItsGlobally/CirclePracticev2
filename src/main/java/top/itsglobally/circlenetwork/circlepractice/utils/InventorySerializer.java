package top.itsglobally.circlenetwork.circlepractice.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class InventorySerializer {

    /**
     * Serialize inventory contents and armor.
     */
    public static String serializeInventory(ItemStack[] contents, ItemStack[] armor) {
        return serialize(contents, armor);
    }

    /**
     * Deserialize inventory contents and armor.
     */
    public static ItemStack[][] deserializeInventory(String data) {
        return deserialize(data, true);
    }

    /**
     * Serialize FFA inventory contents only.
     */
    public static String serializeInventoryFFA(ItemStack[] contents) {
        return serialize(contents);
    }

    /**
     * Deserialize FFA inventory contents only.
     */
    public static ItemStack[][] deserializeInventoryFFA(String data) {
        return deserialize(data, false);
    }

    // ----------------- Internal helpers -----------------

    private static String serialize(ItemStack[] contents, ItemStack... armor) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            // Write contents
            dataOutput.writeInt(contents.length);
            for (ItemStack item : contents) {
                dataOutput.writeObject(item);
            }

            // Write armor if provided
            if (armor != null && armor.length > 0) {
                dataOutput.writeInt(armor.length);
                for (ItemStack item : armor) {
                    dataOutput.writeObject(item);
                }
            }

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ItemStack[][] deserialize(String data, boolean withArmor) {
        if (data == null || data.isEmpty()) return null;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            // Read contents
            int contentsLength = dataInput.readInt();
            ItemStack[] contents = new ItemStack[contentsLength];
            for (int i = 0; i < contentsLength; i++) {
                contents[i] = (ItemStack) dataInput.readObject();
            }

            if (withArmor) {
                int armorLength = dataInput.readInt();
                ItemStack[] armor = new ItemStack[armorLength];
                for (int i = 0; i < armorLength; i++) {
                    armor[i] = (ItemStack) dataInput.readObject();
                }
                return new ItemStack[][]{contents, armor};
            } else {
                return new ItemStack[][]{contents};
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}