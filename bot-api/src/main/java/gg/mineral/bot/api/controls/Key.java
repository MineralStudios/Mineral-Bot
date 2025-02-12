package gg.mineral.bot.api.controls;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface Key {

    /**
     * Checks if the key is currently held.
     *
     * @return True if the key is held, false otherwise.
     */
    boolean isPressed();

    /**
     * Gets the type of the key.
     *
     * @return The type of the key as a KeyType enum.
     */
    Type getType();

    @RequiredArgsConstructor
    @Getter
    enum Type {
        UNKNOWN(-1, '\0'), KEY_NONE(0, '\0'), KEY_ESCAPE(1, '\0'), KEY_1(2, '1'), KEY_2(3, '2'), KEY_3(4, '3'), KEY_4(5,
                '4'), KEY_5(6, '5'), KEY_6(7, '6'), KEY_7(8, '7'), KEY_8(9, '8'), KEY_9(10,
                '9'), KEY_0(11, '0'), KEY_MINUS(12, '-'), KEY_EQUALS(13, '='), KEY_BACK(14, '\0'), KEY_TAB(15,
                '\t'), KEY_Q(16, 'Q'), KEY_W(17, 'W'), KEY_E(18, 'E'), KEY_R(19, 'R'), KEY_T(20,
                'T'), KEY_Y(21, 'Y'), KEY_U(22, 'U'), KEY_I(23, 'I'), KEY_O(24, 'O'), KEY_P(25,
                'P'), KEY_LBRACKET(26, '['), KEY_RBRACKET(27, ']'), KEY_RETURN(28,
                '\n'), KEY_LCONTROL(29, '\0'), KEY_A(30, 'A'), KEY_S(31,
                'S'), KEY_D(32, 'D'), KEY_F(33, 'F'), KEY_G(34,
                'G'), KEY_H(35, 'H'), KEY_J(36, 'J'), KEY_K(37,
                'K'), KEY_L(38, 'L'), KEY_SEMICOLON(39,
                ';'), KEY_APOSTROPHE(40,
                '\''), KEY_GRAVE(41,
                '`'), KEY_LSHIFT(
                42,
                '\0'), KEY_BACKSLASH(
                43,
                '\\'), KEY_Z(
                44,
                'Z'), KEY_X(
                45,
                'X'), KEY_C(
                46,
                'C'), KEY_V(
                47,
                'V'), KEY_B(
                48,
                'B'), KEY_N(
                49,
                'N'), KEY_M(
                50,
                'M'), KEY_COMMA(
                51,
                ','), KEY_PERIOD(
                52,
                '.'), KEY_SLASH(
                53,
                '/'), KEY_RSHIFT(
                54,
                '\0'), KEY_MULTIPLY(
                55,
                '*'), KEY_LMENU(
                56,
                '\0'), KEY_SPACE(
                57,
                ' '), KEY_CAPITAL(
                58,
                '\0'), KEY_F1(
                59,
                '\0'), KEY_F2(
                60,
                '\0'), KEY_F3(
                61,
                '\0'), KEY_F4(
                62,
                '\0'), KEY_F5(
                63,
                '\0'), KEY_F6(
                64,
                '\0'), KEY_F7(
                65,
                '\0'), KEY_F8(
                66,
                '\0'), KEY_F9(
                67,
                '\0'), KEY_F10(
                68,
                '\0'), KEY_NUMLOCK(
                69,
                '\0'), KEY_SCROLL(
                70,
                '\0'), KEY_NUMPAD7(
                71,
                '7'), KEY_NUMPAD8(
                72,
                '8'), KEY_NUMPAD9(
                73,
                '9'), KEY_SUBTRACT(
                74,
                '-'), KEY_NUMPAD4(
                75,
                '4'), KEY_NUMPAD5(
                76,
                '5'), KEY_NUMPAD6(
                77,
                '6'), KEY_ADD(
                78,
                '+'), KEY_NUMPAD1(
                79,
                '1'), KEY_NUMPAD2(
                80,
                '2'), KEY_NUMPAD3(
                81,
                '3'), KEY_NUMPAD0(
                82,
                '0'), KEY_DECIMAL(
                83,
                '.'), KEY_F11(
                87,
                '\0'), KEY_F12(
                88,
                '\0'), KEY_F13(
                100,
                '\0'), KEY_F14(
                101,
                '\0'), KEY_F15(
                102,
                '\0'), KEY_F16(
                103,
                '\0'), KEY_F17(
                104,
                '\0'), KEY_F18(
                105,
                '\0'), KEY_KANA(
                112,
                '\0'), KEY_F19(
                113,
                '\0'), KEY_CONVERT(
                121,
                '\0'), KEY_NOCONVERT(
                123,
                '\0'), KEY_YEN(
                125,
                '¥'), KEY_NUMPADEQUALS(
                141,
                '='), KEY_CIRCUMFLEX(
                144,
                '^'), KEY_AT(
                145,
                '@'), KEY_COLON(
                146,
                ':'), KEY_UNDERLINE(
                147,
                '_'), KEY_KANJI(
                148,
                '\0'), KEY_STOP(
                149,
                '\0'), KEY_AX(
                150,
                '\0'), KEY_UNLABELED(
                151,
                '\0'), KEY_NUMPADENTER(
                156,
                '\n'), KEY_RCONTROL(
                157,
                '\0'), KEY_SECTION(
                167,
                '\0'), KEY_NUMPADCOMMA(
                179,
                ','), KEY_DIVIDE(
                181,
                '/'), KEY_SYSRQ(
                183,
                '\0'), KEY_RMENU(
                184,
                '\0'), KEY_FUNCTION(
                196,
                '\0'), KEY_PAUSE(
                197,
                '\0'), KEY_HOME(
                199,
                '\0'), KEY_UP(
                200,
                '\0'), KEY_PRIOR(
                201,
                '\0'), KEY_LEFT(
                203,
                '\0'), KEY_RIGHT(
                205,
                '\0'), KEY_END(
                207,
                '\0'), KEY_DOWN(
                208,
                '\0'), KEY_NEXT(
                209,
                '\0'), KEY_INSERT(
                210,
                '\0'), KEY_DELETE(
                211,
                '\0'), KEY_CLEAR(
                218,
                '\0'), KEY_LMETA(
                219,
                '\0'), KEY_RMETA(
                220,
                '\0'), KEY_APPS(
                221,
                '\0'), KEY_POWER(
                222,
                '\0'), KEY_SLEEP(
                223,
                '\0');

        final int keyCode;
        final char character;

        public static Type fromKeyCode(int keyCode2) {
            for (Type type : values())
                if (type.keyCode == keyCode2)
                    return type;

            return UNKNOWN;
        }
    }
}
