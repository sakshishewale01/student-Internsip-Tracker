package service;

import dao.InternshipDAO;
import dao.SkillDAO;
import java.util.*;
import model.Internship;
import model.Skill;

public class RecommendationService {
    private final InternshipDAO internshipDAO = new InternshipDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    public List<Internship> getRecommendations(int studentId) {
        List<Skill> studentSkills = skillDAO.getSkillsByStudent(studentId);
        List<Internship> allInternships = internshipDAO.getAll();
        
        if (studentSkills.isEmpty()) return allInternships.subList(0, Math.min(5, allInternships.size()));

        Set<String> skillSet = new HashSet<>();
        for (Skill s : studentSkills) skillSet.add(s.getSkillName().toLowerCase().trim());

        // Score each internship by skill match
        List<Map.Entry<Internship, Integer>> scored = new ArrayList<>();
        for (Internship i : allInternships) {
            int score = calculateMatch(skillSet, i.getRequiredSkills());
            scored.add(new AbstractMap.SimpleEntry<>(i, score));
        }
        scored.sort((a, b) -> b.getValue() - a.getValue());

        List<Internship> result = new ArrayList<>();
        for (Map.Entry<Internship, Integer> e : scored) result.add(e.getKey());
        return result;
    }

    public int calculateMatchPercent(int studentId, Internship internship) {
        List<Skill> studentSkills = skillDAO.getSkillsByStudent(studentId);
        if (studentSkills.isEmpty()) return 0;
        Set<String> skillSet = new HashSet<>();
        for (Skill s : studentSkills) skillSet.add(s.getSkillName().toLowerCase().trim());
        return calculateMatch(skillSet, internship.getRequiredSkills());
    }

    private int calculateMatch(Set<String> studentSkills, String requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 0;
        String[] required = requiredSkills.split(",");
        int matched = 0;
        for (String req : required) {
            if (studentSkills.contains(req.trim().toLowerCase())) matched++;
        }
        return required.length > 0 ? (matched * 100) / required.length : 0;
    }

    public List<String> getSkillGaps(int studentId, Internship internship) {
        List<Skill> studentSkills = skillDAO.getSkillsByStudent(studentId);
        Set<String> skillSet = new HashSet<>();
        for (Skill s : studentSkills) skillSet.add(s.getSkillName().toLowerCase().trim());

        List<String> gaps = new ArrayList<>();
        if (internship.getRequiredSkills() == null) return gaps;
        for (String req : internship.getRequiredSkills().split(",")) {
            if (!skillSet.contains(req.trim().toLowerCase())) gaps.add(req.trim());
        }
        return gaps;
    }
}