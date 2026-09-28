package com.torchteam.thetorch.service;

import com.torchteam.thetorch.dto.RiderStatsDto;
import com.torchteam.thetorch.exception.IncompleteAccountException;
import com.torchteam.thetorch.model.*;
import com.torchteam.thetorch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AchievementEvaluationService {

    private final AchievementRepository achievementRepository;
    private final CollectedRepository collectedRepository;
    private final MembershipRepository membershipRepository;
    private final RideAttributionRepository attributionRepository;
    private final DecorativePieceRepository decorativePieceRepository;
    private final TeamPieceOwnershipRepository teamPieceOwnershipRepository;

    public AchievementEvaluationService(AchievementRepository achievementRepository,
                                        CollectedRepository collectedRepository,
                                        MembershipRepository membershipRepository,
                                        RideAttributionRepository attributionRepository,
                                        DecorativePieceRepository decorativePieceRepository,
                                        TeamPieceOwnershipRepository teamPieceOwnershipRepository) {
        this.achievementRepository = achievementRepository;
        this.collectedRepository = collectedRepository;
        this.membershipRepository = membershipRepository;
        this.attributionRepository = attributionRepository;
        this.decorativePieceRepository = decorativePieceRepository;
        this.teamPieceOwnershipRepository = teamPieceOwnershipRepository;
    }

    @Transactional
    public void evaluateRideForAchievements(Ride ride) {
        List<Achievement> activeAchievements = achievementRepository.findAllActive(ride.getStartDate());
        Membership userTeam = membershipRepository.findFirstByRiderId(ride.getRider().getId())
                .orElseThrow(() -> new IncompleteAccountException("Rider isn't a member of any team"));

        // Decode polyline once per ride (if present) for Location Checks
        List<double[]> routeCoordinates = new ArrayList<>();
        double[] boundingBox = null; // [minLat, maxLat, minLng, maxLng]
        if (ride.getSummaryPolyline() != null && !ride.getSummaryPolyline().isEmpty()) {
            routeCoordinates = decodePolyline(ride.getSummaryPolyline());
            boundingBox = calculateBoundingBox(routeCoordinates);
        }

        for (Achievement achievement : activeAchievements) {
            if (achievement instanceof AchievementSingleRide single) {
                checkSingleRide(ride, userTeam, single);
            } else if (achievement instanceof AchievementLocation location && boundingBox != null) {
                checkLocation(ride, userTeam, location, routeCoordinates, boundingBox);
            } else if (achievement instanceof AchievementGroupPeriod group) {
                checkGroupPeriod(ride, userTeam, group);
            }
        }
    }

    private void checkSingleRide(Ride ride, Membership membership, AchievementSingleRide ach) {
        double achievedValue = ach.getParameter() == ParamEnum.DISTANCE ? ride.getDistance() : ride.getVertical();
        if (achievedValue >= ach.getAmount()) {
            saveCollected(ride, membership.getTeam(), ach);
        }
    }

    private void checkGroupPeriod(Ride ride, Membership membership, AchievementGroupPeriod ach) {
        LocalDateTime windowEnd = ride.getStartDate();
        LocalDateTime windowStart = windowEnd.minusSeconds(ach.getDurationSeconds());

        Team team = membership.getTeam();
        if (!collectedRepository.existsByAchievementIdAndTeamId(ach.getId(), team.getId())) {
            // This queries ALL team members' rides within the window
            RiderStatsDto windowStats = attributionRepository.getTeamStatsInTimeWindow(team.getId(), windowStart, windowEnd);

            double achievedValue = ach.getParameter() == ParamEnum.DISTANCE ? windowStats.getTotalDistance() : windowStats.getTotalVertical();

            if (achievedValue >= ach.getAmount()) {
                saveCollected(ride, team, ach);
            }
        }
    }

    private void checkLocation(Ride ride, Membership membership, AchievementLocation ach, List<double[]> route, double[] bbox) {
        // 1. Bounding Box Pre-Filter (Expand bbox by rangeKm approximately)
        // 1 degree latitude is approx 111km. Expand box for safety.
        double expansionBuffer = ach.getRangeKm() / 111.0;
        if (ach.getLatitude() < bbox[0] - expansionBuffer || ach.getLatitude() > bbox[1] + expansionBuffer ||
                ach.getLongitude() < bbox[2] - expansionBuffer || ach.getLongitude() > bbox[3] + expansionBuffer) {
            return; // Completely outside the ride's area
        }

        // 2. Exact Haversine check
        for (double[] point : route) {
            if (haversineDistance(point[0], point[1], ach.getLatitude(), ach.getLongitude()) <= ach.getRangeKm()) {
                saveCollected(ride, membership.getTeam(), ach);
                return; // Grant once and exit
            }
        }
    }


    private void saveCollected(Ride ride, Team team, Achievement ach) {
        Collected collected = new Collected();
        collected.setRide(ride);
        collected.setTeam(team);
        collected.setAchievement(ach);
        collectedRepository.save(collected);

        decorativePieceRepository.findByAchievementId(ach.getId()).ifPresent(piece -> {
            TeamPieceOwnership ownership = new TeamPieceOwnership();
            ownership.setPiece(piece);
            ownership.setTree(team.getTree());
            // posX and posY remain null automatically
            teamPieceOwnershipRepository.save(ownership);
        });
    }

    // --- Math & Geo Utils ---

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private double[] calculateBoundingBox(List<double[]> coords) {
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE, maxLng = -Double.MAX_VALUE;
        for (double[] point : coords) {
            if (point[0] < minLat) minLat = point[0];
            if (point[0] > maxLat) maxLat = point[0];
            if (point[1] < minLng) minLng = point[1];
            if (point[1] > maxLng) maxLng = point[1];
        }
        return new double[]{minLat, maxLat, minLng, maxLng};
    }

    // Standard Google Encoded Polyline algorithm decoder
    private List<double[]> decodePolyline(String encoded) {
        List<double[]> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new double[]{(lat / 1E5), (lng / 1E5)});
        }
        return poly;
    }
}