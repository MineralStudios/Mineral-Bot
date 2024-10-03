package gg.mineral.bot.api.event.mouse;

import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MouseButtonEvent implements Event {
    private final MouseButton.Type type;
    private final boolean pressed;
    private final int duration;
}
