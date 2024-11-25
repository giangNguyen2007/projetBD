package org.example;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void findTotalQteAchatPerUser_Case1() {

        int res = 0;
        try {
            res = Utility.findTotalQteAchatPerUser(1, "giang.nguyen@gmail");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertEquals(6, res);

    }

    @Test
    void findTotalQteAchatPerUser_Case2_NoUser() {

        int res = 0;
        try {
            res = Utility.findTotalQteAchatPerUser(1, "giang2.nguyen@gmail");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}