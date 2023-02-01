package com.reiserx.nimbleq.Utils;

import com.reiserx.nimbleq.Models.classModel;
import com.reiserx.nimbleq.Models.subjectAndTimeSlot;

public class TopicSubscription {

    public static String getTopicForSlot(classModel classModel) {
        return classModel.getSubject() + "_" + classModel.getTopic() + "_" + classModel.getTime_slot().replaceAll("\\D+", "");
    }

    public static String getTopicForSlot(subjectAndTimeSlot classModel) {
        if (classModel.getTimeSlot() != null)
            return classModel.getSubject() + "_" + classModel.getTopic() + "_" + classModel.getTimeSlot().replaceAll("\\D+", "");
        else
            return null;
    }
}
