package com._2_ug_1.cacyno.repos;

import com._2_ug_1.cacyno.models.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * creates an interface for the lobby table
 */
public interface ILobbyRepo extends JpaRepository<Lobby, String> {
}