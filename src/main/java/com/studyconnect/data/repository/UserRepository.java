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

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_BIO = "bio";
    private static final String COLUMN_PROFILE_IMAGE_URL = "profile_image_url";

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
                                rs.getLong(COLUMN_ID),
                                rs.getString(COLUMN_NAME),
                                rs.getString(COLUMN_EMAIL),
                                rs.getString(COLUMN_PASSWORD_HASH),
                                rs.getString(COLUMN_ROLE),
                                null,
                                null,
                                rs.getString(COLUMN_PROFILE_IMAGE_URL)
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
                                rs.getLong(COLUMN_ID),
                                rs.getString(COLUMN_NAME),
                                rs.getString(COLUMN_EMAIL),
                                rs.getString(COLUMN_PASSWORD_HASH),
                                rs.getString(COLUMN_ROLE),
                                rs.getString(COLUMN_BIO),
                                null,
                                rs.getString(COLUMN_PROFILE_IMAGE_URL)
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
                                rs.getLong(COLUMN_ID),
                                rs.getString(COLUMN_NAME),
                                rs.getString(COLUMN_EMAIL),
                                rs.getString(COLUMN_PASSWORD_HASH),
                                rs.getString(COLUMN_ROLE),
                                null,
                                null,
                                rs.getString(COLUMN_PROFILE_IMAGE_URL)
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
    public List<UserEntity> findTeachersPaged(int limit, int offset) {
        String sql = """
            SELECT id, name, email, password_hash, role, bio, profile_image_url
            FROM users
            WHERE role = 'TEACHER'
            LIMIT ? OFFSET ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new UserEntity(
                        rs.getLong(COLUMN_ID),
                        rs.getString(COLUMN_NAME),
                        rs.getString(COLUMN_EMAIL),
                        rs.getString(COLUMN_PASSWORD_HASH),
                        rs.getString(COLUMN_ROLE),
                        rs.getString(COLUMN_BIO),
                        null,
                        rs.getString(COLUMN_PROFILE_IMAGE_URL)
                ),
                limit,
                offset
        );
    }

    @Override
    public UserEntity findEntityById(Long id) {
        String sql = """
            SELECT id, name, email, password_hash, role, bio, profile_image_url
            FROM users
            WHERE id = ?
            """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new UserEntity(
                                rs.getLong(COLUMN_ID),
                                rs.getString(COLUMN_NAME),
                                rs.getString(COLUMN_EMAIL),
                                rs.getString(COLUMN_PASSWORD_HASH),
                                rs.getString(COLUMN_ROLE),
                                rs.getString(COLUMN_BIO),
                                null,
                                rs.getString(COLUMN_PROFILE_IMAGE_URL)
                        );
                    }

                    return null;
                },
                id
        );
    }

    @Override
    public long countTeachers() {
        String sql = """
            SELECT COUNT(*)
            FROM users
            WHERE role = 'TEACHER'
            """;

        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        return count == null ? 0 : count;
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
                        rs.getLong(COLUMN_ID),
                        rs.getString(COLUMN_NAME),
                        rs.getString(COLUMN_EMAIL),
                        rs.getString(COLUMN_PASSWORD_HASH),
                        rs.getString(COLUMN_ROLE),
                        rs.getString(COLUMN_BIO),
                        null,
                        rs.getString(COLUMN_PROFILE_IMAGE_URL)
                )
        );
    }

}