package dao;

import order_system_util.DBUtil;
import entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class UserDao {
    public static User selectByName(String name){
        User user=null;
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "select * from user where name = ?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setString(1,name);
            resultSet=statement.executeQuery();
            if(resultSet.next()){
                user=new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return user;
    }
    public static User selectById(int id){
        User user=null;
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "select * from user where userId = ?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,id);
            resultSet=statement.executeQuery();
            if(resultSet.next()){
                user=new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return user;
    }
    public static void delete(int id){
        Connection connection=DBUtil.getConnection();
        PreparedStatement statement = null;
        String sql="delete from user where userId = ?";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setInt(1,id);
            int ret = statement.executeUpdate();
            if(ret!=0){
                System.out.println("删除用户成功");
                return;
            }
            System.out.println("删除用户失败");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
    public static void add(User user){
        Connection connection= DBUtil.getConnection();
        PreparedStatement statement=null;
        String sql="insert into user values (null,?,?,?)";
        try {
            assert connection != null;
            statement= connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setInt(3,user.getIsAdmin());
            int ret = statement.executeUpdate();
            if(ret!=0){
                System.out.println("添加新用户成功");
                return;
            }
            System.out.println("添加新用户失败");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }
}
