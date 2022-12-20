package com._2_ug_1.cacyno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com._2_ug_1.cacyno.models.User;
import com._2_ug_1.cacyno.repos.IUserRepo;
import com._2_ug_1.cacyno.controllers.UserController;

import java.util.ArrayList;
import java.util.Optional;

public class User_Test {

    User user = null;
    IUserRepo mockUserRepo = null;
    UserController controller = null;
    ArrayList<User> list = null;

    @BeforeEach
    public void User_Test() {
        user = new User();
        mockUserRepo = mock(IUserRepo.class);
        controller = new UserController(mockUserRepo);
        list = new ArrayList<>();
    }

    @Test
    public void testGetAll() {
        when(mockUserRepo.findAll()).thenReturn(list);
        assertEquals(list, controller.getAll());
    }


    @Test
    public void testGetUserById() {
        user.setId("123");
        list.add(user);

        when(mockUserRepo.findById("123")).thenReturn(Optional.ofNullable(list.get(0)));
        assertEquals(list.get(0), controller.getUserById("123"));
    }

    @Test
    public void testSaveUser() {
        ArrayList<User> answerKey = new ArrayList<>();
        for (User u : list) {
            answerKey.add(u);
        }
        answerKey.add(user);

        doAnswer(x -> {
            list.add(user);
            return null;
        }).when(mockUserRepo).save(user);

        controller.saveUser(user);

        assertEquals(list.toString(), answerKey.toString());
    }

    @Test
    public void testUpdateUser() {
        user.setId("123");
        list.add(user);

        User user1 = new User();
        user1.setId("456");

        ArrayList<User> answerKey = new ArrayList<>();
        for (User u : list) {
            answerKey.add(u);
        }
        answerKey.set(answerKey.indexOf(user), user1);

        doAnswer(x -> {
            list.set(list.indexOf(user), user1);
            return null;
        }).when(mockUserRepo).save(user);

        controller.updateUser(user);

        assertEquals(list.toString(), answerKey.toString());
    }

    @Test
    public void testDeleteUser() {//DOESN'T WORK
        user.setId("123");
        list.add(user);

        ArrayList<User> answerKey = new ArrayList<>();
        for (User u : list) {
            answerKey.add(u);
        }
        answerKey.remove(user);

        doAnswer(x -> {
            for (User u : list) {
                if (u.getId().equals(x.getArgument(0))) {
                    list.remove(u);
                    break;
                }
            }
            return null;
        }).when(mockUserRepo).deleteById(any(String.class));

        controller.deleteUser("123");

        assertEquals(list.toString(), answerKey.toString());
    }


}
