package com.hconex.ui;

import com.hconex.core.packets.Packet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConsoleUI {

	private static final String RESET = "\u001B[0m";
	private static final String RED = "\u001B[31m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String BLUE = "\u001B[34m";
	private static final String CYAN = "\u001B[36m";

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

	public void printHeader(String title) {
		System.out.println(BLUE + "==============================================" + RESET);
		System.out.println(BLUE + " " + title + RESET);
		System.out.println(BLUE + "==============================================" + RESET);
	}

	public void printConfig(String key, String value) {
		System.out.println(CYAN + "[CONFIG] " + RESET + key + " = " + value);
	}

	public void printRunning(String message) {
		System.out.println(YELLOW + "[RUNNING] " + RESET + message);
	}

	public void printPacketTableHeader() {
		String header = String.format("%-10s %-10s %-10s %-8s %-12s", "TIME", "DIRECTION", "PACKET ID", "SIZE", "TYPE");
		System.out.println(BLUE + header + RESET);
		System.out.println(BLUE + "--------------------------------------------------------------" + RESET);
	}

	public void printStatistics(int incoming, int outgoing, int totalPackets) {
		System.out.println(CYAN + "[STATISTICS]" + RESET);
		System.out.println(" Incoming: " + incoming);
		System.out.println(" Outgoing: " + outgoing);
		System.out.println(" Total: " + totalPackets);
	}

	public void printStats(int incoming, int outgoing, int totalPackets) {
		printStatistics(incoming, outgoing, totalPackets);
	}

	public void printError(String message) {
		System.out.println(RED + "[ERROR] " + message + RESET);
	}

	public void printInfo(String message) {
		System.out.println(CYAN + "[INFO] " + RESET + message);
	}

	public void printSuccess(String message) {
		System.out.println(GREEN + "[SUCCESS] " + RESET + message);
	}

	public void printPacket(Packet packet) {
		if (packet == null) {
			printError("Packet is null");
			return;
		}

		Instant instant = packet.getTimestamp() != null ? packet.getTimestamp() : Instant.now();
		LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		String time = timestamp.format(TIME_FORMAT);
		String direction = packet.getDirection() != null ? packet.getDirection().name() : "UNKNOWN";
		int packetId = packet.getId();
		int size = packet.getData() != null ? packet.getData().length : 0;
		String type = direction.equals(Packet.Direction.INCOMING.name()) ? "RECV" : "SEND";

		String row = String.format("%-10s %-10s %-10d %-8d %-12s", time, direction, packetId, size, type);
		System.out.println(row);
	}

	public void clearScreen() {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (Exception e) {
			System.out.println();
		}
	}
}
