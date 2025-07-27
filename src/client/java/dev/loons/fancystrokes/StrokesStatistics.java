package dev.loons.fancystrokes;

import static dev.loons.fancystrokes.Strokes.InputType;
import java.util.HashMap;
import java.util.Map;

public class StrokesStatistics {
    private Map<InputType, Long> keypressCounter;
    private Map<InputType, Long> oldKeypressCounter;
    private long totalPressCounter;
    private long oldTotalPresses;
    public StrokesStatistics(Map<InputType, Long> oldKeypressCounter, long oldTotalPresses){
        this.keypressCounter = new HashMap<>();
        this.totalPressCounter = 0;
        this.oldKeypressCounter = oldKeypressCounter;
        this.oldTotalPresses = oldTotalPresses;
    }

    public void increasePressCount(InputType inputType){
        keypressCounter.put(inputType, keypressCounter.getOrDefault(inputType, 0L) + 1);
        totalPressCounter++;
    }

    public long getTotalPressCounter() {return totalPressCounter;}
    public long getOldTotalPresses() {return oldTotalPresses;}

    public Map<InputType, Long> getOldKeypressCounter() {
        return oldKeypressCounter;
    }

    public Map<InputType, Long> getKeypressCounter() {
        return keypressCounter;
    }
}
