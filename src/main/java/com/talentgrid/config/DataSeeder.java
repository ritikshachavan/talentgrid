package com.talentgrid.config;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.entity.*;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.entity.enums.ProjectStatus;
import com.talentgrid.repository.*;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final SkillCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRequiredSkillRepository requiredSkillRepository;
    private final AllocationRepository allocationRepository;

    private final Map<String, Skill> skills = new HashMap<>();

    public DataSeeder(DepartmentRepository departmentRepository,
                      DesignationRepository designationRepository,
                      SkillCategoryRepository categoryRepository,
                      SkillRepository skillRepository,
                      EmployeeRepository employeeRepository,
                      EmployeeSkillRepository employeeSkillRepository,
                      ProjectRepository projectRepository,
                      ProjectRequiredSkillRepository requiredSkillRepository,
                      AllocationRepository allocationRepository) {
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
        this.employeeRepository = employeeRepository;
        this.employeeSkillRepository = employeeSkillRepository;
        this.projectRepository = projectRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.allocationRepository = allocationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // ---- departments and designations ----
        Department eng = dept("Engineering", "Product engineering");
        Department qa = dept("QA", "Quality assurance");
        Department devops = dept("DevOps", "Infrastructure and CI/CD");
        Department data = dept("Data", "Data and analytics");

        Designation swe = desig("Software Engineer", 2);
        Designation senior = desig("Senior Engineer", 3);
        Designation lead = desig("Tech Lead", 4);
        Designation manager = desig("Engineering Manager", 5);

        // ---- categories and skills ----
        SkillCategory backend = category("Backend");
        SkillCategory frontend = category("Frontend");
        SkillCategory database = category("Database");
        SkillCategory cloud = category("Cloud & DevOps");

        skills.clear();
        skill("Java", backend);
        skill("Spring Boot", backend);
        skill("Hibernate", backend);
        skill("Python", backend);
        skill("React", frontend);
        skill("JavaScript", frontend);
        skill("MySQL", database);
        skill("Docker", cloud);
        skill("AWS", cloud);
        skill("Kubernetes", cloud);

        // ---- employees ----
        Employee e101 = emp("E101", "Meera", "Iyer", LocalDate.of(2016, 3, 1), 10.0, EmployeeStatus.ACTIVE, eng, manager, null);
        Employee e102 = emp("E102", "Rahul", "Desai", LocalDate.of(2019, 7, 15), 7.0, EmployeeStatus.ACTIVE, eng, lead, e101);
        Employee e103 = emp("E103", "Priya", "Nair", LocalDate.of(2021, 1, 10), 5.0, EmployeeStatus.ACTIVE, eng, senior, e102);
        Employee e104 = emp("E104", "Karan", "Mehta", LocalDate.of(2023, 6, 1), 2.0, EmployeeStatus.ACTIVE, eng, swe, e102);
        Employee e105 = emp("E105", "Sneha", "Kulkarni", LocalDate.of(2020, 9, 1), 6.0, EmployeeStatus.ACTIVE, devops, senior, e101);
        Employee e106 = emp("E106", "Amit", "Patil", LocalDate.of(2022, 2, 14), 3.0, EmployeeStatus.ON_LEAVE, qa, swe, e101);
        Employee e107 = emp("E107", "Neha", "Joshi", LocalDate.of(2024, 4, 1), 1.5, EmployeeStatus.ACTIVE, eng, swe, e102);
        Employee e108 = emp("E108", "Vikram", "Rao", LocalDate.of(2018, 8, 20), 8.0, EmployeeStatus.ACTIVE, data, senior, e101);
        Employee e109 = emp("E109", "Isha", "Kapoor", LocalDate.of(2022, 5, 9), 4.5, EmployeeStatus.ACTIVE, eng, senior, e102);
        Employee e110 = emp("E110", "Arjun", "Singh", LocalDate.of(2024, 1, 15), 2.5, EmployeeStatus.ACTIVE, devops, swe, e105);

        // ---- employee skills: (employee, skill, proficiency 1-5, years) ----
        give(e101, "Java", 5, 10); give(e101, "Spring Boot", 5, 8); give(e101, "Hibernate", 4, 6); give(e101, "MySQL", 4, 8);
        give(e102, "Java", 5, 7); give(e102, "Spring Boot", 5, 6); give(e102, "Hibernate", 4, 5); give(e102, "MySQL", 4, 6); give(e102, "Docker", 3, 3);
        give(e103, "Java", 4, 5); give(e103, "Spring Boot", 4, 4); give(e103, "Hibernate", 3, 3); give(e103, "MySQL", 3, 4); give(e103, "React", 2, 1);
        give(e104, "Java", 3, 2); give(e104, "Spring Boot", 2, 1); give(e104, "JavaScript", 3, 2); give(e104, "React", 3, 1.5);
        give(e105, "Docker", 5, 6); give(e105, "Kubernetes", 4, 4); give(e105, "AWS", 4, 5); give(e105, "Java", 2, 2);
        give(e106, "JavaScript", 3, 3); give(e106, "Python", 2, 2);
        give(e107, "React", 4, 1.5); give(e107, "JavaScript", 4, 1.5);
        give(e108, "Python", 5, 8); give(e108, "MySQL", 5, 8); give(e108, "AWS", 3, 3);
        give(e109, "Java", 4, 4.5); give(e109, "Spring Boot", 4, 4); give(e109, "React", 3, 2); give(e109, "AWS", 2, 1);
        give(e110, "Docker", 3, 2.5); give(e110, "AWS", 3, 2); give(e110, "Kubernetes", 2, 1);

        // ---- projects and required skills: (skill, min level, min years, weight 1-10) ----
        Project payment = project("Payment Gateway", "UPI and card integration",
                LocalDate.of(2026, 11, 1), LocalDate.of(2027, 3, 31), ProjectStatus.ACTIVE, eng);
        need(payment, "Java", 4, 3, 9);
        need(payment, "Spring Boot", 4, 2, 8);
        need(payment, "Hibernate", 3, 2, 6);
        need(payment, "MySQL", 3, 2, 5);

        Project migration = project("Cloud Migration", "Move services to AWS",
                LocalDate.of(2026, 12, 1), LocalDate.of(2027, 5, 31), ProjectStatus.PLANNED, devops);
        need(migration, "AWS", 4, 3, 9);
        need(migration, "Docker", 4, 3, 8);
        need(migration, "Kubernetes", 3, 2, 7);

        Project analytics = project("Analytics Dashboard", "Reporting for management",
                LocalDate.of(2027, 1, 10), LocalDate.of(2027, 6, 30), ProjectStatus.PLANNED, data);
        need(analytics, "Python", 4, 3, 9);
        need(analytics, "MySQL", 4, 3, 8);
        need(analytics, "React", 3, 1, 5);

        // ---- allocations (E105 ends in 5 days: handy for the Day 3 scheduler) ----
        allocate(e102, payment, "Tech Lead", 50, LocalDate.of(2026, 11, 1), LocalDate.of(2027, 3, 31));
        allocate(e103, payment, "Backend Developer", 60, LocalDate.of(2026, 11, 1), LocalDate.of(2027, 3, 31));
        allocate(e105, migration, "DevOps Engineer", 80, LocalDate.of(2026, 11, 1), LocalDate.now().plusDays(5));

        skills.clear();
        log.info("Seed data ready.");
    }

    // ------------------------------------------------------------------
    // Helpers: each one finds an existing row first and creates it only if missing
    // ------------------------------------------------------------------

    private Department dept(String name, String description) {
        return departmentRepository.findAll().stream()
                .filter(d -> d.getName().equalsIgnoreCase(name)).findFirst()
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName(name);
                    d.setDescription(description);
                    return departmentRepository.save(d);
                });
    }

    private Designation desig(String title, int level) {
        return designationRepository.findAll().stream()
                .filter(d -> d.getTitle().equalsIgnoreCase(title)).findFirst()
                .orElseGet(() -> {
                    Designation d = new Designation();
                    d.setTitle(title);
                    d.setLevel(level);
                    return designationRepository.save(d);
                });
    }

    private SkillCategory category(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name)).findFirst()
                .orElseGet(() -> {
                    SkillCategory c = new SkillCategory();
                    c.setName(name);
                    return categoryRepository.save(c);
                });
    }

    private void skill(String name, SkillCategory category) {
        Skill s = skillRepository.findAll().stream()
                .filter(x -> x.getName().equalsIgnoreCase(name)).findFirst()
                .orElseGet(() -> {
                    Skill n = new Skill();
                    n.setName(name);
                    n.setCategory(category);
                    return skillRepository.save(n);
                });
        skills.put(name, s);
    }

    private Employee emp(String code, String first, String last, LocalDate hired, double years,
                         EmployeeStatus status, Department d, Designation g, Employee mgr) {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getEmpCode().equalsIgnoreCase(code)).findFirst()
                .orElseGet(() -> {
                    Employee e = new Employee();
                    e.setEmpCode(code);
                    e.setFirstName(first);
                    e.setLastName(last);
                    e.setEmail((first + "." + last).toLowerCase() + "@corp.com");
                    e.setHireDate(hired);
                    e.setExperienceYears(years);
                    e.setStatus(status);
                    e.setDepartment(d);
                    e.setDesignation(g);
                    e.setManager(mgr);
                    return employeeRepository.save(e);
                });
    }

    private void give(Employee e, String skillName, int proficiency, double years) {
        Skill s = skills.get(skillName);
        EmployeeSkillId id = new EmployeeSkillId(e.getId(), s.getId());
        if (employeeSkillRepository.existsById(id)) return;
        EmployeeSkill es = new EmployeeSkill();
        es.setId(id);
        es.setEmployee(e);
        es.setSkill(s);
        es.setProficiency(proficiency);
        es.setYearsOfExperience(years);
        employeeSkillRepository.save(es);
    }

    private Project project(String name, String description, LocalDate start, LocalDate end,
                            ProjectStatus status, Department d) {
        return projectRepository.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name)).findFirst()
                .orElseGet(() -> {
                    Project p = new Project();
                    p.setName(name);
                    p.setDescription(description);
                    p.setStartDate(start);
                    p.setEndDate(end);
                    p.setStatus(status);
                    p.setDepartment(d);
                    return projectRepository.save(p);
                });
    }

    private void need(Project p, String skillName, int minProficiency, double minYears, int weight) {
        Skill s = skills.get(skillName);
        ProjectRequiredSkillId id = new ProjectRequiredSkillId(p.getId(), s.getId());
        if (requiredSkillRepository.existsById(id)) return;
        ProjectRequiredSkill rs = new ProjectRequiredSkill();
        rs.setId(id);
        rs.setProject(p);
        rs.setSkill(s);
        rs.setMinProficiency(minProficiency);
        rs.setMinYears(minYears);
        rs.setWeight(weight);
        requiredSkillRepository.save(rs);
    }

    private void allocate(Employee e, Project p, String role, int percent,
                          LocalDate start, LocalDate end) {
        boolean exists = allocationRepository.findByEmployeeId(e.getId()).stream()
                .anyMatch(a -> a.getProject().getId().equals(p.getId()));
        if (exists) return;
        Allocation a = new Allocation();
        a.setEmployee(e);
        a.setProject(p);
        a.setRoleName(role);
        a.setAllocationPercent(percent);
        a.setStartDate(start);
        a.setEndDate(end);
        allocationRepository.save(a);
    }
}