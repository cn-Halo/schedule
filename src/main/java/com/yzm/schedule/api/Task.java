package com.yzm.schedule.api;

import java.util.UUID;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public interface Task {

    default String taskId() {
        return UUID.randomUUID().toString();
    }
}
