package games.negative.mines.api.model.schematic;

public interface PasteSpecifications {

    int y();

    MineLocationRelative regionRelativeMinimum();

    MineLocationRelative regionRelativeMaximum();

    MinePositionRelative spawnRelative();

    record MineLocationRelative(int x, int y, int z) {
    }

    record MinePositionRelative(double x, double y, double z, float yaw, float pitch) {
    }
}
