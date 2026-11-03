package com.talentgrid.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectRequiredSkillId implements Serializable {

    private Long projectId;
    private Long skillId;

    public ProjectRequiredSkillId() {}

    public ProjectRequiredSkillId(Long projectId, Long skillId) {
        this.projectId = projectId;
        this.skillId = skillId;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectRequiredSkillId that)) return false;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() { return Objects.hash(projectId, skillId); }
}