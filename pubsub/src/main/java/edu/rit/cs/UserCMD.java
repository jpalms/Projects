package edu.rit.cs;

import java.util.*;

/*
 * Class to run on publisher / subscriber nodes.
 * Makes a User object based on input.
 */

public class UserCMD {

    private static enum cmdBegin() {
        Scanner initial

        System.out.println(
                "=================================\n" +
                "            User Node            \n" +
                "=================================\n\n");

    }

    private static void startCMD() {
        cmdBegin();
    }

    public static void main(String[] args) {
        startCMD();
    }


}
