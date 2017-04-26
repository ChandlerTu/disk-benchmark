package com.chandlertu.disk.benchmark;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class DiskBenchmarkWriter {

	public static void main(String[] args) {
		int numberOfTestRuns = Integer.parseInt(args[0]);
		long numberOfTestSize = Integer.parseInt(args[1]);
		String unitOfTestSize = args[2];
		int numberOfBlockSize = Integer.parseInt(args[3]);
		String unitOfBlockSize = args[4];

		long testSize = numberOfTestSize * getUnitValue(unitOfTestSize);
		int blockSize = numberOfBlockSize * getUnitValue(unitOfBlockSize);
		byte[] b = new byte[blockSize];
		long blockCount = testSize / blockSize;

		System.out.println("Number of Test Runs: " + numberOfTestRuns);
		System.out.println("Test Size: " + testSize);
		System.out.println("Block Size: " + blockSize);
		System.out.println("Block Count: " + blockCount);

		Path data = Paths.get("data");
		try {
			Files.createDirectories(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (Stream<Path> paths = Files.list(data)) {
			paths.forEach(path -> {
				try {
					Files.deleteIfExists(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < numberOfTestRuns; i++) {
			Instant start = Instant.now();

			try (OutputStream out = new FileOutputStream(Paths.get("data", "DiskBenchmark" + i).toFile())) {
				for (int j = 0; j < blockCount; j++) {
					out.write(b);
				}
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Instant end = Instant.now();
			double s = Duration.between(start, end).toMillis() / 1000.0;
			System.out.println(s + " s");

			double sizeMB = blockSize * blockCount / (1000 * 1000.0);
			DecimalFormat df = new DecimalFormat("0.0000");
			System.out.println(df.format(sizeMB / s) + " MB/s");
		}

	}

	public static int getUnitValue(String unit) {
		int unitValue;

		switch (unit) {
		case "B":
		default:
			unitValue = 1;
			break;
		case "KB":
			unitValue = 1000;
			break;
		case "MB":
			unitValue = 1000 * 1000;
			break;
		case "GB":
			unitValue = 1000 * 1000 * 1000;
			break;
		case "KiB":
			unitValue = 1024;
			break;
		case "MiB":
			unitValue = 1024 * 1024;
			break;
		case "GiB":
			unitValue = 1024 * 1024 * 1024;
			break;
		}

		return unitValue;
	}

}
