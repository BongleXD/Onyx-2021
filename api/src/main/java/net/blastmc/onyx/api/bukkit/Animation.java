package net.blastmc.onyx.api.bukkit;

import java.util.Arrays;
import java.util.List;

public class Animation {

    private List<Value> valueList;

    public Animation(Value... values){
        this.valueList = Arrays.asList(values);
    }

    public Animation(List<Value> values){
        this.valueList = values;
    }

    public List<Value> getValueList() {
        return valueList;
    }

    public static class Value{

        public String line;
        public int frame;

        public Value(String line, int frame){
            this.line = line;
            this.frame = frame;
        }

    }
}
