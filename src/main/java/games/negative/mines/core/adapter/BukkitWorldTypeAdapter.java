package games.negative.mines.core.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class BukkitWorldTypeAdapter extends TypeAdapter<World> {

    @Override
    public void write(JsonWriter out, World value) throws IOException {
//        out.name("world");
        out.value(value.getName());
    }

    @Override
    public World read(JsonReader in) throws IOException {
//        String next = in.nextName();
//        if (next == null) return null;
//
//        if (!next.equalsIgnoreCase("world")) return null;

        return Bukkit.getWorld(in.nextString());
    }
}
