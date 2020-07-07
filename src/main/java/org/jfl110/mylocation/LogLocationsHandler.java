package org.jfl110.mylocation;

import static org.jfl110.mylocation.MyLocationAppConfig.BAD_SECURITY_KEY;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.inject.Inject;

import org.jfl110.app.ZonedNowSupplier;
import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.util.LambdaUtils;
import org.jfl110.util.StringUtils;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Handler that receives and saves location logs.
 * 
 * @author jim
 *
 */
class LogLocationsHandler implements GatewayRequestHandler<ExposedLogLocationsInput> {

	private final LocationsDao locationsDao;
	private final SecurityKeyProvider securityKeyProvider;
	private final ZonedNowSupplier nowSupplier;

	@Inject
	LogLocationsHandler(LocationsDao locationsDao, SecurityKeyProvider securityKeyProvider, ZonedNowSupplier nowSupplier) {
		this.locationsDao = locationsDao;
		this.nowSupplier = nowSupplier;
		this.securityKeyProvider = securityKeyProvider;
	}


	@Override
	public GatewayResponse handleRequest(ExposedLogLocationsInput input, GatewayEventInformation eventInfo, Context context) throws IOException {
		// Validate input
		if (input == null)
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NO_INPUT_MESSAGE);

		if (StringUtils.isBlank(input.getTenantId()))
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "tennant-id");

		if (StringUtils.isBlank(input.getSecurityKey()))
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "security-key");

		if (!securityKeyProvider.getSecurityKey(input.getTenantId()).orElse("").equals(input.getSecurityKey()))
			throw new BadInputGatewayResponseException(BAD_SECURITY_KEY);

		if (StringUtils.isBlank(input.getDeviceName()))
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "device-name");

		// Save locations
		context.getLogger().log("Saving locations [ " + input + "]");
		locationsDao.save(input.getTenantId(),
				LambdaUtils.mapList(input.getLocations(), l -> mapToItemAndCheck(input.getDeviceName(), l)));

		// Return location ids in response
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(
				new ExposedLogLocationsOutput(LambdaUtils.mapList(input.getLocations(), ExposedLogLocationsInput.Location::getId)))
				.build();
	}


	private AutoLogLocation mapToItemAndCheck(String deviceName, ExposedLogLocationsInput.Location location) {
		if (StringUtils.isBlank(location.getId())) {
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "id");
		}
		if (StringUtils.isBlank(location.getRecordedTimeAsString())) {
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "time");
		}

		return new AutoLogLocation(
				location.getId(),
				location.getLatitude(),
				location.getLongitude(),
				location.getAltitude(),
				location.getAccuracy(),
				deviceName,
				ZonedDateTime.parse(location.getRecordedTimeAsString(), ExposedLogLocationsInput.DATE_FORMAT),
				nowSupplier.get(),
				Optional.empty());
	}
}