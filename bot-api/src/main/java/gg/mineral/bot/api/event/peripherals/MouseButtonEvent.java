package gg.mineral.bot.api.event.peripherals;

import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class MouseButtonEvent implements Event {
    MouseButton.Type type;
    boolean pressed;
}