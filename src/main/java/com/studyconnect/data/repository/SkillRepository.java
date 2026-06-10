package com.studyconnect.data.repository;

import com.studyconnect.data.entity.SkillEntity;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.mapper.SkillMapper;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkillRepository implements ISkillRepository {

    private final JdbcTemplate jdbcTemplate;

    public SkillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Skill> findAll() {
        String sql = """
                SELECT id, name
                FROM skill
                """;

        List<SkillEntity> entities = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SkillEntity(
                        rs.getLong("id"),
                        rs.getString("name")
                )
        );

        return entities.stream()
                .map(SkillMapper::toDomain)
                .toList();
    }

    @Override
    public List<String> findSkillNamesByTeacherId(Long teacherId) {
        String sql = """
            SELECT s.name
            FROM teacher_skill ts
            JOIN skill s ON ts.skill_id = s.id
            WHERE ts.teacher_id = ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("name"),
                teacherId
        );
    }

    @Override
    public List<SkillResponse> findSkillsByTeacherId(Long teacherId) {
        String sql = """
            SELECT s.id, s.name
            FROM teacher_skill ts
            JOIN skill s ON ts.skill_id = s.id
            WHERE ts.teacher_id = ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SkillResponse(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                teacherId
        );
    }

    @Override
    public List<SkillResponse> findAllSkillResponses() {
        String sql = """
            SELECT id, name
            FROM skill
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SkillResponse(
                        rs.getLong("id"),
                        rs.getString("name")
                )
        );
    }
    @Override
    public void deleteTeacherSkillsByTeacherId(Long teacherId) {
        String sql = """
        DELETE FROM teacher_skill
        WHERE teacher_id = ?
        """;

        jdbcTemplate.update(sql, teacherId);
    }

    @Override
    public void addTeacherSkill(Long teacherId, Long skillId) {
        String sql = """
        INSERT INTO teacher_skill (teacher_id, skill_id)
        VALUES (?, ?)
        """;

        jdbcTemplate.update(sql, teacherId, skillId);
    }

    @Override
    public Skill findById(Long skillId) {
        String sql = """
                SELECT id, name
                FROM skill
                WHERE id = ?
                """;

        SkillEntity entity = jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new SkillEntity(
                                rs.getLong("id"),
                                rs.getString("name")
                        );
                    }
                    return null;
                },
                skillId
        );

        return SkillMapper.toDomain(entity);
    }
}