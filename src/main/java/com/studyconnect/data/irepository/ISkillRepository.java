package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.Skill;
import java.util.List;

public interface ISkillRepository {
    List<Skill> findAll();
    Skill findById(Long skillId);
}