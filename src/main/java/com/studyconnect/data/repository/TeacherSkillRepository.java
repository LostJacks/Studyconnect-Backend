package com.studyconnect.data.repository;

import com.studyconnect.data.irepository.ITeacherSkillRepository;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.domain.model.TeacherSkill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherSkillRepository implements ITeacherSkillRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeacherSkillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TeacherSkill save(TeacherSkill teacherSkill) {
        String sql = """
                INSERT INTO teacher_skill (teacher_id, skill_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                teacherSkill.getTeacherId(),
                teacherSkill.getSkillId()
        );

        return teacherSkill;
    }

    @Override
    public List<Skill> findSkillsByTeacherId(Long teacherId) {
        String sql = """
                SELECT s.id, s.name
                FROM teacher_skill ts
                JOIN skill s ON ts.skill_id = s.id
                WHERE ts.teacher_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Skill(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                teacherId
        );
    }
}