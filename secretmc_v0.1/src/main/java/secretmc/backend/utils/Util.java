package secretmc.backend.utils;

/* *
 *
 *  About: Useful pure and impure methods
 *
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * */

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;

@SuppressWarnings("SpellCheckingInspection")
public class Util {

    // sends a message to all online ops and console
    public static void notifyOps(TextComponent msg) {
        if (msg == null) return;

        for (Player thisPlayer: Bukkit.getOnlinePlayers()) {
            try {
                if (thisPlayer.isOp()) thisPlayer.sendMessage(msg);
            } catch (Exception e) {return;}
        }

        System.out.println(msg.getText());
    }

    // converts sum seconds into human-readable string
    public static String timeToString(double seconds) {

        long hours;
        long days = (long) (seconds / 86400);
        long daysRem = (long) (seconds % 86400);

        if (days < 1) hours = (long) (seconds / 3600);
        else hours = daysRem / 3600;

        long hoursRem = (long) (seconds % 3600);
        long minutes = hoursRem / 60;

        String daysString;
        String hoursString;
        String minutesString;

        if (hours == 1) {
            hoursString = hours + " hour";
        } else {
            hoursString = hours + " hours";
        }

        if (days == 1) {
            daysString = days + " day";
        } else {
            daysString = days + " days";
        }

        if (minutes == 1) {
            minutesString = minutes + " minute";
        } else if (minutes == 0) {
            minutesString = "";
        } else {
            minutesString = minutes + " minutes";
        }

        if (minutesString.isEmpty() && hoursString.equals("0 hours")) return "< 1 minute";

        if (days < 1 && minutes == 0) {
            return hoursString;

        } else if (days < 1) {
            return hoursString + ", " + minutesString;

        } else if (minutes == 0) {
            return daysString + ", " + hoursString;
        } else {
            return daysString + ", " + hoursString + ", " + minutesString;
        }
    }

    public static boolean validServerIP(String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");

        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String getDimensionName (Location thisLoc) {

        String out = null;
        org.bukkit.World.Environment thisEnv = thisLoc.getWorld().getEnvironment();

        if (thisEnv.equals(org.bukkit.World.Environment.NORMAL)) out = "overworld";
        else if (thisEnv.equals(org.bukkit.World.Environment.NETHER)) out = "the_nether";
        else if (thisEnv.equals(org.bukkit.World.Environment.THE_END)) out = "the_end";

        return out;
    }

    public static boolean isCmdRestricted (String thisCmd) {

        return thisCmd.contains("/op") || thisCmd.contains("/deop") ||
                thisCmd.contains("/ban") || thisCmd.contains("/attribute") ||
                thisCmd.contains("/default") || thisCmd.contains("/execute") ||
                thisCmd.contains("/rl") || thisCmd.contains("/summon") ||
                thisCmd.contains("/gamerule") || thisCmd.contains("/set") ||
                thisCmd.contains("/difficulty") || thisCmd.contains("/replace") ||
                thisCmd.contains("/enchant") || thisCmd.contains("/time") ||
                thisCmd.contains("/weather") || thisCmd.contains("/schedule") ||
                thisCmd.contains("/data") || thisCmd.contains("/fill") ||
                thisCmd.contains("/save") || thisCmd.contains("/loot") ||
                thisCmd.contains("/experience") || thisCmd.contains("/xp") ||
                thisCmd.contains("/forceload") || thisCmd.contains("/function") ||
                thisCmd.contains("/spreadplayers") || thisCmd.contains("/reload") ||
                thisCmd.contains("/world") || thisCmd.contains("/restart") ||
                thisCmd.contains("/spigot") || thisCmd.contains("/plugins") ||
                thisCmd.contains("/protocol") || thisCmd.contains("/packet") ||
                thisCmd.contains("/whitelist") || thisCmd.contains("/minecraft") ||
                thisCmd.contains("/dupe") || thisCmd.contains("/score") ||
                thisCmd.contains("/tell") || thisCmd.contains("/global");
    }

    public static World getWorldByDimension(World.Environment thisEnv) {

        for (org.bukkit.World thisWorld: Bukkit.getServer().getWorlds()) {
            if (thisWorld.getEnvironment().equals(thisEnv)) return thisWorld;
        }
        return null;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
