package com.torchteam.thetorch.service;

import com.torchteam.thetorch.model.Rider;
import com.torchteam.thetorch.repository.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RiderService {

    private final RiderRepository riderRepository;

    public RiderService(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    @Transactional
    public Rider createRider(String name, int age, String gender, String tuidToken) {
        Rider existing = riderRepository.findByName(name);
        if (existing != null) {
            throw new IllegalArgumentException("A rider with this student mail already exists.");
        }

        Rider rider = new Rider();
        rider.setName(name);
        rider.setGender(gender.toUpperCase());
        rider.setTuidToken(tuidToken);

        double coefficient = calculateCoefficient(age, gender);
        rider.setCoefficient(coefficient);

        return riderRepository.save(rider);
    }

    @Transactional
    public Rider getRider(String name) {
        return riderRepository.findByName(name);
    }

    private double calculateCoefficient(int age, String gender) {
        double coefficient = 1.0;

        // Age modifiers
        if (age >= 55) {
            coefficient += 0.4;
        } else if (age >= 45) {
            coefficient += 0.25;
        } else if (age >= 35) {
            coefficient += 0.1;
        } // 0-34 gets no change (+0.0)

        // Gender modifier (anything other than M gets +0.2)
        if (!"M".equalsIgnoreCase(gender)) {
            coefficient += 0.2;
        }

        return coefficient;
    }
}