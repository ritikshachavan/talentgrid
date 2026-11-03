package com.talentgrid.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "project_required_skills")
public class ProjectRequiredSkill {

    @EmbeddedId
    private ProjectRequiredSkillId id = new ProjectRequiredSkillId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "min_proficiency", nullable = false)
    private Integer minProficiency;

    @Column(name = "min_years", nullable = false)
    private Double minYears;

    @Column(nullable = false)
    private Integer weight;

    public ProjectRequiredSkillId getId() { return id; }
    public void setId(ProjectRequiredSkillId id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Integer getMinProficiency() { return minProficiency; }
    public void setMinProficiency(Integer minProficiency) { this.minProficiency = minProficiency; }

    public Double getMinYears() { return minYears; }
    public void setMinYears(Double minYears) { this.minYears = minYears; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
}