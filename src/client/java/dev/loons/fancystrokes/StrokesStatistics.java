package dev.loons.fancystrokes;

import static dev.loons.fancystrokes.Strokes.InputType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

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
    public long getLifetimePresses(){return totalPressCounter+oldTotalPresses;}
    public long getSpecificLifetimePresses(InputType inputType){
        long currentCount = 0L;
        if (this.keypressCounter != null) {
            currentCount = this.keypressCounter.getOrDefault(inputType, 0L);
        }

        long oldCount = 0L;
        if (this.oldKeypressCounter != null) {
            oldCount = this.oldKeypressCounter.getOrDefault(inputType, 0L);
        }

        return currentCount + oldCount;
    }
    public long getSpecificInstancePresses(InputType inputType){
        long currentCount = 0L;
        if (this.keypressCounter != null) {
            currentCount = this.keypressCounter.getOrDefault(inputType, 0L);
        }
        return currentCount;
    }

    public Map<InputType, Long> getOldKeypressCounter() {
        return oldKeypressCounter;
    }

    public Map<InputType, Long> getKeypressCounter() {
        return keypressCounter;
    }

    public List<Map.Entry<InputType, Long>> getTop3LifetimePresses() {
        Map<InputType, Long> allLifetimePresses = new HashMap<>();

        for (InputType type : InputType.values()) {
            if (type == InputType.NULL) {
                continue;
            }
            allLifetimePresses.put(type, getSpecificLifetimePresses(type));
        }

        return allLifetimePresses.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3) // Nur die Top 3 nehmen
                .collect(Collectors.toList());
    }

}
