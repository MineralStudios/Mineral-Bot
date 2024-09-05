package gg.mineral.bot.impl.controls;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class Key implements gg.mineral.bot.api.controls.Key {
    private final Type type;
    @Setter
    private boolean pressed = false;
}
