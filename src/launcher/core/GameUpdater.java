package launcher.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GameUpdater {
	/*
	 * GameUpdater.Java
	 * Checks if the games version is out dated / downloads new updates.
	 * Author: GabrielBailey74
	 */
	private static byte[] BYTE_BUFFER = new byte[1024];
	private String currentClientVersion;
	private String versionLocation;
	private String versionFileNameAndExt;
	private String gameName;
	private String mostRecentVersion;
	private boolean needsUpdating;
	private int downloadPercentage;
	private int kbs;
	private double ETA;
	private LodeScapeLauncher launcher;

	public GameUpdater(LodeScapeLauncher launcher, String currentClientVersion,
			String versionLocation, String versionFileNameAndExtension, String gameName)
			throws MalformedURLException, IOException {
		this.setLauncher(launcher);
		setCurrentClientVersion(currentClientVersion);
		setVersionLocation(versionLocation);
		setVersionFileNameAndExt(versionFileNameAndExtension);
		setGameName(gameName);
		setMostRecentVersion(getVersionFromUrl(versionLocation + versionFileNameAndExtension));
		System.out.println("Current client version: " + currentClientVersion);
		System.out.println("Actual client version: " + getMostRecentVersion());
		setNeedsUpdating(!currentClientVersion.equals(getMostRecentVersion()));
		System.out.println("Client needs updating: " + needsUpdating());

	}

	private File updateJar(String jarLocation, boolean printPercentage)
			throws MalformedURLException, IOException {
		getLauncher().getRenderer().statusLabel
				.setText("Please be patient while updates are being downloaded.. ("
						+ getDownloadPercentage() + "% complete, ETA: " + (int) getETA() + " secs)");
		return getJarFromURL(jarLocation, getGameName() + getMostRecentVersion() + ".jar",
				printPercentage);
	}

	File downloadUpdatedJar(boolean printPercentage) throws MalformedURLException, IOException {
		return updateJar(getVersionLocation() + getGameName() + getMostRecentVersion() + ".jar",
				printPercentage);
	}

	public static String getVersionFromUrl(String versionFileURL) throws MalformedURLException,
			IOException {
		/*
		 * Initialize the Input Stream. (Downloading from remote system / server)
		 */
		BufferedInputStream in = new BufferedInputStream(new URL(versionFileURL).openStream());

		/*
		 * Initialize the Output Stream. (Downloading on local system / pc)
		 */
		File savedFile = new File("version.txt.tmp");
		savedFile.deleteOnExit();
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(savedFile));

		/*
		 * Begin the download.
		 */
		int inCount;
		while ((inCount = in.read(BYTE_BUFFER, 0, BYTE_BUFFER.length)) != -1) {
			out.write(BYTE_BUFFER, 0, inCount);
		}

		/*
		 * Close the Input/Output streams.
		 */
		out.flush();
		out.close();
		in.close();

		// after the version data has been saved, read it and delete the .TMP file.
		String version = "";
		if (savedFile.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(savedFile));
			version = br.readLine();
			br.close();
		}
		return version;
	}

	private File getJarFromURL(String urlLocation, String saveLocation, boolean printPercentage)
			throws MalformedURLException, IOException {
		double numWritten = 0;
		double length = getURLSizeInKB(urlLocation);

		/*
		 * Initialize the Input Stream. (Downloading from remote system / server)
		 */
		BufferedInputStream in = new BufferedInputStream(new URL(urlLocation).openStream());

		/*
		 * Initialize the Output Stream. (Downloading on local system / pc)
		 */
		File savedFile = new File(saveLocation);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(savedFile));

		/*
		 * Keeping track of when we started.
		 */
		long startTime = System.currentTimeMillis();

		/*
		 * Begin the download.
		 */
		int inCount;
		while ((inCount = in.read(BYTE_BUFFER, 0, BYTE_BUFFER.length)) != -1) {
			out.write(BYTE_BUFFER, 0, inCount);
			numWritten += inCount;

			/*
			 * Calculate the Percentage.
			 */
			setDownloadPercentage((int) (((double) numWritten / (double) length) * 100D));
			if (printPercentage) {
				System.out.println("Download is " + getDownloadPercentage() + "% complete, "
						+ (int) getKbs() + " seconds left");
			}
			getLauncher().getRenderer().statusLabel
					.setText("Please be patient while updates are being downloaded.. ("
							+ getDownloadPercentage() + "% complete, ETA: " + (int) getETA()
							+ " secs)");
			getLauncher().getRenderer().bar.setValue(getDownloadPercentage());

			/*
			 * Calculate the KBS.
			 */
			setKbs((int) ((numWritten / BYTE_BUFFER.length) / (1 + ((System.currentTimeMillis() - startTime) / 1000))));

			/*
			 * Calculate the ETA.
			 */
			setETA((length - numWritten) / kbs / 1000D);
		}

		/*
		 * Close the Input/Output streams.
		 */
		out.flush();
		out.close();
		in.close();
		return savedFile;
	}

	/* Obtaining the files size in kilobytes */
	private Double getURLSizeInKB(String urlStr) {
		double contentLength = 0;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			contentLength = httpConn.getContentLength();
			httpConn.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return contentLength;
	}

	boolean needsUpdating() {
		return needsUpdating;
	}

	public String getCurrentClientVersion() {
		return currentClientVersion;
	}

	public void setCurrentClientVersion(String currentClientVersion) {
		this.currentClientVersion = currentClientVersion;
	}

	public String getVersionLocation() {
		return versionLocation;
	}

	public void setVersionLocation(String versionLocation) {
		this.versionLocation = versionLocation;
	}

	public String getVersionFileNameAndExt() {
		return versionFileNameAndExt;
	}

	public void setVersionFileNameAndExt(String versionFileNameAndExt) {
		this.versionFileNameAndExt = versionFileNameAndExt;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public void setNeedsUpdating(boolean needsUpdating) {
		this.needsUpdating = needsUpdating;
	}

	public String getMostRecentVersion() {
		return mostRecentVersion;
	}

	public void setMostRecentVersion(String mostRecentVersion) {
		this.mostRecentVersion = mostRecentVersion;
	}

	public int getDownloadPercentage() {
		return downloadPercentage;
	}

	public void setDownloadPercentage(int downloadPercentage) {
		this.downloadPercentage = downloadPercentage;
	}

	public int getKbs() {
		return kbs;
	}

	public void setKbs(int kbs) {
		this.kbs = kbs;
	}

	public double getETA() {
		return ETA;
	}

	public void setETA(double eTA) {
		ETA = eTA;
	}

	/**
	 * @return the launcher
	 */
	public LodeScapeLauncher getLauncher() {
		return launcher;
	}

	/**
	 * @param launcher the launcher to set
	 */
	public void setLauncher(LodeScapeLauncher launcher) {
		this.launcher = launcher;
	}

}