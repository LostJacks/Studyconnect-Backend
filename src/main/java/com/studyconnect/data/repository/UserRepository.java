package com.studyconnect.data.repository;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.data.mapper.UserMapper;
import com.studyconnect.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {

        UserEntity entity = UserMapper.toEntity(user);

        String sql = """
                INSERT INTO users (name, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole()
        );

        return user;
    }

    @Override
    public User findByEmail(String email) {

        String sql = """
                SELECT id, name, email, password_hash, role
                FROM users
                WHERE email = ?
                """;

        UserEntity entity = jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new UserEntity(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password_hash"),
                                rs.getString("role")
                        );
                    }
                    return null;
                },
                email
        );

        return UserMapper.toDomain(entity);
    }
}