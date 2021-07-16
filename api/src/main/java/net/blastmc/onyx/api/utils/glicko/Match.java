package net.blastmc.glicko;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.OptionalDouble;

public class Match {

    private final double opponentRating;
    private final double opponentDeviation;
    private final Result result;

    public Match(double opponentGlicko2Rating, double opponentGlicko2Deviation, @NotNull Result result) {
        this.opponentRating = opponentGlicko2Rating;
        this.opponentDeviation = opponentGlicko2Deviation;
        this.result = result;
    }

    public Match(@NotNull Map<Double, Double> map, @NotNull Result result) {
        OptionalDouble opt1 = map.keySet().stream().mapToDouble(Double::doubleValue).average();
        if(opt1.isPresent()){
            this.opponentRating = opt1.getAsDouble();
        }else{
            throw new UnsupportedOperationException("key can't be empty");
        }
        OptionalDouble opt2 = map.keySet().stream().mapToDouble(Double::doubleValue).average();
        if(opt2.isPresent()){
            this.opponentDeviation = opt2.getAsDouble();
        }else{
            throw new UnsupportedOperationException("key can't be empty");
        }
        this.result = result;
    }

    public double getOpponentGlicko2Rating() {
        return this.opponentRating;
    }

    public double getOpponentGlicko2Deviation() {
        return this.opponentDeviation;
    }

    public Result getResult() {
        return this.result;
    }

    public static enum Result{

        WIN(1.0),
        LOSS(0),
        DRAW(0.5);

        protected final double score;

        Result(double score){
            this.score = score;
        }

        public double getScore() {
            return score;
        }

    }

}
