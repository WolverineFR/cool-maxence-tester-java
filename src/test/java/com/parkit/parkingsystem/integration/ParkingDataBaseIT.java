package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	public static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	public void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	public static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		assertNotNull(ticketDAO.getTicket("ABCDEF"));
		assertEquals("ABCDEF",
				ticketDAO.getTicket("ABCDEF").getVehicleRegNumber());
		assertNotNull(ticketDAO.getTicket("ABCDEF").getParkingSpot());
		assertFalse(ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable());

	}

	@Test
	public void testParkingLotExit() throws InterruptedException {
		testParkingACar();

		Thread.sleep(500); // add time to make exit time greater than arrival time

		ParkingService parkingService = new ParkingService(inputReaderUtil,
				parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime());
		assertNotNull(ticketDAO.getTicket("ABCDEF").getPrice());
	}

	@Test
	public void testParkingLotExitRecurringUser() {
		// testParkingAcar();

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();

		parkingService.processExitingVehicle();

		assertNotNull(ticketDAO.getTicket("ABCDEF").getOutTime());
		assertEquals(ticketDAO.getTicket("ABCDEF").getPrice(), (Fare.CAR_RATE_PER_HOUR) * 0.95, 0.01);

	}

}
