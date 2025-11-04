package model;

import dto.User_DTO;
import entity.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebFilter(urlPatterns = {"/product-registation.html"})
public class FilterSellerCheck implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Session session = HibernateUtil.getSessionFactory().openSession();

//        if (httpServletRequest.getSession().getAttribute("user") ==null) {
//            httpServletResponse.sendRedirect("signIn.html");
//        }else if (user.getUser_type().getId() == 2) {
//            chain.doFilter(request, response);
//        } else {
//            httpServletResponse.sendRedirect("index.html");
//        }
        if (httpServletRequest.getSession().getAttribute("user") != null) {

            User_DTO user_DTO = (User_DTO) httpServletRequest.getSession().getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();
            if (user.getUser_type().getId() == 2) {
                chain.doFilter(request, response);
            } else {
                httpServletResponse.sendRedirect("index.html");
            }

//            chain.doFilter(request, response);
        } else {
            httpServletResponse.sendRedirect("signIn.html");
        }

    }

    @Override
    public void destroy() {
    }

}
