package dao;

import order_system_util.DBUtil;
import entity.Dish;
import order_system_util.OrderSystemException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DishDao {
    public static Dish selectById(int id) throws OrderSystemException {
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement =null;
        ResultSet resultSet =null;
        String sql="select * from dishes where dishId=?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,id);
            resultSet=statement.executeQuery();
            if(resultSet.next()){
                Dish dish=new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                return dish;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException(e.getMessage());
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
    public static List<Dish> selectAll() throws OrderSystemException {
        List<Dish> dishes=new ArrayList<>();
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet=null;
        String sql="select * from dishes";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            resultSet=statement.executeQuery();
            while(resultSet.next()){
                Dish dish=new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException(e.getMessage());
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return dishes;
    }
    public static void delete(int id) throws OrderSystemException {
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement=null;
        String sql="delete from dishes where dishId=?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,id);
            int ret= statement.executeUpdate();
            if(ret==0){
                throw new OrderSystemException("删除菜品失败");
            }
            System.out.println("删除成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException(e.getMessage());
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
    public static void add(Dish dish) throws OrderSystemException {
        Connection connection = DBUtil.getConnection();
        PreparedStatement statement =null;
        String sql= "insert into dishes values (null,?,?)";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setString(1, dish.getName());
            statement.setInt(2,dish.getPrice());
            int ret = statement.executeUpdate();
            if(ret==0){
                throw new OrderSystemException("添加菜品失败");
            }
            System.out.println("添加菜品成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new OrderSystemException(e.getMessage());
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
}
