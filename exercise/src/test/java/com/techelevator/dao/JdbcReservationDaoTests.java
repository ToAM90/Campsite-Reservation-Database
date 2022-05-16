package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JdbcReservationDaoTests extends BaseDaoTests {

    private ReservationDao dao;

    @Before
    public void setup() {
        dao = new JdbcReservationDao(dataSource);
    }

    @Test
    public void createReservation_Should_ReturnNewReservationId() {
        int reservationCreated = dao.createReservation(1,
                "TEST NAME",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        assertEquals(5, reservationCreated);
    }

    @Test
    public void returning_list_of_reservations_for_next_30_days_given_park_id(){
        List<Reservation> reservations = dao.getUpcomingReservations(1);
        assertEquals(2,reservations.size());

    }

}
