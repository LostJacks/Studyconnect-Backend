package com.studyconnect.data.repository;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.data.mapper.UserMapper;
import com.studyconnect.domain.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateBio(Long userId, String bio) {
        String sql = """
                UPDATE users
                SET bio = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, bio, userId);
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
                SELECT id, name, email, password_hash, role, profile_image_url
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
                                rs.getString("role"),
                                null,
                                null,
                                rs.getString("profile_image_url")
                        );
                    }
                    return null;
                },
                email
        );

        return UserMapper.toDomain(entity);
    }

    @Override
    public UserEntity findEntityByEmail(String email) {
        String sql = """
                SELECT id, name, email, password_hash, role, bio, profile_image_url
                FROM users
                WHERE email = ?
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new UserEntity(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password_hash"),
                                rs.getString("role"),
                                rs.getString("bio"),
                                null,
                                rs.getString("profile_image_url")
                        );
                    }
                    return null;
                },
                email
        );
    }

    @Override
    public User findById(Long id) {

        String sql = """
                SELECT id, name, email, password_hash, role, profile_image_url
                FROM users
                WHERE id = ?
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
                                rs.getString("role"),
                                null,
                                null,
                                rs.getString("profile_image_url")
                        );
                    }
                    return null;
                },
                id
        );

        return UserMapper.toDomain(entity);
    }

    @Override
    public void updateProfileImage(Long userId, String profileImageUrl) {
        String sql = """
            UPDATE users
            SET profile_image_url = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(sql, profileImageUrl, userId);
    }

    @Override
    public List<UserEntity> findAll() {

        String sql = """
                SELECT id, name, email, password_hash, role, bio, profile_image_url
                FROM users
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new UserEntity(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("bio"),
                        null,
                        rs.getString("profile_image_url")
                )
        );
    }
}