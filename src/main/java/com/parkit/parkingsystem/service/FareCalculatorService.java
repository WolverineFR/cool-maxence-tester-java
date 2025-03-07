package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean discount) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		long duration = outHour - inHour;
		double doubleDuration = (double) duration / (60 * 60 * 1000); // convert of duration to double

		if (doubleDuration < 0.5) {
			ticket.setPrice(0.0);
		} else if (discount) {
			double discountPrice = doubleDuration * 0.95;
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(discountPrice * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(discountPrice * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(doubleDuration * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(doubleDuration * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

	public void calculateFare(Ticket ticket) {
		calculateFare(ticket, false);
	}
}