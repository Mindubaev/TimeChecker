package com.example.time.checker;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class TimeCheckerTest {

    @Test
    public void test() {
        try (MockedStatic<NanoTimer> systemMock = Mockito.mockStatic(NanoTimer.class)) {
            systemMock.when(NanoTimer::getNano).thenReturn(0l);
            TimeChecker checker = new TimeChecker();
            systemMock.when(NanoTimer::getNano).thenReturn(3000_000_000l);
            CheckPoint first = checker.checkPoint("First check point");
            systemMock.when(NanoTimer::getNano).thenReturn(6000_000_000l);
            CheckPoint second = checker.checkPoint("Second check point");
            systemMock.when(NanoTimer::getNano).thenReturn(10_000_000_000l);
            CheckPoint third = checker.checkPoint("Third check point");
            Assert.assertEquals(List.of(first, second), checker.beforeOrEq(second));
            Assert.assertEquals(List.of(second, third), checker.afterOrEq(second));
            Assert.assertEquals(List.of(first, second, third), checker.betweenOrEq(third, first));
            Assert.assertEquals(3000, checker.durationBefore(second, ChronoUnit.MILLIS));
            Assert.assertEquals(7, checker.durationBetween(first, third, ChronoUnit.SECONDS));
            checker.printCheckPoints(ChronoUnit.SECONDS);
        }
    }

}
