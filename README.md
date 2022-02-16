版本迭代：



# 1. 项目简介

## 1.1 项目背景

该项目就是为了顾客方便通过手机来进行在线点餐。

## 1.2 模块

1、对用户的管理

2、对菜品的管理

3、对订单的管理

## 1.3 技术栈

MySQL以及JavaJDBC编程

JavaServlet编程

Http网络相关知识

# 2. 需求分析

## 2.1 用户管理

注册、登录、登出

需要区分顾客与商家，因为他们的操作不相同

## 2.2 菜品管理

能够对菜品进行增删查改

对于顾客：只能查看

对于商家：可以进行增删查改

## 2.3 订单管理

对订单进行增删查改

对于顾客：新增订单以及查看自己过去订单

对于商家：查看所有订单，修改订单状态

# 3. 数据库设计

## 3.1 对于用户需要有

id，用户名，密码，还要有一个区分是商家还是顾客

```sql
create table user (
    userId int primary key auto_increment,
    name varchar(50) unique,
    password varchar(50),
    isAdmin int  -- 是否是管理员, 0 表示不是管理员, 1 表示是管理员
);
```

## 3.2 对于菜品需要有

id，名称，价格

```sql
create table dishes (
    dishId int primary key auto_increment,
    name varchar(50),
    price int  -- 以分为单位, 使用 int 表示. 尽量不要用 float double, 会存在误差
);
```

需要注意的价格为多少分，不可用float以及double，由于IEEE754格式，导致浮点相加容易出现错误

## 3.3 对于订单管理

我们需要有两张表，一张关联用户订单，一张关联订单和菜品

对于用户订单表

订单id，用户id，下单时间，是否完成

```sql
create table order_user (
    orderId int primary key auto_increment,
    userId int,    -- 这个用户 id 需要和 user 表中的 userId 具有关联关系
    time datetime, -- 下单时间
    isDone int,    -- 1 表示订单完结, 0 表示订单未完结.
    foreign key(userId) references user(userId)
);
```

对于订单菜品表

订单id，菜品id

```sql
create table order_dish (
    orderId int,   -- orderId 也和 order_user 表的 orderId 字段有外键关系
    dishId int,    -- dishId 也和 dishes 表中的 dishId 存在外键关系
    foreign key(orderId) references order_user(orderId),
    foreign key(dishId) references dishes(dishId)
);
```

# 4. 数据库操作

## 4.1 对用户操作

```java
public static void add(User user);
public static void deleteUser(int id);
public static User seleteById(int id);
```

## 4.2 对菜品的操作

```java
public static void add(Dish dish);
public static void delete(int id);
public static List<Dish> seleteAll();
public static Dish seleteById(int id);
```

## 4.3 对订单的操作

```java
public static void add(Order order);
private static void addOrderUser(Order order);
private static void addOrderDish(Order order);
public static void deleteOrderUser(int orderId);
public static List<Order> selectAll();
public static List<Order> selectByUserId(int userId);
public static Order selectById(int orderId);
private static Order buildOrder(int orderId);
private static List<Integer> selectDishIds(int orderId);
private static void getDishDetail(Order order, List<Integer> dishIds);
public static void changeState(int orderId, int isDone);
```

1、新增订单

在前文中说明，一个订单是由两个表来完成的。分别是`order_user`和`order_dish`

所以新增一个订单就需要往两个数据库中填表

需要注意的，先添加`order_user`,这时得到自增id`orderId`然后用与添加到`order_dish`中

也需要注意的是，如果第二表添加失败，抛出异常，就需要删除`order_user`表中刚才添加的（调用`deleteOrderUser`）

2、查看订单详情

第一步构建订单

`buildOrder`函数通过订单id构造一个订单，这时候我们的订单中就只有菜品没有表示（操作order_user表）

`selecDishIds`来通过订单id来得到菜品线性表(操作order_dish表)

`getDishDetail`来将菜品线性表添加到order中

# 5. 前后端API的约定

前后端采用JSON来构造相应格式

前端发给后端一个请求字符串，通过谷歌gson转化为一个请求类

后端构造一个响应类，通过谷歌gson转化为一个响应字符串

## 5.1 用户操作

用户注册

```
POST/register
{
	name:xxx,
	password:xxx
}
HTTP/1.1 200 OK
{
	ok:1,
	reason:xxx
}
```

用户登录

```
POST/login
{
	name:xxx,
	password:xxx
}
HTTP/1.1 200 OK
{
	ok:1,//1表示成功
	reason:xxx,//失败的时候返回原因
	name:xxx,
	isAdmin:0 //0表示普通用户，1表示管理员
}
```

检查登录状态

```
GET/login
{
}
{
	ok:1,
	reason:xxx,
	name:xxx,
	isAdmin:0
}
```

用户注销

```
GET/logout
{
}
{
	ok:1,
	reason:xxx
}
```

## 5.2 对菜品的管理

 新增菜品

需要验证管理员身份

```
POST/dish
{
	name:xxx,
	price:xxx,
}
{
	ok:1,
	reason:xxx
}
```

删除菜品

需要验证管理员身份

```
DELETE/dish?dishId=xxx
{
}
{
	ok:1,
	reason:xxx
}
```

查看所有菜品

```
GET/dish
{
}
{
	{
		dishId:xxx,
		name:xxx,
		price:xxx
	},{
		dishId:xxx,
		name:xxx,
		price:xxx	
	}
}
```

## 5.3 订单管理

新增订单

```
POST/order
{
	[1,2,3,4]
}
{
	ok:1,
	reason:xxx
}
```

