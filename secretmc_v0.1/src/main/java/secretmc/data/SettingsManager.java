package secretmc.data;

/* *
 *
 *  About: Stores and mutates `PlayerSettings` objects in memory
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

import secretmc.data.objects.SettingsContainer;
import org.bukkit.OfflinePlayer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

public class SettingsManager {

    public static SettingsContainer getNewSettings(OfflinePlayer p) {
        return new SettingsContainer(p.getUniqueId(), true, true, true, true, true, true, "UTC"); }

    public static SettingsContainer getSettings(OfflinePlayer p) {
        SettingsContainer settings = PlayerMeta.sPlayerSettings.get(p.getUniqueId());

        if (settings != null && PlayerMeta.sPlayerSettings.containsKey(p.getUniqueId())) {

            return settings;

        } else return getNewSettings(p);
    }

    public static void writePlayerSettings() throws IOException {

        BufferedWriter w = new BufferedWriter(new FileWriter("plugins/core/player_settings.txt"));

        for (SettingsContainer object: PlayerMeta.sPlayerSettings.values()) {
            try {
                w.write(object.toString() + "\n");
                w.flush();

              } catch (IOException e) {
                  throw new UncheckedIOException(e);
              }
        }
        w.close();
    }
}
