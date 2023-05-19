package com.workwolf.documentservice.nlp;

import lombok.Getter;

public enum WorkExperienceField {
    DATE_START("date_start"), DATE_END("date_end"), JOB_TITLE("jobtitle"), ORGANIZATION("organization");

    @Getter
    private String fieldName;

    WorkExperienceField(String fieldName) {
        this.fieldName = fieldName;
    }
}
