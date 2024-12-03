package app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoordsTest {

    @Test
    void testCoordsInitialization() {
        Coords coords = new Coords(3, 4);
        assertEquals(3, coords.getX());
        assertEquals(4, coords.getY());
    }

    @Test
    void testGetRelXForWhitePerspective() {
        Coords coords = new Coords(3, 4);
        int relX = coords.getRelX(true);
        assertEquals(3, relX);
    }

    @Test
    void testGetRelXForBlackPerspective() {
        Coords coords = new Coords(3, 4);
        int relX = coords.getRelX(false);
        assertEquals(7 - 3, relX);
    }

    @Test
    void testGetRelYForWhitePerspective() {
        Coords coords = new Coords(3, 4);
        int relY = coords.getRelY(true);
        assertEquals(4, relY);
    }

    @Test
    void testGetRelYForBlackPerspective() {
        Coords coords = new Coords(3, 4);
        int relY = coords.getRelY(false);
        assertEquals(7 - 4, relY);
    }

    @Test
    void testEqualityForSameCoordinates() {
        Coords coords1 = new Coords(3, 4);
        Coords coords2 = new Coords(3, 4);
        assertEquals(coords1, coords2);
    }

    @Test
    void testInequalityForDifferentCoordinates() {
        Coords coords1 = new Coords(3, 4);
        Coords coords2 = new Coords(5, 6);
        assertNotEquals(coords1, coords2);
    }

    @Test
    void testBoundaryValues() {
        Coords coords = new Coords(0, 0);
        assertEquals(0, coords.getX());
        assertEquals(0, coords.getY());
    }

    @Test
    void testNegativeCoordinates() {
        Coords coords = new Coords(-1, -2);
        assertEquals(-1, coords.getX());
        assertEquals(-2, coords.getY());
    }

    @Test
    void testNullCoords() {
        Coords coords = null;
        assertNull(coords);
    }
}
