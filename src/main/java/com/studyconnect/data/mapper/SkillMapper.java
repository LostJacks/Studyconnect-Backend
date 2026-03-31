package com.studyconnect.data.mapper;

import com.studyconnect.data.entity.SkillEntity;
import com.studyconnect.domain.model.Skill;

public class SkillMapper {

    public static Skill toDomain(SkillEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Skill(
                entity.getName()
        );
    }

    public static SkillEntity toEntity(Skill domain) {
        if (domain == null) {
            return null;
        }

        return new SkillEntity(
                null,
                domain.getName()
        );
    }
}