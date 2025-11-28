package itmo.soa.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class CorsPreFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -100; // Выполняется раньше других фильтров
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        
        // Устанавливаем CORS заголовки для всех запросов
        // Используем "*" для максимальной совместимости
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, X-CSRF-TOKEN");
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", "*");
        
        // Обработка OPTIONS preflight запросов
        if ("OPTIONS".equals(ctx.getRequest().getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            ctx.setSendZuulResponse(false);
            return null;
        }
        
        return null;
    }
}
