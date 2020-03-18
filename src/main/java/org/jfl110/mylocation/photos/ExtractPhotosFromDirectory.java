//package org.jfl110.mylocation.photos;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.StreamSupport;
//
//import org.jfl110.util.ExceptionUtils;
//
//import com.drew.imaging.ImageMetadataReader;
//import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
//
//public class ExtractPhotosFromDirectory {
//
//	private static final DateTimeFormatter EXIF_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
//
//	public List<ExtractedPhotoDetails> extract(String dir) {
//		return ExceptionUtils.doRethrowing(() -> Files.walk(Paths.get(dir)).filter(f -> f.toFile().isFile()).map(f -> {
//			System.out.println(f);
//			Metadata metadata = ExceptionUtils.doRethrowing(() -> ImageMetadataReader.readMetadata(f.toFile()));
//			Optional<Double> latRef = getTag(metadata, "GPS Latitude Ref").map(this::refFactor);
//			Optional<Double> longRef = getTag(metadata, "GPS Longitude Ref").map(this::refFactor);
//			if (!latRef.isPresent() || !longRef.isPresent()) {
//				return null;
//			}
//
//			Optional<Double> lat = getTag(metadata, "GPS Latitude").flatMap(this::fromMinutes).map(d -> d * latRef.get());
//			Optional<Double> lng = getTag(metadata, "GPS Longitude").flatMap(this::fromMinutes).map(d -> d * longRef.get());
//			Optional<LocalDateTime> time = getTag(metadata, "Date/Time").map(s -> LocalDateTime.parse(s, EXIF_DATE_TIME_FORMAT));
//
//			if (!lat.isPresent() || !lng.isPresent() || !time.isPresent()) {
//				return null;
//			}
//			System.out.println(lat.get() + ", " + lng.get() + "-" + time.get());
//
//			return new ExtractedPhotoDetails(f, lat.get(), lng.get(), time.get());
//		})).filter(o -> o != null).collect(Collectors.toList());
//	}
//
//}
