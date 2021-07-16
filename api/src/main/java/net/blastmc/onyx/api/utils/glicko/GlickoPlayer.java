package net.blastmc.glicko;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GlickoPlayer {

    private final UUID uuid;
    private double rating;
    private double deviation;
    private double volatility;
    private long lastPlay;

    public GlickoPlayer(UUID uuid, double rating, double deviation, double volatility, long lastPlay) {
        this.uuid = uuid;
        this.rating = rating;
        this.deviation = deviation;
        this.volatility = volatility;
        this.lastPlay = lastPlay;
    }

    public double getRating() {
        return rating;
    }

    public double getGlicko2Rating() {
        return (this.rating - 1500) / 173.7178;
    }

    public double getGlicko2RatingDeviation() {
        return this.deviation / 173.7178;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRatingDeviation() {
        return deviation;
    }

    public void setRatingDeviation(double deviation) {
        this.deviation = deviation;
    }

    public double getRatingVolatility() {
        return volatility;
    }

    public void setRatingVolatility(double volatility) {
        this.volatility = volatility;
    }

    public long getLastPlay() {
        return lastPlay;
    }

    public void setLastPlay(long lastPlay) {
        this.lastPlay = lastPlay;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void calcMatch(Match... match) {
        List<Match> matches = Arrays.asList(match);
        double phi = getGlicko2RatingDeviation();
        double sigma = getRatingVolatility();
        double a = Math.log(Math.pow(sigma, 2));
        double delta = delta(matches);
        double v = v(matches);
        double A = a;
        double B;
        double tau = 0.75;
        if (Math.pow(delta, 2) > Math.pow(phi, 2) + v) {
            B = Math.log(Math.pow(delta, 2) - Math.pow(phi, 2) - v);
        } else {
            double k = 1;
            B = a - (k * Math.abs(tau));
            while (f(B, delta, phi, v, a, tau) < 0) {
                k++;
                B = a - (k * Math.abs(tau));
            }
        }
        double fA = f(A, delta, phi, v, a, tau);
        double fB = f(B, delta, phi, v, a, tau);
        while (Math.abs(B - A) > 0.000001) {
            double C = A + (((A - B) * fA) / (fB - fA));
            double fC = f(C, delta, phi, v, a, tau);
            if (fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA = fA / 2.0;
            }
            B = C;
            fB = fC;
        }
        double newSigma = Math.exp(A / 2.0);
        double phiStar = calculateNewRD(phi, newSigma);
        double newPhi = 1.0 / Math.sqrt((1.0 / Math.pow(phiStar, 2)) + (1.0 / v));
        double newMu = getGlicko2Rating() + (Math.pow(newPhi, 2) * outcomeBasedRating(matches));
        this.rating = 173.7178 * newMu + 1500;
        this.deviation = 173.7178 * newPhi;
        this.volatility = newSigma;
    }

    private double g(double deviation) {
        return 1 / (Math.sqrt(1 + ((3 * Math.pow(deviation, 2)) / Math.pow(Math.PI, 2))));
    }

    private double E(double rating, double opponentRating, double opponentDeviation) {
        return 1 / (1 + Math.exp(-g(opponentDeviation) * (rating - opponentRating)));
    }

    private double v(List<Match> matches) {
        return Math.pow(matches.stream().mapToDouble(match -> ((Math.pow(g(match.getOpponentGlicko2Deviation()), 2))
                * E(getGlicko2Rating(),
                match.getOpponentGlicko2Rating(),
                match.getOpponentGlicko2Deviation())
                * (1.0 - E(getGlicko2Rating(),
                match.getOpponentGlicko2Rating(),
                match.getOpponentGlicko2Deviation())
        ))).sum(), -1);
    }

    private double delta(List<Match> matches) {
        return v(matches) * outcomeBasedRating(matches);
    }

    private double outcomeBasedRating(List<Match> matches) {
        return matches.stream().mapToDouble(match -> (g(match.getOpponentGlicko2Deviation())
                * (match.getResult().getScore() - E(
                getGlicko2Rating(),
                match.getOpponentGlicko2Rating(),
                match.getOpponentGlicko2Deviation()))
        )).sum();
    }

    private double calculateNewRD(double phi, double sigma) {
        return Math.sqrt(Math.pow(phi, 2) + Math.pow(sigma, 2));
    }

    private double f(double x, double delta, double phi, double v, double a, double tau) {
        return (Math.exp(x) * (Math.pow(delta, 2) - Math.pow(phi, 2) - v - Math.exp(x)) /
                (2.0 * Math.pow(Math.pow(phi, 2) + v + Math.exp(x), 2))) -
                ((x - a) / Math.pow(tau, 2));
    }

}
