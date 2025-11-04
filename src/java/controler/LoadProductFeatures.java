package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Category;
import entity.Color;
import entity.Main_Category;
import entity.Size;
import entity.Sub_Category;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@WebServlet(name = "LoadProductFeatures", urlPatterns = {"/LoadProductFeatures"})
public class LoadProductFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Criteria crteria1 = session.createCriteria(Brand.class);
        crteria1.addOrder(Order.asc("name"));
        List<Brand> brandList = crteria1.list();
        jsonObject.add("brandList", gson.toJsonTree(brandList));
        
        Criteria crteria2 = session.createCriteria(Main_Category.class);
        crteria2.addOrder(Order.asc("name"));
        List<Main_Category> main_CategoryList = crteria2.list();
        jsonObject.add("main_CategoryList", gson.toJsonTree(main_CategoryList));
        
        Criteria crteria3 = session.createCriteria(Sub_Category.class);
        crteria3.addOrder(Order.asc("name"));
        List<Sub_Category> sub_CategoryList = crteria3.list();
        jsonObject.add("sub_CategoryList", gson.toJsonTree(sub_CategoryList));
        
        Criteria crteria4 = session.createCriteria(Category.class);
        crteria4.addOrder(Order.asc("id"));
        List<Category> categoryList = crteria4.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        
        Criteria crteria5 = session.createCriteria(Color.class);
        crteria5.addOrder(Order.asc("name"));
        List<Color> colorList = crteria5.list();
        jsonObject.add("colorList", gson.toJsonTree(colorList));
        
        Criteria crteria6 = session.createCriteria(Size.class);
        crteria6.addOrder(Order.desc("name"));
        List<Size> sizeList = crteria6.list();
        jsonObject.add("sizeList", gson.toJsonTree(sizeList));

        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
    }
}
