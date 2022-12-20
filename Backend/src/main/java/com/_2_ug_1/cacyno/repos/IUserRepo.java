package com._2_ug_1.cacyno.repos;

import com._2_ug_1.cacyno.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.Future;

/**
 * creates an interface for the user table
 */
public interface IUserRepo extends JpaRepository<User, String> {
    @Async
    @Query(value = "SELECT * FROM user WHERE is_spectator = FALSE AND game_id = ?1", nativeQuery = true)
    Future<List<User>> getAllByGameId(String gameId);

    @Async
    @Query(value = "SELECT * FROM user WHERE id = ?1", nativeQuery = true)
    Future<User> AsyncGetById(String id);
}
