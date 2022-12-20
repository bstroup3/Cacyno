package com._2_ug_1.cacyno.repos;

import com._2_ug_1.cacyno.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * Interfaces between the server and the games in the database
 */
public interface IGameRepo extends JpaRepository<Game, String> {
    /**
     * gets games from the database that have the specified id
     *
     * @param id the id of the requested game
     * @return A game with the specified id
     */
    @Async
    @Query(value = "SELECT * FROM game WHERE id = ?1", nativeQuery = true)
    Future<Game> AsyncGetById(String id);
}