package gg.mineral.bot.api.event.peripherals;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class KeyboardKeyEvent implements Event {
    Key.Type type;
    boolean pressed;
}