package games.negative.mines.core.structure;

import games.negative.mines.api.model.schematic.PasteSpecifications;

public record UPMPasteSpecifications(int y, MineLocationRelative regionRelativeMinimum,
                                     MineLocationRelative regionRelativeMaximum,
                                     MinePositionRelative spawnRelative) implements PasteSpecifications {
}
