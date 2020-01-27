package org.jfl110.mylocation;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfl110.app.Logger;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Handler to list all saved locations as JSON.
 * 
 * @author jim
 *
 */
class ShowAllLocationsJsonHandler implements GatewayRequestHandler<String> {

	private final LogLocationDAO logLocationDAO;
	private final ManualLocationsDAO manualLocationsDAO;
	private final Logger logger;
	
	@Inject
	ShowAllLocationsJsonHandler(LogLocationDAO logLocationDAO, ManualLocationsDAO manualLocationsDAO, Logger logger) {
		this.logLocationDAO = logLocationDAO;
		this.manualLocationsDAO = manualLocationsDAO;
		this.logger = logger;
	}


	@Override
	public GatewayResponse handleRequest(String input, GatewayEventInformation eventInfo, Context context) throws IOException {
		logger.log("Displaying all saved log locations");
		ImmutableList<LogLocationItem> locations = logLocationDAO.listAll().collect(new DeDuplicator());
		List<ManualLocationItem> manualLocations = manualLocationsDAO.listAll().collect(Collectors.toList());
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(new ExposedShowLogLocationsOutput(
				FluentIterable.from(locations).transform(this::map).filter(Optional::isPresent).transform(Optional::get)
				.append(
				FluentIterable.from(manualLocations).transform(this::map).filter(Optional::isPresent).transform(Optional::get))
			.toList())).build();
	}
	
	
	private Optional<ExposedShowLogLocationsOutput.Location> map(ManualLocationItem item) {
		ZonedDateTime itemTime;
		try {
			itemTime = ZonedDateTime.parse(item.getTime(), ManualLocationItem.DATE_FORMAT);
		} catch (Exception e) {
			// Log & Squash
			logger.log("Exception decoding date for item [" + item.getId() + "]" + e.getMessage());
			return Optional.empty();
		}
		return Optional.of(new ExposedShowLogLocationsOutput.Location(
				item.getId(), item.getLatitude(), item.getLongitude(), null,
				item.getAccuracy(), itemTime.toInstant().toEpochMilli(), item.getTitle(), LocationType.MANUAL.getCode()));
	}


	private Optional<ExposedShowLogLocationsOutput.Location> map(LogLocationItem item) {
		ZonedDateTime itemTime;
		try {
			itemTime = ZonedDateTime.parse(item.getTime(), LogLocationItem.DATE_FORMAT);
		} catch (Exception e) {
			// Log & Squash
			logger.log("Exception decoding date for item [" + item.getId() + "]" + e.getMessage());
			return Optional.empty();
		}
		return Optional.of(new ExposedShowLogLocationsOutput.Location(
				item.getId(), item.getLatitude(), item.getLongitude(), item.getAltitude(),
				item.getAccuracy(), itemTime.toInstant().toEpochMilli(), null,  LocationType.AUTO.getCode()));
	}


	@Override
	public Class<String> inputClazz() {
		return String.class;
	}

	static class DeDuplicator implements Collector<LogLocationItem, Map<String, LogLocationItem>, ImmutableList<LogLocationItem>> {

		private static final String SEP = "-";

		@Override

		public Supplier<Map<String, LogLocationItem>> supplier() {
			return () -> Maps.<String, LogLocationItem>newConcurrentMap();
		}


		@Override
		public Function<Map<String, LogLocationItem>, ImmutableList<LogLocationItem>> finisher() {
			return m -> FluentIterable.from(m.entrySet()).transform(e -> e.getValue()).toList();
		}


		@Override
		public BiConsumer<Map<String, LogLocationItem>, LogLocationItem> accumulator() {
			return (m, l) -> {
				String key = l.getDeviceName() + SEP + l.getLatitude() + SEP + l.getLongitude() + SEP + l.getTime();
				LogLocationItem existingItem = m.get(key);
				// Put if no existing with key or if accuracy of new item is lower
				if (existingItem == null || existingItem.getAccuracy() > l.getAccuracy()) {
					m.put(key, l);
				}
			};

		}


		@Override
		public BinaryOperator<Map<String, LogLocationItem>> combiner() {
			return (m1, m2) -> {
				Map<String, LogLocationItem> m3 = Maps.newConcurrentMap();
				m3.putAll(m1);
				m3.putAll(m2);
				return m3;
			};
		}


		@Override
		public Set<Characteristics> characteristics() {
			return Sets.immutableEnumSet(Characteristics.UNORDERED);
		}
	}

}