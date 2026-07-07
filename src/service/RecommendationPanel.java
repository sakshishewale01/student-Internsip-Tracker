package service;

import java.util.List;
import model.Internship;
import model.Student;

// Wrapper panel class (logic is in RecommendationService)
public class RecommendationPanel {
    private final RecommendationService service = new RecommendationService();
    
    public List<Internship> getTopRecommendations(Student student, int limit) {
        List<Internship> recs = service.getRecommendations(student.getId());
        return recs.subList(0, Math.min(limit, recs.size()));
    }
    
    public String getRecommendationSummary(Student student, Internship internship) {
        int match = service.calculateMatchPercent(student.getId(), internship);
        List<String> gaps = service.getSkillGaps(student.getId(), internship);
        StringBuilder sb = new StringBuilder();
        sb.append("Match: ").append(match).append("%\n");
        if (!gaps.isEmpty()) {
            sb.append("Missing skills: ").append(String.join(", ", gaps));
        } else {
            sb.append("✅ You match all required skills!");
        }
        return sb.toString();
    }
}