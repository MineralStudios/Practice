package gg.mineral.practice.scoreboard;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import gg.mineral.api.collection.GlueList;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class Scoreboard {

    // Scoreboard enums
    private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SB_ACTION;
    private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;

    static {
        ENUM_SB_HEALTH_DISPLAY = IScoreboardCriteria.EnumScoreboardHealthDisplay.class;

        ENUM_SB_ACTION = PacketPlayOutScoreboardScore.EnumScoreboardAction.class;

        ENUM_SB_HEALTH_DISPLAY_INTEGER = enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER");
        ENUM_SB_ACTION_CHANGE = enumValueOf(ENUM_SB_ACTION, "CHANGE");
        ENUM_SB_ACTION_REMOVE = enumValueOf(ENUM_SB_ACTION, "REMOVE");
    }

    private final Player player;
    private final String id;

    private String title = ChatColor.RESET.toString();
    private List<String> lines = new GlueList<>();

    private boolean deleted = false;

    /**
     * Creates a new FastBoard.
     *
     * @param player the player the scoreboard is for
     */
    public Scoreboard(Player player) {
        this.player = Objects.requireNonNull(player, "player");

        id = "fb-" + Double.toString(Math.random()).substring(2, 10);

        try {
            sendObjectivePacket(ObjectiveMode.CREATE);
            sendDisplayObjectivePacket();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the scoreboard title.
     *
     * @return the scoreboard title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Update the scoreboard title.
     *
     * @param title the new scoreboard title
     * @throws IllegalArgumentException if the title is longer than 32 chars on 1.12
     *                                  or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public void updateTitle(String title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) {
            return;
        }

        if (title.length() > 32) {
            throw new IllegalArgumentException("Title is longer than 32 chars");
        }

        this.title = title;

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the scoreboard lines.
     *
     * @return the scoreboard lines
     */
    public List<String> getLines() {
        return new GlueList<>(lines);
    }

    /**
     * Get the specified scoreboard line.
     *
     * @param line the line number
     * @return the line
     * @throws IndexOutOfBoundsException if the line is higher than {@code size}
     */
    public String getLine(int line) {
        checkLineNumber(line, true);

        return lines.get(line);
    }

    /**
     * Update a single scoreboard line.
     *
     * @param line the line number
     * @param text the new line text
     * @throws IndexOutOfBoundsException if the line is higher than {@code size} + 1
     */
    public void updateLine(int line, String text) {
        checkLineNumber(line, false);

        try {
            if (line < size()) {
                lines.set(line, text);

                sendTeamPacket(getScoreByLine(line), TeamMode.UPDATE);
                return;
            }

            List<String> newLines = new GlueList<>(lines);

            if (line > size()) {
                for (int i = size(); i < line; i++) {
                    newLines.add("");
                }
            }

            newLines.add(text);

            updateLines(newLines);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a scoreboard line.
     *
     * @param line the line number
     */
    public void removeLine(int line) {
        checkLineNumber(line, false);

        if (line >= size()) {
            return; // The line don't exists
        }

        List<String> lines = new GlueList<>(this.lines);
        lines.remove(line);
        updateLines(lines);
    }

    /**
     * Update all the scoreboard lines.
     *
     * @param lines the new lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12
     *                                  or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public void updateLines(String... lines) {
        updateLines(Arrays.asList(lines));
    }

    /**
     * Update the lines of the scoreboard
     *
     * @param lines the new scoreboard lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12
     *                                  or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public void updateLines(Collection<String> lines) {
        Objects.requireNonNull(lines, "lines");

        int lineCount = 0;
        for (String s : lines) {
            if (s != null && s.length() > 30) {
                throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");
            }
            lineCount++;
        }

        List<String> oldLines = new GlueList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        int linesSize = this.lines.size();

        try {
            if (oldLines.size() != linesSize) {
                List<String> oldLinesCopy = new GlueList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (int i = oldLinesCopy.size(); i > linesSize; i--) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE);

                        sendScorePacket(i - 1, ScoreboardAction.REMOVE);

                        oldLines.remove(0);
                    }
                } else {
                    for (int i = oldLinesCopy.size(); i < linesSize; i++) {
                        sendScorePacket(i, ScoreboardAction.CHANGE);

                        sendTeamPacket(i, TeamMode.CREATE);

                        oldLines.add(oldLines.size() - i, getLineByScore(i));
                    }
                }
            }

            for (int i = 0; i < linesSize; i++) {
                if (!Objects.equals(getLineByScore(oldLines, i), getLineByScore(i))) {
                    sendTeamPacket(i, TeamMode.UPDATE);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the player who has the scoreboard.
     *
     * @return current player for this FastBoard
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the scoreboard id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get if the scoreboard is deleted.
     *
     * @return true if the scoreboard is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Get the scoreboard size (the number of lines).
     *
     * @return the size
     */
    public int size() {
        return lines.size();
    }

    /**
     * Delete this FastBoard, and will remove the scoreboard for the associated
     * player if he is online. After this, all uses of {@link #updateLines} and
     * {@link #updateTitle} will throws an {@link IllegalStateException}
     *
     * @throws IllegalStateException if this was already call before
     */
    public void delete() {
        try {
            for (int i = 0; i < lines.size(); i++) {
                sendTeamPacket(i, TeamMode.REMOVE);
            }

            sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        deleted = true;
    }

    private void checkLineNumber(int line, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive");
        }

        if (checkMax && line >= lines.size()) {
            throw new IllegalArgumentException("Line number must be under " + lines.size());
        }
    }

    private int getScoreByLine(int line) {
        return lines.size() - line - 1;
    }

    private String getLineByScore(int score) {
        return getLineByScore(lines, score);
    }

    private String getLineByScore(List<String> lines, int score) {
        return lines.get(lines.size() - score - 1);
    }

    private void sendObjectivePacket(ObjectiveMode mode) throws ReflectiveOperationException {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();

        setField(packet, String.class, id);
        setField(packet, int.class, mode.ordinal());

        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, title, 1);

            setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);

        }

        sendPacket(packet);
    }

    private void sendDisplayObjectivePacket() throws ReflectiveOperationException {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();

        setField(packet, int.class, 1);
        setField(packet, String.class, id);

        sendPacket(packet);
    }

    private void sendScorePacket(int score, ScoreboardAction action) throws ReflectiveOperationException {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();

        setField(packet, String.class, getColorCode(score), 0);

        setField(packet, ENUM_SB_ACTION,
                action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE);

        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String.class, id, 1);
            setField(packet, int.class, score);
        }

        sendPacket(packet);
    }

    private void sendTeamPacket(int score, TeamMode mode) throws ReflectiveOperationException {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }

        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        setField(packet, String.class, id + ':' + score); // Team name
        setField(packet, int.class, mode.ordinal(), 1); // Update mode

        if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
            String line = getLineByScore(score);
            String prefix;
            String suffix = null;

            if (line == null || line.isEmpty()) {
                prefix = getColorCode(score) + ChatColor.RESET;
            } else if (line.length() <= 16) {
                prefix = line;
            } else {
                // Prevent splitting color codes
                int index = line.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
                prefix = line.substring(0, index);
                String suffixTmp = line.substring(index);
                ChatColor chatColor = null;

                if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) {
                    chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
                }

                String color = ChatColor.getLastColors(prefix);
                boolean addColor = chatColor == null || chatColor.isFormat();

                suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
            }

            if (prefix.length() > 16 || (suffix != null && suffix.length() > 16)) {
                // Something went wrong, just cut to prevent client crash/kick
                prefix = prefix.substring(0, 16);
                suffix = (suffix != null) ? suffix.substring(0, 16) : null;
            }

            setComponentField(packet, prefix, 2); // Prefix
            setComponentField(packet, suffix == null ? "" : suffix, 3); // Suffix
            setField(packet, String.class, "always", 4); // Visibility for 1.8+
            setField(packet, String.class, "always", 5); // Collisions for 1.9+

            if (mode == TeamMode.CREATE) {
                setField(packet, Collection.class, Collections.singletonList(getColorCode(score))); // Players in the
                                                                                                    // team
            }
        }

        sendPacket(packet);
    }

    private String getColorCode(int score) {
        return ChatColor.values()[score].toString();
    }

    private void sendPacket(Packet<?> packet) throws ReflectiveOperationException {
        if (deleted) {
            throw new IllegalStateException("This FastBoard is deleted");
        }

        if (player.isOnline()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        setField(object, fieldType, value, 0);
    }

    private void setField(Object object, Class<?> fieldType, Object value, int count)
            throws ReflectiveOperationException {
        int i = 0;

        for (Field f : object.getClass().getDeclaredFields()) {
            if (f.getType() == fieldType && i++ == count) {
                f.setAccessible(true);
                f.set(object, value);
            }
        }
    }

    private void setComponentField(Object object, String value, int count) throws ReflectiveOperationException {
        setField(object, String.class, value, count);
        return;
    }

    enum ObjectiveMode {

        CREATE, REMOVE, UPDATE

    }

    enum TeamMode {

        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS

    }

    enum ScoreboardAction {

        CHANGE, REMOVE

    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E enumValueOf(Class<?> enumClass, String enumName) {
        return Enum.valueOf((Class<E>) enumClass, enumName.toUpperCase(Locale.ROOT));
    }
}
