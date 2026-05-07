package rb.aczg.servlet

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletResponse

class CorsFilter implements Filter {

    @Override
    void init(FilterConfig config) {}

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = response as HttpServletResponse
        res.setHeader('Access-Control-Allow-Origin',  '*')
        res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        chain.doFilter(request, response)
    }

    @Override
    void destroy() {}
}