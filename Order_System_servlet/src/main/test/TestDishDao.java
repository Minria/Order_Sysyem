import dao.DishDao;
import entity.Dish;
import order_system_util.OrderSystemException;

import java.util.List;

public class TestDishDao {
    public static void main4(String[] args) throws OrderSystemException {
        DishDao.update(1,0);
    }
    public static void main3(String[] args) throws OrderSystemException {
        Dish d=DishDao.selectById(2);
        System.out.println(d);
    }
    public static void main(String[] args) throws OrderSystemException {
        List<Dish> list=DishDao.selectAll();
        for(Dish d:list){
            System.out.println(d);
        }
    }
    public static void main1(String[] args) {
//        Dish dish=new Dish();

//        dish.setName("鱼香肉丝");
//        dish.setPrice(1800);

//        dish.setName("烤鸡");
//        dish.setPrice(2000);

//        dish.setName("羊肉串");
//        dish.setPrice(300);

//        dish.setName("年糕");
//        dish.setPrice(200);

//        DishDao.add(dish);
    }
}