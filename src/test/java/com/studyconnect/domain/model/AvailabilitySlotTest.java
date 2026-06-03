package com.studyconnect.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilitySlotTest {

    private static final LocalDate DATE = LocalDate.of(2026, 5, 30);
    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(10, 30);

    @Test
    void constructor_validData_createsAvailabilitySlot() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, false, true);

        assertEquals(DATE, slot.getDate());
        assertEquals(START_TIME, slot.getStartTime());
        assertEquals(END_TIME, slot.getEndTime());
        assertFalse(slot.isBooked());
        assertTrue(slot.isAvailable());
    }

    @Test
    void constructor_bookedTrue_createsBookedSlot() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, true, false);

        assertTrue(slot.isBooked());
        assertFalse(slot.isAvailable());
    }

    @Test
    void constructor_nullDate_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AvailabilitySlot(null, START_TIME, END_TIME, false, true)
        );
    }

    @Test
    void constructor_nullStartTime_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AvailabilitySlot(DATE, null, END_TIME, false, true)
        );
    }

    @Test
    void constructor_nullEndTime_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AvailabilitySlot(DATE, START_TIME, null, false, true)
        );
    }

    @Test
    void constructor_startTimeAfterEndTime_throwsException() {
        LocalTime invalidStartTime = LocalTime.of(11, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new AvailabilitySlot(DATE, invalidStartTime, END_TIME, false, true)
        );
    }

    @Test
    void constructor_startTimeEqualsEndTime_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AvailabilitySlot(DATE, START_TIME, START_TIME, false, true)
        );
    }

    @Test
    void markAsBooked_whenAvailable_setsBookedToTrueAndAvailableToFalse() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, false, true);

        slot.markAsBooked();

        assertTrue(slot.isBooked());
        assertFalse(slot.isAvailable());
    }

    @Test
    void markAsBooked_whenAlreadyBooked_throwsException() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, true, false);

        assertThrows(IllegalStateException.class, slot::markAsBooked);
    }

    @Test
    void markAsAvailable_whenBooked_throwsException() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, true, false);

        assertThrows(IllegalStateException.class, slot::markAsAvailable);

        assertTrue(slot.isBooked());
        assertFalse(slot.isAvailable());
    }

    @Test
    void markAsAvailable_whenNotBooked_setsAvailableToTrue() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, false, false);

        slot.markAsAvailable();

        assertFalse(slot.isBooked());
        assertTrue(slot.isAvailable());
    }

    @Test
    void markAsAvailable_whenAlreadyAvailable_keepsSlotAvailable() {
        AvailabilitySlot slot = new AvailabilitySlot(DATE, START_TIME, END_TIME, false, true);

        slot.markAsAvailable();

        assertFalse(slot.isBooked());
        assertTrue(slot.isAvailable());
    }
}