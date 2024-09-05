package gg.mineral.bot.impl.controls;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class MouseButton implements gg.mineral.bot.api.controls.MouseButton {
    private final Type type;
    @Setter
    private boolean pressed = false;
}
