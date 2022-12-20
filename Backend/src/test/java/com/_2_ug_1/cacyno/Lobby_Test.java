package com._2_ug_1.cacyno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com._2_ug_1.cacyno.models.Lobby;
import com._2_ug_1.cacyno.repos.ILobbyRepo;
import com._2_ug_1.cacyno.controllers.LobbyController;

import java.util.ArrayList;
import java.util.Optional;

public class Lobby_Test {

    LobbyController controller = null;
    ILobbyRepo mockLobbyRepo = null;
    Lobby lobby = null;
    ArrayList<Lobby> list = null;

    @BeforeEach
    public void Lobby_Test() {
        mockLobbyRepo = mock(ILobbyRepo.class);
        controller = new LobbyController(mockLobbyRepo);
        lobby = new Lobby();
        list = new ArrayList<>();
    }

    @Test
    public void testGetAll() {
        when(mockLobbyRepo.findAll()).thenReturn(list);
        assertEquals(list, controller.getAll());
    }

    @Test
    public void testGetById() {
        lobby.setId("123");
        list.add(lobby);

        when(mockLobbyRepo.findById("123")).thenReturn(Optional.ofNullable(list.get(0)));
        assertEquals(list.get(0), controller.getLobbyById("123"));
    }


    @Test
    public void testSaveLobby() {
        ArrayList<Lobby> answerKey = new ArrayList<>();
        for (Lobby l : list) {
            answerKey.add(l);
        }
        answerKey.add(lobby);

        doAnswer(x -> {
            list.add(lobby);
            return null;
        }).when(mockLobbyRepo).save(lobby);

        controller.saveLobby(lobby);

        assertEquals(list.toString(), answerKey.toString());
    }


    @Test
    public void testUpdate() {
        lobby.setId("123");
        list.add(lobby);

        Lobby lobby1 = new Lobby();
        lobby1.setId("456");

        ArrayList<Lobby> answerKey = new ArrayList<>();
        for (Lobby l : list) {
            answerKey.add(l);
        }
        answerKey.set(answerKey.indexOf(lobby), lobby1);

        doAnswer(x -> {
            list.set(list.indexOf(lobby), lobby1);
            return null;
        }).when(mockLobbyRepo).save(lobby);

        controller.updateLobby(lobby);

        assertEquals(list.toString(), answerKey.toString());
    }

    @Test
    public void testDelete() {
        lobby.setId("123");
        list.add(lobby);

        ArrayList<Lobby> answerKey = new ArrayList<>();
        for (Lobby l : list) {
            answerKey.add(l);
        }
        answerKey.remove(lobby);

        doAnswer(x -> {
            for (Lobby l : list) {
                if (l.getId().equals(x.getArgument(0))) {
                    list.remove(l);
                    break;
                }
            }
            return null;
        }).when(mockLobbyRepo).deleteById(any(String.class));

        controller.deleteLobby("123");

        assertEquals(list.toString(), answerKey.toString());
    }
}
