package top.xep0268.calabashes.items;

import top.xep0268.calabashes.Game;
import top.xep0268.calabashes.field.Field;
import top.xep0268.calabashes.field.Position;

import java.io.Serializable;

public abstract class Living extends Item implements Serializable {
    public Living(Position pos, Field field_, Game game_) {
        super(pos, field_, game_);
    }
}