查看订单

根据身份来判断操作

```
GET/order
{
}
{
	{
		orderId:xxx,
		userId:xxx,
		time:xxx,
		isDone:xxx,
	}
}
```

查看指定订单详情

```
GET/order?orderId=xxx
{
}
{
	orderId:xxx,
	userId:xxx,
	time:xxx,
	isDone:xxx,
	dishes:xxx
}
```

修改订单状态

```	
PUT/order?orderId=xxx&isDone=1
{
}
{
	ok:1,
	reason:xxx
}
```

# 6. API设计分析

## 6.1 用户操作

1、用户注册

构造注册用户名以及密码

由于用户名的唯一性，所以需要与已有用户名进行对比。

如果已经存在则注册成功，反之，则注册失败。

2、用户登录

构造登录用户名以及密码

先通过用户名查找用户是否存在，不存在则登录失败

用户存在则需要对比密码是否相同，不相同则登录失败

3、用户登出

首先判断是否存在session，如果不存在，说明没有登录

如果存在，就删除user

## 6.2 对菜品的管理

1、新增菜品

首先需要验证登录状态，并确定管理员的身份

具体步骤为判断session是否存在，判断user是否为非空，判断是否管理员身份

然后将名称以及价格构造菜品类然后加入数据库

2、删除菜品

首先需要验证登录状态，并确定管理员的身份

然后删除菜品

3、查看所有菜品

首先需要验证登录状态

然后调用查看所有菜品函数

## 6.3 对订单的管理

1、新增订单

前后传过来菜品的一系列id，然后将这些id的菜品加入order中，然后加入数据库

2、查看订单

验证登录状态

如果是管理员可以查看所有订单

如果是顾客就只能查看自己的订单

3、修改订单状态

验证身份是管理员

通过前端的页面来反馈该订单是完成还是未完成

# 7. BUG-FREE

## 7.1 添加菜品

在进行添加order_user时，在添加成功后，需要得到自动生成的`orderId`所以需要如下实现

```java
PreparedStatement statement =null;
ResultSet resultSet = null;
String sql = "xxxxxx";
try {
    assert connection != null;
    statement= connection.prepareStatement(sql);
    //下面是修改后
    //statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    statement.setInt(1,order.getUserId());
    int ret = statement.executeUpdate();
    if(ret!=0){
        resultSet=statement.getGeneratedKeys();
        if(resultSet.next()){
            order.setOrderId(resultSet.getInt(1));
        }
        System.out.println("插入订单第一步成功");
    }else{
        System.out.println("新增失败");
    }
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    DBUtil.close(connection,statement,resultSet);
}
```

执行后报错

```java
java.sql.SQLException: Generated keys not requested.
```

错误原因：
获取预编译块的时候未获取带有主键返回策略的预编译块
解决方式：
获取`preparedStatement`的时候多一个参数`Statement.RETURN_GENERATED_KEYS`就可以了

````java
statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
````

## 7.2 在订单完成后无法删除或者更新菜品

在已有订单选中菜品时，我们执行删除操作，**虽然在页面时提示成功**，但是在控制台查看提示

绑定外键后执行删除或者更新操作不可进行

那我们可以取消外键，但是我们在查看订单时也会消失不见。

我们就需要给菜品设定一个状态，表示现在存在，还是现在不存在。

不在执行删除数据库中的操作，这个在后续的优化中进行改进。

# 8. 代码优化

## 8.1 对繁琐代码进行优化

在我们执行删除操作时

```java
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="没有登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                response.ok=0;
                response.reason="没有登录";
                System.out.println("没有登录");
            }else{
                if(user.getIsAdmin()==0){
                    response.ok=0;
                    response.reason="不是管理员";
                    System.out.println("不是管理员");
                }else{
                    String dishStrId=req.getParameter("dishId");
                    System.out.println("字符串格式->"+dishStrId);
                    if(dishStrId==null){
                        response.ok=0;
                        response.reason="参数不正确";
                    }else{
                        int id=Integer.parseInt(dishStrId);
                        System.out.println("数字格式->"+dishStrId);
                        DishDao.delete(id);
                        response.ok=1;
                        response.reason="";
                    }
                }
            }
        }
        String jsonString = gson.toJson(response);
        resp.getWriter().write(jsonString);
    }
```

每一个情况我们都需要来写出response.ok=？和response.reason=？我们可以自定义一个异常，然后在catch中捕捉到异常后进行统一处理。

```java
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            if(user.getIsAdmin()==0){
                throw new OrderSystemException("您不是管理员");
            }
            String dishStrId=req.getParameter("dishId");
            System.out.println("字符串格式->"+dishStrId);
            if(dishStrId==null){
                throw new OrderSystemException("参数不正确");
            }
            int id=Integer.parseInt(dishStrId);
            System.out.println("数字格式->"+dishStrId);
            DishDao.delete(id);
            response.ok=1;
            response.reason="";
        }catch (Exception e){
            System.out.println("DishServlet_doDelete:");
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.ok=0;
            response.reason=e.getMessage();
        } finally {
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
```

如上改进后虽然代码没有减少太多

但是如果在dao层也使用异常抛出，那么我们在管理员页面进行删除的时候就可以提示删除失败

```
删除菜品失败! Cannot delete or update a parent row: a foreign key constraint fails (`order_system`.`order_dish`, CONSTRAINT `order_dish_ibfk_2` FOREIGN KEY (`dishId`) REFERENCES `dishes` (`dishId`))
```

而不是删除成功，但实际上并没有删除成功。

