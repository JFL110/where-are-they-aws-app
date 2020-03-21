package org.jfl110.mylocation.photos;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.jfl110.util.ExceptionUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class ExtractGeoExifData {

	private static final DateTimeFormatter EXIF_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

	public Optional<ExtractedPhotoDetails> extract(InputStream is) {
		Metadata metadata = ExceptionUtils.doRethrowing(() -> ImageMetadataReader.readMetadata(is));

		Optional<Double> lat = getTag(metadata, "GPS Latitude").flatMap(this::fromMinutes);
		Optional<Double> lng = getTag(metadata, "GPS Longitude").flatMap(this::fromMinutes);
		Optional<LocalDateTime> time = getTag(metadata, "Date/Time").map(s -> LocalDateTime.parse(s, EXIF_DATE_TIME_FORMAT));

		if (!lat.isPresent() || !lng.isPresent() || !time.isPresent()) {
			return Optional.empty();
		}

		System.out.println("Converted at time[" + time.get() + "] - [" + getTag(metadata, "GPS Latitude") + "," + getTag(metadata, "GPS Longitude")
				+ "] to [" + lat.get() + "," + lng.get() + "]");
		
		return Optional.of(new ExtractedPhotoDetails(lat.get(), lng.get(), time.get()));
	}

	public static class ExtractedPhotoDetails {

		private final double latitude;
		private final double longitude;
		private final LocalDateTime time;

		ExtractedPhotoDetails(double latitude, double longitude, LocalDateTime time) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.time = time;
		}


		public double getLatitude() {
			return latitude;
		}


		public double getLongitude() {
			return longitude;
		}


		public LocalDateTime getTime() {
			return time;
		}


		@Override
		public String toString() {
			return "ExtractedPhotoDetails [latitude=" + latitude + ", longitude=" + longitude + ", time=" + time + "]";
		}
	}

	private Optional<String> getTag(Metadata metaData, String tagName) {
		return StreamSupport.stream(metaData.getDirectories().spliterator(), false).flatMap(d -> d.getTags().stream())
				.filter(t -> tagName.equals(t.getTagName())).map(Tag::getDescription).findAny();
	}


	private Optional<Double> fromMinutes(String str) {
		String[] parts = str.replace("'", "").replace("°", "").replace("\"", "").split(" ");
		if (parts.length != 3) {
			return Optional.empty();
		}
		double firstPart = Double.valueOf(parts[0]);
		return Optional.of(firstPart + (firstPart < 0 ? -1 : 1) * Double.valueOf(parts[1]) / 60 +  (firstPart < 0 ? -1 : 1) * Double.valueOf(parts[2]) / 3600);
	}


	private Double refFactor(String ref) {
		return "N".equals(ref) || "E".equals(ref) ? 1d : -1d;
	}
}
