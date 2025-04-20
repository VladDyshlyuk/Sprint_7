package org.example.generators;

import org.example.models.Courier;

import static org.example.utils.Utils.randomString;

public class CourierGenerator {

    public static Courier randomCourier() {
      return new Courier()
                .setLogin(randomString())
                .setPassword(randomString())
                .setFirstName(randomString());
    }
}
