package data;

import java.util.Arrays;
import java.util.List;

public class Modes {
    public static final String MOUSE = "Klik myszą";
    public static final String MOUSE_FOLLOW = "Śledzenie myszą";
    public static final String KEYBOARD = "Klik klawiaturą";
    public static final String SOUND = "Dźwięk";
    public static final String SHUTDOWN = "Wyłączenie komputera";

    public static final String TIME = "Czas";
    public static final String PIXEL_COLOR = "Kolor piksela";
    public static final String PIXEL_COLOR_DIFF = "Kolor piksela różny od";
    public static final String IMAGE_PRESENCE = "Obecność obrazu w polu";
    public static final String IMAGE_ABSENCE = "Brak obecności obrazu w polu";

    public static final List<String> actions = Arrays.asList(MOUSE, KEYBOARD, SOUND, SHUTDOWN);
    public static final List<String> triggers = Arrays.asList(TIME, PIXEL_COLOR, PIXEL_COLOR_DIFF, IMAGE_PRESENCE, IMAGE_ABSENCE);

}
