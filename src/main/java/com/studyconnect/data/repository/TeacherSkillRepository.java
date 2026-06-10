package com.studyconnect.data.repository;

import com.studyconnect.data.entity.SkillEntity;
import com.studyconnect.data.entity.TeacherSkillEntity;
import com.studyconnect.data.irepository.ITeacherSkillRepository;
import com.studyconnect.data.mapper.SkillMapper;
import com.studyconnect.domain.model.Skill;
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
    public void saveByIds(Long teacherId, Long skillId) {
        String sql = """
                INSERT INTO teacher_skill (teacher_id, skill_id)
                VALUES (?, ?)
                """;

        TeacherSkillEntity entity = new TeacherSkillEntity(teacherId, skillId);

        jdbcTemplate.update(
                sql,
                entity.getTeacherId(),
                entity.getSkillId()
        );
    }

    @Override
    public List<Skill> findSkillsByTeacherId(Long teacherId) {
        String sql = """
                SELECT s.id, s.name
                FROM teacher_skill ts
                JOIN skill s ON ts.skill_id = s.id
                WHERE ts.teacher_id = ?
                """;

        List<SkillEntity> entities = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SkillEntity(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                teacherId
        );

        return entities.stream()
                .map(SkillMapper::toDomain)
                .toList();
    }
}