package com.drukido.vrun.utils;

import com.drukido.vrun.entities.Run;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ido on 2/3/2016.
 */
public class RunsAnalyzer {
    private static final long MINUETS = 60;
    private static final long SECONDS = 60;
    private static final long KM_UNIT = 1000;
    private List<Run> allRuns;
    private List<Run> measuredRuns;
    private boolean mIsMeasuredRunsAvailable = false;

    public RunsAnalyzer(List<Run> runsList) {
        this.allRuns = runsList;
        this.measuredRuns = new ArrayList<>();
        cleanNotMeasuredRuns();
    }

    private void cleanNotMeasuredRuns() {
        for(Run run:allRuns) {
            if ((run.getDistance()) != 0 && (run.getDuration() != null)) {
                measuredRuns.add(run);
            }
        }

        if (measuredRuns.size() > 0) {
            mIsMeasuredRunsAvailable = true;
        }
    }

    public boolean isMeasuredRunsAvailable() {
        return mIsMeasuredRunsAvailable;
    }

    public int getAllRunsCount() {
        return allRuns.size();
    }

    public int getMeasuredRunsCount() {
        return measuredRuns.size();
    }

    public Run getBestRun() {
        Run bestRun = measuredRuns.get(0);

        for (Run run:measuredRuns) {
            if (bestRun.getDistance() < run.getDistance()) {
                bestRun = run;
            }
        }

        return bestRun;
    }

    public Duration getOverAllRunTime() {
        Duration currDuration;
        long allSeconds = 0;

        for(Run currRun:measuredRuns) {
            currDuration = Duration.fromString(currRun.getDuration());
            allSeconds += (currDuration.getHours() * MINUETS * SECONDS) +
                    (currDuration.getMinutes() * SECONDS) + currDuration.getSeconds();
        }

        long tempAllSeconds = allSeconds;
        Duration durationResult = new Duration();
        durationResult.setHours(tempAllSeconds / (MINUETS * SECONDS));
        tempAllSeconds %= (MINUETS * SECONDS);
        durationResult.setMinutes(tempAllSeconds / MINUETS);
        tempAllSeconds %= MINUETS;
        durationResult.setSeconds(tempAllSeconds);

        return durationResult;
    }

    public long getOverAllDistance() {
        long result = 0;

        for (Run currRun:measuredRuns){
            result += currRun.getDistance();
        }


        return result;
    }

    public double getOverAllDistanceAverage() {
        return ((double)(getOverAllDistance())) / measuredRuns.size();
    }

    public Duration getOverAllRunTimeAverage() {
        Duration currDuration;
        long allSeconds = 0;

        for(Run currRun:measuredRuns) {
            currDuration = Duration.fromString(currRun.getDuration());
            allSeconds += (currDuration.getHours() * MINUETS * SECONDS) +
                    (currDuration.getMinutes() * SECONDS) + currDuration.getSeconds();
        }

        // Average
        allSeconds /= measuredRuns.size();

        long tempAllSeconds = allSeconds;
        Duration durationResult = new Duration();
        durationResult.setHours(tempAllSeconds / (MINUETS * SECONDS));
        tempAllSeconds %= (MINUETS * SECONDS);
        durationResult.setMinutes(tempAllSeconds / MINUETS);
        tempAllSeconds %= MINUETS;
        durationResult.setSeconds(tempAllSeconds);

        return durationResult;
    }

    public double getAverageSpeed() {
        Duration overallDuration = getOverAllRunTime();
        double allHours = overallDuration.getHours() +
                (((double)(overallDuration.getMinutes()) / MINUETS)) +
                (((double)(overallDuration.getSeconds()) / MINUETS / SECONDS));
        return (((double)(getOverAllDistance())) / KM_UNIT) / allHours;
    }

    public ArrayList<String> getRunsDates() {
        ArrayList<String> strings = new ArrayList<>();

        for (Run run:measuredRuns){
            strings.add(DateHelper.getTimeStringFromDate(run.getRunTime()));
        }

        return strings;
    }

    public ArrayList<String> getRunsDistance() {
        ArrayList<String> strings = new ArrayList<>();

        for (Run run:measuredRuns){
            strings.add(String.valueOf(((double)run.getDistance()) / KM_UNIT));
        }

        return strings;
    }
}
