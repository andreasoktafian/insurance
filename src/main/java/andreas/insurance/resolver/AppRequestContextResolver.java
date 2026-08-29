package andreas.insurance.resolver;

import andreas.insurance.dto.context.AppRequestContext;
import andreas.insurance.exception.custom.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AppRequestContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AppRequestContext.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

        String userId = webRequest.getHeader("X-User-ID");
        String correlationId = (String) webRequest.getAttribute("correlationId", RequestAttributes.SCOPE_REQUEST);

        if (!StringUtils.hasText(userId)) throw new BusinessException("X-User-ID header is required");

        return new AppRequestContext(userId, correlationId);

    }
}
