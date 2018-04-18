package com.se2006.teamkaydon.powerfull.Boundary;

import com.se2006.teamkaydon.powerfull.Control.FirebaseManager;

import org.junit.Test;

import static org.junit.Assert.*;

public class PortableChargerActivityTest {


    @Test
    public void borrowPortable() {
        int input = 20;
        boolean output;
        boolean expected = true;
        double delta = 0.1;

        PortableChargerActivity p = new PortableChargerActivity();
        FirebaseDAO f = new FirebaseManager();
        p.currentValue = 20;
        output = p.BorrowPortable("1");
        assertTrue(output == expected);

    }
}