package com.example.time.checker;

import lombok.*;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CheckPoint {

    private long time;
    private String description;
    private UUID timeCheckerId;

    public CheckPoint(CheckPoint checkPoint) {
        Objects.requireNonNull(checkPoint);
        this.time = checkPoint.time;
        this.description = checkPoint.description;
        this.timeCheckerId = checkPoint.timeCheckerId;
    }

}
