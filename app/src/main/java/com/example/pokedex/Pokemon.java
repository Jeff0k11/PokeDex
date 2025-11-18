package com.example.pokedex;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
    private Sprites sprites;
    private List<AbilityInfo> abilities;
    private String name;
    private int height;
    private int weight;
    private List<TypeInfo> types;
    private List<StatInfo> stats;

    public Sprites getSprites() { return sprites; }
    public List<AbilityInfo> getAbilities() { return abilities; }
    public String getName() { return name; }
    public int getHeight() { return height; }
    public int getWeight() { return weight; }
    public List<TypeInfo> getTypes() { return types; }
    public List<StatInfo> getStats() { return stats; }

    public class Sprites {
        private String front_default;
        public String getFrontDefault() { return front_default; }
    }

    public class AbilityInfo {
        private Ability ability;
        public Ability getAbility() { return ability; }
    }

    public class Ability {
        private String name;
        public String getName() { return name; }
    }

    public class TypeInfo {
        private Type type;
        public Type getType() { return type; }
    }

    public class Type {
        private String name;
        public String getName() { return name; }
    }

    public class StatInfo {
        @SerializedName("base_stat")
        private int baseStat;
        private Stat stat;
        public int getBaseStat() { return baseStat; }
        public Stat getStat() { return stat; }
    }

    public class Stat {
        private String name;
        public String getName() { return name; }
    }
}