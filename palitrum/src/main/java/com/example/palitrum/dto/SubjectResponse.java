package com.example.palitrum.dto;

public class SubjectResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer standardHoursPerWeek;
    private Long defaultProgramId;
    private String lessonType;
    private Integer minGroupSize;
    private Integer maxGroupSize;

    public SubjectResponse() {}

    public SubjectResponse(Long id, String code, String name, String description,
                           Integer standardHoursPerWeek, Long defaultProgramId,
                           String lessonType, Integer minGroupSize, Integer maxGroupSize) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.standardHoursPerWeek = standardHoursPerWeek;
        this.defaultProgramId = defaultProgramId;
        this.lessonType = lessonType;
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStandardHoursPerWeek() { return standardHoursPerWeek; }
    public void setStandardHoursPerWeek(Integer standardHoursPerWeek) { this.standardHoursPerWeek = standardHoursPerWeek; }

    public Long getDefaultProgramId() { return defaultProgramId; }
    public void setDefaultProgramId(Long defaultProgramId) { this.defaultProgramId = defaultProgramId; }

    public String getLessonType() { return lessonType; }
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }

    public Integer getMinGroupSize() { return minGroupSize; }
    public void setMinGroupSize(Integer minGroupSize) { this.minGroupSize = minGroupSize; }

    public Integer getMaxGroupSize() { return maxGroupSize; }
    public void setMaxGroupSize(Integer maxGroupSize) { this.maxGroupSize = maxGroupSize; }
}