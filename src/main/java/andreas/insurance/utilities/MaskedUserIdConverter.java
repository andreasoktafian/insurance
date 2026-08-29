package andreas.insurance.utilities;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskedUserIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {

        String userId = event.getMDCPropertyMap().get("userId");

        if (userId == null || userId.isEmpty()) return "ANONYMOUS";

        if (userId.equals("ANONYMOUS")) return userId;

        int length = userId.length();

        if (length == 6) {
            String end = userId.substring(3);
            return "***" + end;
        }

        if (length > 6) {
            String start = userId.substring(0, 2);
            String end = userId.substring(length - 3);

            int maskLength = length - 5;
            String mask = "*".repeat(maskLength);

            return start + mask + end;
        }

        String start = userId.substring(0, 1);
        String end = userId.substring(length - 1);
        return start + "***" + end;

    }

}
