package org.jfl110.mylocation;

import java.io.IOException;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.jfl110.app.ZonedNowSupplier;
import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.util.StringUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * Handler that receives and saves location logs.
 * 
 * @author jim
 *
 */
class LogLocationsHandler implements GatewayRequestHandler<ExposedLogLocationsInput> {

	static final String BAD_SECURITY_KEY = "bad-security-key";
	
	private final LogLocationDAO logLocationDAO;
	private final SecurityKeyProvider securityKeyProvider;
	private final ZonedNowSupplier nowSupplier;

	@Inject
	LogLocationsHandler(LogLocationDAO logLocationDAO, SecurityKeyProvider securityKeyProvider, ZonedNowSupplier nowSupplier) {
		this.logLocationDAO = logLocationDAO;
		this.nowSupplier = nowSupplier;
		this.securityKeyProvider = securityKeyProvider;
	}


	@Override
	public GatewayResponse handleRequest(ExposedLogLocationsInput input, GatewayEventInformation eventInfo, Context context) throws IOException {
		if (input == null)
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NO_INPUT_MESSAGE);
		
		if(!Strings.nullToEmpty(securityKeyProvider.getSecurityKey()).equals(Strings.nullToEmpty(input.getSecurityKey())))
			throw new BadInputGatewayResponseException(BAD_SECURITY_KEY);
		
		
		if(StringUtils.isBlank(input.getDeviceName()))
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "device-name");

		context.getLogger().log("Saving locations [ " + input + "]");
		logLocationDAO.save(FluentIterable.from(input.getLocations()).transform(l -> mapToItemAndCheck(input.getDeviceName(), l)).toList());
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(
				new ExposedLogLocationsOutput(FluentIterable.from(input.getLocations()).transform(ExposedLogLocationsInput.Location::getId).toList()))
				.build();
	}


	private LogLocationItem mapToItemAndCheck(String deviceName, ExposedLogLocationsInput.Location location) {
		if(StringUtils.isBlank(location.getId())) {
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "id");
		}
		if(StringUtils.isBlank(location.getRecordedTimeAsString())) {
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NULL_IN_INPUT, "time");
		}
		
		LogLocationItem item = new LogLocationItem();
		item.setId(location.getId());
		item.setLatitude(location.getLatitude());
		item.setLongitude(location.getLongitude());
		item.setAltitude(location.getAltitude());
		item.setAccuracy(location.getAccuracy());
		item.setDeviceName(deviceName);
		item.setTime(LogLocationItem.DATE_FORMAT.format(ZonedDateTime.parse(location.getRecordedTimeAsString(), ExposedLogLocationsInput.DATE_FORMAT)));
		item.setSavedTime(LogLocationItem.DATE_FORMAT.format(nowSupplier.get()));
		return item;
	}


	@Override
	public Class<ExposedLogLocationsInput> inputClazz() {
		return ExposedLogLocationsInput.class;
	}
}