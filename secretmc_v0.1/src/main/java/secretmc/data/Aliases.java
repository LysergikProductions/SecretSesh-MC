package secretmc.data;

/* *
 *
 *  About: Aliases for exceptionally long and / or complex minecraft commands
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

@SuppressWarnings("SpellCheckingInspection")
public class Aliases {

    public static String armor_a; public static String armor_b; static {

        // chestplate and helmet
        armor_a = "/summon armor_stand ~1 ~2 ~1 {CustomName:\"\\\"Sinse's_32kStackedArmor_a\\\"\",CustomNameVisible:1," +
                "ShowArms:1,HandItems:[{id:netherite_chestplate,tag:{Enchantments:[{id:protection,lvl:32767}," +
                "{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}," +
                "{id:netherite_helmet,tag:{Enchantments:[{id:respiration,lvl:3},{id:aqua_affinity,lvl:1},{id:protection,lvl:32767}," +
                "{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}";

        // boots and leggings
        armor_b = "/summon armor_stand ~-1 ~2 ~-1 {CustomName:\"\\\"Sinse's_32kStackedArmor_b\\\"\",CustomNameVisible:1," +
                "ShowArms:1,HandItems:[{id:netherite_boots,tag:{Enchantments:[{id:blast_protection,lvl:32767}," +
                "{id:thorns,lvl:32767},{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}," +
                "{id:netherite_leggings,tag:{Enchantments:[{id:blast_protection,lvl:32767},{id:thorns,lvl:32767}," +
                "{id:unbreaking,lvl:32767},{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]},Count:127}]}";
    }

    // totems on armor stands
    public static String totems_armor1; public static String totems_armor2; static {

        totems_armor1 = "/summon armor_stand ~-1 ~2 ~1 {CustomName:\"StackedTotems\",CustomNameVisible:1," +
                "ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}";

        totems_armor2 = "/summon armor_stand ~1 ~2 ~-1 {CustomName:\"StackedTotems\",CustomNameVisible:1," +
                "ShowArms:1,HandItems:[{id:totem_of_undying,Count:64},{id:totem_of_undying,Count:64}]}";
    }

    // totems in shulker box
    public static String totems_shulker; static {

        totems_shulker = "/give @s yellow_shulker_box{BlockEntityTag:{Items:[{Slot:0,id:totem_of_undying,Count:127}," +
                "{Slot:1,id:totem_of_undying,Count:127},{Slot:2,id:totem_of_undying,Count:127}," +
                "{Slot:3,id:totem_of_undying,Count:127},{Slot:4,id:totem_of_undying,Count:127}," +
                "{Slot:5,id:totem_of_undying,Count:127},{Slot:6,id:totem_of_undying,Count:127}," +
                "{Slot:7,id:totem_of_undying,Count:127},{Slot:8,id:totem_of_undying,Count:127}," +
                "{Slot:9,id:totem_of_undying,Count:127},{Slot:10,id:totem_of_undying,Count:127}," +
                "{Slot:11,id:totem_of_undying,Count:127},{Slot:12,id:totem_of_undying,Count:127}," +
                "{Slot:13,id:totem_of_undying,Count:127},{Slot:14,id:totem_of_undying,Count:127}," +
                "{Slot:15,id:totem_of_undying,Count:127},{Slot:16,id:totem_of_undying,Count:127}," +
                "{Slot:17,id:totem_of_undying,Count:127},{Slot:18,id:totem_of_undying,Count:127}," +
                "{Slot:19,id:totem_of_undying,Count:127},{Slot:20,id:totem_of_undying,Count:127}," +
                "{Slot:21,id:totem_of_undying,Count:127},{Slot:22,id:totem_of_undying,Count:127}," +
                "{Slot:23,id:totem_of_undying,Count:127},{Slot:24,id:totem_of_undying,Count:127}," +
                "{Slot:25,id:totem_of_undying,Count:127},{Slot:26,id:totem_of_undying,Count:127}]}}";
    }

    // illegals kit
    public static String illegals_kit; static {

        illegals_kit = "/give @s black_shulker_box{BlockEntityTag:{Items:[{Slot:0,id:totem_of_undying,Count:127}," +
                "{Slot:1,id:barrier,Count:127},{Slot:2,id:structure_block,Count:127}," +
                "{Slot:3,id:structure_void,Count:127},{Slot:4,id:enchanted_golden_apple,Count:127}," +
                "{Slot:5,id:end_crystal,Count:127},{Slot:6,id:end_portal_frame,Count:127}," +
                "{Slot:7,id:farmland,Count:127},{Slot:8,id:bedrock,Count:127}," +
                "{Slot:9,id:knowledge_book,Count:127},{Slot:10,id:command_block,Count:127}," +
                "{Slot:11,id:player_head,Count:127},{Slot:12,id:creeper_head,Count:127}," +
                "{Slot:13,id:mule_spawn_egg,Count:127},{Slot:14,id:wither_head,Count:127}]}}";
    }

    // 32k feathers
    public static String feather_32k; static {

        feather_32k = "/give @s feather{Enchantments:[{id:sharpness,lvl:32767},{id:knockback,lvl:32767}," +
                "{id:fire_aspect,lvl:32767},{id:looting,lvl:10},{id:sweeping,lvl:3},{id:unbreaking,lvl:32767}," +
                "{id:mending,lvl:1},{id:vanishing_curse,lvl:1}]} 128";
    }

    // invulnerable end-crystal below commander
    public static String invulCrystal; static {

        invulCrystal = "/summon minecraft:end_crystal ~ ~-4 ~ {" +
                "ShowBottom:true,Invulnerable:true,BeamTarget:{X:0.5,Y:128,Z:0.5}}";
    }
}
