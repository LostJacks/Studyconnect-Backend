package com.studyconnect.data.repository;

import com.studyconnect.data.entity.SkillEntity;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.mapper.SkillMapper;
import com.studyconnect.domain.model.Skill;
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