package com.example.time.checker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TimeChecker {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
    private final LocalDateTime startDateTime = LocalDateTime.now();
    private final UUID id = UUID.randomUUID();
    private final long start = NanoTimer.getNano();
    private final List<CheckPoint> checkPoints = Collections.synchronizedList(new ArrayList<>());

    public CheckPoint checkPoint(String description) {
        CheckPoint checkPoint = new CheckPoint(NanoTimer.getNano(), description, UUID.randomUUID());
        checkPoints.add(new CheckPoint(checkPoint));
        return checkPoint;
    }

    public long durationBetween(CheckPoint checkPoint1, CheckPoint checkPoint2, ChronoUnit chronoUnit) {
        Objects.requireNonNull(checkPoint1);
        Objects.requireNonNull(checkPoint2);
        Objects.requireNonNull(chronoUnit);
        Duration duration = Duration.of(checkPoint1.getTime() - checkPoint2.getTime(), ChronoUnit.NANOS).abs();
        return switch (chronoUnit) {
            case NANOS -> duration.toNanos();
            case MILLIS -> duration.toMillis();
            case SECONDS -> duration.toSeconds();
            case MINUTES -> duration.toMinutes();
            case HOURS -> duration.toHours();
            default -> throw new TimeCheckerException("Not implemented");
        };
    }

    public long durationBefore(CheckPoint checkPoint, ChronoUnit unit) {
        Objects.requireNonNull(checkPoint);
        checkInteraction(checkPoint);
        CheckPoint first = checkPoints.get(0);
        return durationBetween(first, checkPoint, unit);
    }

    public synchronized List<CheckPoint> afterOrEq(CheckPoint checkPoint) {
        Objects.requireNonNull(checkPoint);
        checkInteraction(checkPoint);
        int index = checkPoints.indexOf(checkPoint);
        List<CheckPoint> after = new ArrayList<>();
        synchronized (checkPoints) {
            for (int i = index; (i < checkPoints.size() && i > -1); i++) {
                after.add(new CheckPoint(checkPoints.get(i)));
            }
        }
        return after;
    }

    public synchronized List<CheckPoint> beforeOrEq(CheckPoint checkPoint) {
        Objects.requireNonNull(checkPoint);
        checkInteraction(checkPoint);
        int index = checkPoints.indexOf(checkPoint);
        List<CheckPoint> before = new ArrayList<>();
        synchronized (checkPoints) {
            for (int i = 0; i <= index; i++) {
                before.add(new CheckPoint(checkPoints.get(i)));
            }
        }
        return before;
    }

    public synchronized List<CheckPoint> betweenOrEq(CheckPoint checkPoint1, CheckPoint checkPoint2) {
        Objects.requireNonNull(checkPoint1);
        Objects.requireNonNull(checkPoint2);
        checkInteraction(checkPoint1);
        checkInteraction(checkPoint2);
        CheckPoint max = checkPoint1;
        CheckPoint min = checkPoint2;
        if (checkPoint1.getTime() < checkPoint2.getTime()) {
            max = checkPoint2;
            min = checkPoint1;
        }
        int maxIndex = checkPoints.indexOf(max);
        int minIndex = checkPoints.indexOf(min);

        List<CheckPoint> between = new ArrayList<>();
        synchronized (checkPoints) {
            for (int i = minIndex; i <= maxIndex; i++) {
                between.add(new CheckPoint(checkPoints.get(i)));
            }
        }
        return between;
    }

    private void checkInteraction(CheckPoint checkPoint) {
        if (id.equals(checkPoint.getTimeCheckerId())) {
            throw new TimeCheckerException(
                    String.format("Can not interact with current check point. Check point \'%s\' was was created in another TimeChecker!", checkPoint.getDescription())
            );
        }
    }

    public void printCheckPoints(ChronoUnit unit) {
        printCheckPoints(checkPoints, unit);
    }

    public void printCheckPoints(List<CheckPoint> checkPointsSeq, ChronoUnit unit) {
        StringBuilder infoBuilder = new StringBuilder();
        for (int i = 0; i < checkPointsSeq.size() - 1; i++) {
            CheckPoint prev = checkPointsSeq.get(i);
            if (i == 0) {
                LocalDateTime time = startDateTime.plusNanos(durationBefore(prev, ChronoUnit.NANOS));
                infoBuilder.append(String.format(
                        "[%s] %s%n", time.format(dateTimeFormatter), prev.getDescription()
                ));
            }
            CheckPoint next = checkPointsSeq.get(i + 1);
            infoBuilder.append(String.format(
                    "%s %s%n", durationBetween(prev, next, unit), unit.toString()
            ));
            LocalDateTime time = startDateTime.plusNanos(durationBefore(next, ChronoUnit.NANOS));
            infoBuilder.append(String.format(
                    "[%s] %s%n", time.format(dateTimeFormatter), next.getDescription()
            ));
        }
        System.out.println(infoBuilder.toString());
    }

    @Override
    public String toString() {
        return "TimeChecker{" +
                "startDateTime=" + startDateTime +
                ", id=" + id +
                ", start=" + start +
                ", checkPoints=" + checkPoints +
                '}';
    }
}
